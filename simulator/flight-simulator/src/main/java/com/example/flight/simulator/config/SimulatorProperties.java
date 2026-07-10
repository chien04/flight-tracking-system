package com.example.flight.simulator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "simulator")
@Component("simulatorProperties")
public class SimulatorProperties {

    private int targetCount = 10000;
    private long updateIntervalMs = 300;
    private String kafkaTopic = "flight-target-updates";
    private double centerLatitude = 39.8283;
    private double centerLongitude = -98.5795;
    private double defaultAltitude = 1000;
    private double targetLatitudeSpanDegrees = 24;
    private double targetLongitudeSpanDegrees = 58;
    private boolean schedulingEnabled = true;

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public long getUpdateIntervalMs() {
        return updateIntervalMs;
    }

    public void setUpdateIntervalMs(long updateIntervalMs) {
        this.updateIntervalMs = updateIntervalMs;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public double getDefaultAltitude() {
        return defaultAltitude;
    }

    public void setDefaultAltitude(double defaultAltitude) {
        this.defaultAltitude = defaultAltitude;
    }

    public double getTargetLatitudeSpanDegrees() {
        return targetLatitudeSpanDegrees;
    }

    public void setTargetLatitudeSpanDegrees(double targetLatitudeSpanDegrees) {
        this.targetLatitudeSpanDegrees = targetLatitudeSpanDegrees;
    }

    public double getTargetLongitudeSpanDegrees() {
        return targetLongitudeSpanDegrees;
    }

    public void setTargetLongitudeSpanDegrees(double targetLongitudeSpanDegrees) {
        this.targetLongitudeSpanDegrees = targetLongitudeSpanDegrees;
    }

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    public void setSchedulingEnabled(boolean schedulingEnabled) {
        this.schedulingEnabled = schedulingEnabled;
    }
}
