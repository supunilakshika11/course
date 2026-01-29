package com.gearrentpro.entity;

public record UserSession(
        int userId,
        String username,
        Enums.Role role,
        Integer branchId
) {}
