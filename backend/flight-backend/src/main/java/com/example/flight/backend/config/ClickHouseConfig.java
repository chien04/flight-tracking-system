package com.example.flight.backend.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class ClickHouseConfig {

    @Bean
    public DataSource clickHouseDataSource(AppProperties appProperties) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        dataSource.setUrl(appProperties.getClickhouseUrl());
        dataSource.setUsername(appProperties.getClickhouseUsername());
        dataSource.setPassword(appProperties.getClickhousePassword());
        return dataSource;
    }

    @Bean
    public JdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickHouseDataSource") DataSource clickHouseDataSource
    ) {
        return new JdbcTemplate(clickHouseDataSource);
    }
}
