package com.gearrentpro.entity;

import java.math.BigDecimal;

public record Equipment(
        String equipmentId,
        int categoryId,
        int branchId,
        String brand,
        String model,
        int purchaseYear,
        BigDecimal baseDailyPrice,
        BigDecimal securityDeposit,
        Enums.EquipmentStatus status
) {}
