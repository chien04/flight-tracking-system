package com.example.flight.common.util;

import java.util.Locale;

public final class TargetIdUtil {

    public static final int MIN_TARGET_ID = 0;
    public static final int MAX_TARGET_ID = 9999;

    private TargetIdUtil() {
    }

    public static String format(int id) {
        if (id < MIN_TARGET_ID || id > MAX_TARGET_ID) {
            throw new IllegalArgumentException("Target ID must be between 0000 and 9999");
        }

        return String.format(Locale.ROOT, "%04d", id);
    }
}
