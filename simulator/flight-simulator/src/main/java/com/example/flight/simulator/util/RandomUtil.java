package com.example.flight.simulator.util;

public final class RandomUtil {

    private RandomUtil() {
    }

    public static <T> T cycle(T[] values, int index) {
        return values[Math.floorMod(index, values.length)];
    }
}
