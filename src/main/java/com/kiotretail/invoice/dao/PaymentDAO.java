package com.kiotretail.invoice.dao;

import com.kiotretail.invoice.model.Payment;
import com.kiotretail.shared.base.BaseDAO;
import com.kiotretail.shared.constant.AppConstants;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Payments tied to an order.
 *
 * Table: Payments (PaymentsID INT PK IDENTITY, OrderID INT FK, PaymentMethod VARCHAR,
 *                  Amount DECIMAL(18,2), PaidAt DATETIME, Reference NVARCHAR,
 *                  Status VARCHAR DEFAULT 'pending', CreatedAt DATETIME DEFAULT NOW())
 *
 * All statements are parameterized PreparedStatements.
 */
public class PaymentDAO extends BaseDAO {

    private static final String BASE_SELECT =
            "SELECT PaymentsID, OrderID, PaymentMethod, Amount, PaidAt, Reference, Status, CreatedAt " +
            "FROM Payments ";

    /**
     * Fetch every payment recorded against the given order, oldest first.
     */
    public List<Payment> getByOrderId(int orderId) {
        List<Payment> payments = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE OrderID = ? ORDER BY PaymentsID ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(extractPayment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    /**
     * Insert a payment row. CreatedAt is left to the database default.
     *
     * @return true when exactly one row was inserted
     */
    public boolean insert(Payment payment) {
        if (payment == null) {
            return false;
        }
        String sql = "INSERT INTO Payments (OrderID, PaymentMethod, Amount, PaidAt, Reference, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setString(2, payment.getPaymentMethod());
            stmt.setBigDecimal(3, payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO);
            stmt.setTimestamp(4, payment.getPaidAt());
            stmt.setString(5, payment.getReference());
            stmt.setString(6, payment.getStatus() != null ? payment.getStatus() : AppConstants.STATUS_PENDING);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update the status column on an existing payment (e.g. pending -> completed).
     */
    public boolean updateStatus(int paymentId, String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE Payments SET Status = ? WHERE PaymentsID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.trim());
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Payment extractPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("PaymentsID"));
        payment.setOrderId(rs.getInt("OrderID"));
        payment.setPaymentMethod(rs.getString("PaymentMethod"));
        payment.setAmount(rs.getBigDecimal("Amount"));
        payment.setPaidAt(rs.getTimestamp("PaidAt"));
        payment.setReference(rs.getString("Reference"));
        payment.setStatus(rs.getString("Status"));
        payment.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return payment;
    }
}
