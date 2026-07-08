package com.example.flight.simulator.util;

import com.example.flight.common.dto.PositionDto;
import com.example.flight.simulator.domain.Waypoint;

public final class GeoUtil {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private GeoUtil() {
    }

    public static PositionDto offset(PositionDto origin, double northMeters, double eastMeters, double altitude) {
        double deltaLatitude = northMeters / EARTH_RADIUS_METERS;
        double deltaLongitude = eastMeters / (EARTH_RADIUS_METERS * Math.cos(Math.toRadians(origin.latitude())));

        return new PositionDto(
                origin.latitude() + Math.toDegrees(deltaLatitude),
                origin.longitude() + Math.toDegrees(deltaLongitude),
                altitude
        );
    }

    public static double distanceMeters(Waypoint first, Waypoint second) {
        double lat1 = Math.toRadians(first.latitude());
        double lat2 = Math.toRadians(second.latitude());
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(second.longitude() - first.longitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    public static PositionDto interpolate(Waypoint first, Waypoint second, double ratio) {
        double clampedRatio = Math.max(0, Math.min(1, ratio));
        return new PositionDto(
                first.latitude() + (second.latitude() - first.latitude()) * clampedRatio,
                first.longitude() + (second.longitude() - first.longitude()) * clampedRatio,
                first.altitude() + (second.altitude() - first.altitude()) * clampedRatio
        );
    }
}
