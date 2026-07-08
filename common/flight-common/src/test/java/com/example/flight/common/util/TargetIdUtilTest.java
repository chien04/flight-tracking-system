package com.example.flight.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TargetIdUtilTest {

    @Test
    void formatsIdsAsFourDigits() {
        assertEquals("0000", TargetIdUtil.format(0));
        assertEquals("0007", TargetIdUtil.format(7));
        assertEquals("0123", TargetIdUtil.format(123));
        assertEquals("9999", TargetIdUtil.format(9999));
    }

    @Test
    void rejectsIdsBelowRange() {
        assertThrows(IllegalArgumentException.class, () -> TargetIdUtil.format(-1));
    }

    @Test
    void rejectsIdsAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> TargetIdUtil.format(10000));
    }
}
