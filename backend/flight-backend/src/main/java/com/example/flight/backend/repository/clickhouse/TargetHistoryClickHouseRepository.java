package com.example.flight.backend.repository.clickhouse;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.response.TargetHistoryPointResponse;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateEvent;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TargetHistoryClickHouseRepository {

    private static final String TABLE_NAME = "target_position_history";
    private static final String INSERT_SQL = """
            INSERT INTO target_position_history
                (target_id, timestamp, ingested_at, latitude, longitude, altitude, classification)
            VALUES (?, fromUnixTimestamp64Milli(?), fromUnixTimestamp64Milli(?), ?, ?, ?, ?)
            """;
    private static final String RAW_HISTORY_SQL = """
            SELECT
                target_id,
                toUnixTimestamp64Milli(timestamp) AS timestamp_ms,
                toUnixTimestamp64Milli(ingested_at) AS ingested_at_ms,
                latitude,
                longitude,
                altitude,
                classification
            FROM target_position_history
            WHERE target_id = ?
                AND toUnixTimestamp64Milli(timestamp) >= ?
                AND toUnixTimestamp64Milli(timestamp) <= ?
            ORDER BY timestamp
            """;
    private static final String SAMPLED_HISTORY_SQL = """
            SELECT
                target_id,
                toUnixTimestamp64Milli(min(timestamp)) AS timestamp_ms,
                toUnixTimestamp64Milli(argMin(ingested_at, timestamp)) AS ingested_at_ms,
                argMin(latitude, timestamp) AS latitude,
                argMin(longitude, timestamp) AS longitude,
                argMin(altitude, timestamp) AS altitude,
                argMin(classification, timestamp) AS classification
            FROM target_position_history
            WHERE target_id = ?
                AND toUnixTimestamp64Milli(timestamp) >= ?
                AND toUnixTimestamp64Milli(timestamp) <= ?
            GROUP BY target_id, intDiv(toUnixTimestamp64Milli(timestamp), ?)
            ORDER BY timestamp_ms
            """;
    private static final RowMapper<TargetHistoryPointResponse> HISTORY_ROW_MAPPER = (resultSet, rowNum) ->
            new TargetHistoryPointResponse(
                    resultSet.getString("target_id"),
                    resultSet.getDouble("latitude"),
                    resultSet.getDouble("longitude"),
                    resultSet.getDouble("altitude"),
                    TargetClassification.valueOf(resultSet.getString("classification")),
                    resultSet.getLong("timestamp_ms"),
                    resultSet.getLong("ingested_at_ms")
            );

    private final JdbcTemplate jdbcTemplate;
    private final AppProperties appProperties;

    public TargetHistoryClickHouseRepository(
            @Qualifier("clickHouseJdbcTemplate") JdbcTemplate jdbcTemplate,
            AppProperties appProperties
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.appProperties = appProperties;
    }

    public void createTableIfNotExists() {
        int ttlDays = Math.max(1, appProperties.getHistoryTtlDays());
        String sql = """
                CREATE TABLE IF NOT EXISTS %s
                (
                    target_id String,
                    timestamp DateTime64(3),
                    ingested_at DateTime64(3) DEFAULT timestamp,
                    latitude Float64,
                    longitude Float64,
                    altitude Float64,
                    classification LowCardinality(String)
                )
                ENGINE = MergeTree
                PARTITION BY toDate(timestamp)
                ORDER BY (target_id, timestamp)
                TTL toDateTime(timestamp) + INTERVAL %d DAY
                """.formatted(TABLE_NAME, ttlDays);

        jdbcTemplate.execute(sql);
        jdbcTemplate.execute("""
                ALTER TABLE target_position_history
                ADD COLUMN IF NOT EXISTS ingested_at DateTime64(3) DEFAULT timestamp AFTER timestamp
                """);
    }

    public void saveBatch(List<TargetUpdateEvent> targets) {
        if (targets.isEmpty()) {
            return;
        }

        long ingestedAt = System.currentTimeMillis();
        jdbcTemplate.batchUpdate(INSERT_SQL, targets, targets.size(), (preparedStatement, target) -> {
            preparedStatement.setString(1, target.targetId());
            preparedStatement.setLong(2, target.timestamp());
            preparedStatement.setLong(3, ingestedAt);
            preparedStatement.setDouble(4, target.latitude());
            preparedStatement.setDouble(5, target.longitude());
            preparedStatement.setDouble(6, target.altitude());
            preparedStatement.setString(7, target.classification().name());
        });
    }

    public List<TargetHistoryPointResponse> findHistory(
            String targetId,
            Instant from,
            Instant to,
            long sampleMs
    ) {
        long fromMillis = from.toEpochMilli();
        long toMillis = to.toEpochMilli();
        if (sampleMs == 0) {
            return jdbcTemplate.query(
                    RAW_HISTORY_SQL,
                    HISTORY_ROW_MAPPER,
                    targetId,
                    fromMillis,
                    toMillis
            );
        }

        return jdbcTemplate.query(
                SAMPLED_HISTORY_SQL,
                HISTORY_ROW_MAPPER,
                targetId,
                fromMillis,
                toMillis,
                sampleMs
        );
    }
}
