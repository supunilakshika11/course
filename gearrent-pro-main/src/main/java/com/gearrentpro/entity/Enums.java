package com.gearrentpro.entity;

public class Enums {
    public enum Role { ADMIN, BRANCH_MANAGER, STAFF }
    public enum EquipmentStatus { AVAILABLE, RESERVED, RENTED, MAINTENANCE }
    public enum ReservationStatus { ACTIVE, CANCELLED, CONVERTED }
    public enum PaymentStatus { PAID, PARTIALLY_PAID, UNPAID }
    public enum RentalStatus { ACTIVE, RETURNED, OVERDUE, CANCELLED }
    public enum Membership { REGULAR, SILVER, GOLD }
}
