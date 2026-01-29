package com.gearrentpro.controller;

import com.gearrentpro.service.ReturnService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnController {

    private final ReturnService returnService = new ReturnService();

    public ReturnService.Settlement previewReturn(int rentalId,
                                                  LocalDate actual,
                                                  String damageDesc,
                                                  BigDecimal damageCharge) {
        try {
            return returnService.previewSettlement(
                    rentalId,
                    actual,
                    damageDesc,
                    damageCharge
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void confirmReturn(ReturnService.Settlement s) {
        try {
            returnService.finalizeReturn(s);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

