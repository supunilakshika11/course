package com.gearrentpro.service;

import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.RentalQuote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class PricingService {

    public RentalQuote quote(Category cat,
                             java.math.BigDecimal equipmentBaseDaily,
                             java.math.BigDecimal deposit,
                             Customer customer,
                             BigDecimal membershipDiscountPercent,
                             BigDecimal longRentalDiscountPercent,
                             LocalDate start,
                             LocalDate end) {

        long days = Validation.validateDateRange(start, end);

        BigDecimal rentalAmount = BigDecimal.ZERO;

        LocalDate d = start;
        for (int i = 0; i < days; i++) {
            boolean weekend = (d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY);

            BigDecimal daily = equipmentBaseDaily.multiply(cat.basePriceFactor());
            if (weekend) daily = daily.multiply(cat.weekendMultiplier());

            rentalAmount = rentalAmount.add(daily);
            d = d.plusDays(1);
        }

        rentalAmount = rentalAmount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal membershipDiscount = rentalAmount
                .multiply(membershipDiscountPercent)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        BigDecimal longDiscount = BigDecimal.ZERO;
        if (days >= 7) {
            longDiscount = rentalAmount
                    .multiply(longRentalDiscountPercent)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        BigDecimal finalPayable = rentalAmount.subtract(membershipDiscount).subtract(longDiscount);

        return new RentalQuote(
                rentalAmount,
                membershipDiscount,
                longDiscount,
                finalPayable.max(BigDecimal.ZERO),
                deposit
        );
    }
}
