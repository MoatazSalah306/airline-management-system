package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

import util.*;
public class Payment {
    private int id;
    private int userId;
    private double paymentAmount;
    private PaymentStatus paymentState;
    private PaymentMethod paymentMethod;
    private Date paymentDate;
    private Timestamp updatedAt;

    public Payment(double paymentAmount, PaymentMethod paymentMethod, Date paymentDate) {
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.paymentState = PaymentStatus.UNPAID;
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO payment (user_id, payment_amount, payment_state, payment_method, payment_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, paymentAmount);
            stmt.setString(3, paymentState.name());
            stmt.setString(4, paymentMethod.name());
            stmt.setDate(5, paymentDate);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Payment load(int id) throws SQLException {
        String sql = "SELECT * FROM payment WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment(
                        rs.getDouble("payment_amount"),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        rs.getDate("payment_date")
                    );
                    payment.setId(id);
                    payment.setUserId(rs.getInt("user_id"));
                    payment.setPaymentState(PaymentStatus.valueOf(rs.getString("payment_state").toUpperCase()));
                    payment.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return payment;
                }
                throw new SQLException("Payment not found");
            }
        }
    }
    public static Payment loadByReservationId(int ReservationId) throws SQLException {
        String sql = "SELECT * FROM payment WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,ReservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment(
                        rs.getDouble("payment_amount"),
                        PaymentMethod.valueOf(rs.getString("payment_method")),
                        rs.getDate("payment_date")
                    );
                    int id= rs.getInt("id");
                    int user_id= rs.getInt("user_id");
                    payment.setId(id);
                    payment.setUserId(user_id);
                    payment.setPaymentState(PaymentStatus.valueOf(rs.getString("payment_state").toUpperCase()));
                    payment.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return payment;
                }
                throw new SQLException("Payment not found");
            }
        }
    }
    

    public boolean makeTransaction() throws SQLException {
        this.paymentState = PaymentStatus.COMPLETED;
        String sql = "UPDATE payment SET payment_state = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentState.name());
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean update() throws SQLException {
        String sql = "UPDATE payment SET payment_state = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentState.name());
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }
    public PaymentStatus getPaymentState() { return paymentState; }
    public void setPaymentState(PaymentStatus paymentState) { this.paymentState = paymentState; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}