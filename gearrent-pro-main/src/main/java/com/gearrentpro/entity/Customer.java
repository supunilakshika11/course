package com.gearrentpro.entity;

public record Customer(
        int id,
        String name,
        String nicPassport,
        String contact,
        String email,
        String address,
        Enums.Membership membership
) {}
