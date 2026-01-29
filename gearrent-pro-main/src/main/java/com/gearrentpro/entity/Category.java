package com.gearrentpro.entity;

import java.math.BigDecimal;

public record Category(
        int id,
        String name,
        String description,
        BigDecimal basePriceFactor,
        BigDecimal weekendMultiplier,
        BigDecimal lateFeePerDay,
        boolean active
) {}
