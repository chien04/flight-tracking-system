package com.example.flight.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "flight")
@Component
public class AppProperties {

    private boolean kafkaConsumerEnabled = true;
    private boolean historyEnabled = true;
    private String kafkaTopic = "flight-target-updates";
    private String websocketTopic = "/topic/targets/realtime";
    private String redisCurrentStateKey = "target:current:all";
    private int historyTtlDays = 7;
    private long performanceTargetCycleMs = 300;
    private String clickhouseUrl = "jdbc:clickhouse://localhost:8123/flight_tracking";
    private String clickhouseUsername = "flight";
    private String clickhousePassword = "flight";

    public boolean isKafkaConsumerEnabled() {
        return kafkaConsumerEnabled;
    }

    public void setKafkaConsumerEnabled(boolean kafkaConsumerEnabled) {
        this.kafkaConsumerEnabled = kafkaConsumerEnabled;
    }

    public boolean isHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getWebsocketTopic() {
        return websocketTopic;
    }

    public void setWebsocketTopic(String websocketTopic) {
        this.websocketTopic = websocketTopic;
    }

    public String getRedisCurrentStateKey() {
        return redisCurrentStateKey;
    }

    public void setRedisCurrentStateKey(String redisCurrentStateKey) {
        this.redisCurrentStateKey = redisCurrentStateKey;
    }

    public int getHistoryTtlDays() {
        return historyTtlDays;
    }

    public void setHistoryTtlDays(int historyTtlDays) {
        this.historyTtlDays = historyTtlDays;
    }

    public long getPerformanceTargetCycleMs() {
        return performanceTargetCycleMs;
    }

    public void setPerformanceTargetCycleMs(long performanceTargetCycleMs) {
        this.performanceTargetCycleMs = performanceTargetCycleMs;
    }

    public String getClickhouseUrl() {
        return clickhouseUrl;
    }

    public void setClickhouseUrl(String clickhouseUrl) {
        this.clickhouseUrl = clickhouseUrl;
    }

    public String getClickhouseUsername() {
        return clickhouseUsername;
    }

    public void setClickhouseUsername(String clickhouseUsername) {
        this.clickhouseUsername = clickhouseUsername;
    }

    public String getClickhousePassword() {
        return clickhousePassword;
    }

    public void setClickhousePassword(String clickhousePassword) {
        this.clickhousePassword = clickhousePassword;
    }
}
