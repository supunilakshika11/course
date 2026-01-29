package com.gearrentpro.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Validation {
    private Validation(){}

    public static void require(boolean ok, String msg) {
        if (!ok) throw new IllegalArgumentException(msg);
    }

    public static long validateDateRange(LocalDate start, LocalDate end) {
        require(start != null && end != null, "Start/End dates required");
        require(!end.isBefore(start), "End date must be >= start date");
        long days = ChronoUnit.DAYS.between(start, end) + 1; // inclusive
        require(days <= 30, "Max rental/reservation duration is 30 days");
        return days;
    }
}
