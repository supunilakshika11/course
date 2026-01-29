package com.gearrentpro.entity;

import java.math.BigDecimal;

public record RentalQuote(
        BigDecimal rentalAmount,
        BigDecimal membershipDiscount,
        BigDecimal longRentalDiscount,
        BigDecimal finalPayable,
        BigDecimal securityDeposit
) {}
