package com.example.flight.backend.util;

import java.time.Duration;
import java.util.regex.Pattern;

public final class TimeRangeValidator {

    private static final Pattern TARGET_ID_PATTERN = Pattern.compile("\\d{4}");
    private static final long MAX_RANGE_MILLIS = Duration.ofDays(7).toMillis();

    private TimeRangeValidator() {
    }

    public static void validateTargetId(String targetId) {
        if (targetId == null || !TARGET_ID_PATTERN.matcher(targetId).matches()) {
            throw new IllegalArgumentException("Target ID must be exactly 4 digits");
        }
    }

    public static void validateRange(long from, long to) {
        if (from <= 0 || to <= 0) {
            throw new IllegalArgumentException("History time range must use epoch milliseconds");
        }
        if (from > to) {
            throw new IllegalArgumentException("History 'from' must be less than or equal to 'to'");
        }
        if (to - from > MAX_RANGE_MILLIS) {
            throw new IllegalArgumentException("History time range must not exceed 7 days");
        }
    }

    public static void validateSampleMs(long sampleMs) {
        if (sampleMs < 0) {
            throw new IllegalArgumentException("sampleMs must be greater than or equal to 0");
        }
    }
}
