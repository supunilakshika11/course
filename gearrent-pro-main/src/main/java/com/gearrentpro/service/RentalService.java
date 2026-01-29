package com.gearrentpro.service;

import com.gearrentpro.dao.*;
import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.Equipment;
import com.gearrentpro.entity.RentalQuote;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalService {
    private final EquipmentDao equipmentDao = new EquipmentDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final AvailabilityDao availabilityDao = new AvailabilityDao();
    private final RentalDao rentalDao = new RentalDao();
    private final ReservationDao reservationDao = new ReservationDao();
    private final ConfigDao configDao = new ConfigDao();
    private final PricingService pricingService = new PricingService();

    public RentalQuote calculateQuote(java.sql.Connection con, String equipmentId, int customerId, LocalDate start, LocalDate end) throws Exception {
        Equipment eq = equipmentDao.findById(con, equipmentId);
        Validation.require(eq != null, "Invalid equipment");
        Category cat = categoryDao.findById(con, eq.categoryId());
        Validation.require(cat != null && cat.active(), "Invalid/Inactive category");

        Customer cus = customerDao.findById(con, customerId);
        Validation.require(cus != null, "Invalid customer");

        BigDecimal mPct = customerDao.membershipDiscountPercent(con, cus.membership());
        BigDecimal longPct = configDao.getDecimal(con, "LONG_RENTAL_DISCOUNT_PERCENT");

        return pricingService.quote(cat, eq.baseDailyPrice(), eq.securityDeposit(), cus, mPct, longPct, start, end);
    }

    
    public int createRental(java.sql.Connection con, int branchId, String equipmentId, int customerId,
                            LocalDate start, LocalDate end, Enums.PaymentStatus payStatus) throws Exception {

        // validate 30 days
        Validation.validateDateRange(start, end);

        // overlap check
        Validation.require(!availabilityDao.hasOverlap(con, equipmentId, start, end),
                "This equipment has overlapping reservation/rental for given dates");

        Equipment eq = equipmentDao.findById(con, equipmentId);
        Validation.require(eq != null, "Invalid equipment");
        Validation.require(eq.branchId() == branchId, "Equipment does not belong to this branch");

        // deposit limit check
        BigDecimal depositLimit = configDao.getDecimal(con, "DEPOSIT_LIMIT");
        BigDecimal activeDeposits = customerDao.activeDepositsSum(con, customerId);
        BigDecimal newTotal = activeDeposits.add(eq.securityDeposit());
        Validation.require(newTotal.compareTo(depositLimit) <= 0,
                "Deposit limit exceeded for customer. Active deposits: " + activeDeposits + ", new total: " + newTotal);

        // quote
        RentalQuote q = calculateQuote(con, equipmentId, customerId, start, end);

        // concurrency-safe status change (AVAILABLE -> RENTED)
        equipmentDao.updateStatusIf(con, equipmentId, Enums.EquipmentStatus.AVAILABLE, Enums.EquipmentStatus.RENTED);

        // insert rental
        return rentalDao.insertRental(con, equipmentId, customerId, branchId, start, end, q, payStatus, null);
    }


    public int convertReservationToRental(java.sql.Connection con, int reservationId, Enums.PaymentStatus payStatus) throws Exception {
        var res = reservationDao.find(con, reservationId);
        Validation.require(res != null, "Invalid reservation");
        Validation.require(res.status() == Enums.ReservationStatus.ACTIVE, "Reservation not ACTIVE");

        // validate again availability 
        Validation.require(!availabilityDao.hasOverlap(con, res.equipmentId(), res.start(), res.end()),
                "Equipment not available anymore for those dates");

        // create rental under tx
        int rentalId = createRental(con, res.branchId(), res.equipmentId(), res.customerId(), res.start(), res.end(), payStatus);

        // mark reservation converted
        reservationDao.updateStatus(con, reservationId, Enums.ReservationStatus.CONVERTED);

        // link rental with reservation
       
        try (var ps = con.prepareStatement("UPDATE rental SET reservation_id=? WHERE rental_id=?")) {
            ps.setInt(1, reservationId);
            ps.setInt(2, rentalId);
            ps.executeUpdate();
        }

        return rentalId;
    }

    public void refreshOverdues(java.sql.Connection con) throws Exception {
        rentalDao.markOverdue(con);
    }
}
