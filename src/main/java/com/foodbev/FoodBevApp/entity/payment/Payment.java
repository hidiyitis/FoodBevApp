package com.foodbev.FoodBevApp.entity.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.foodbev.FoodBevApp.entity.order.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "midtrans_order_id", unique = true)
    private String midtransOrderId;

    @Column(name = "snap_token")
    private String snapToken;

    @Column(name = "redirect_url", length = 500)
    private String redirectUrl;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "fraud_status")
    private String fraudStatus;

    @Column(name = "va_number")
    private String vaNumber;

    @Column(name = "bank")
    private String bank;

    @Column(name = "payment_code")
    private String paymentCode;

    @Column(name = "bill_key")
    private String billKey;

    @Column(name = "biller_code")
    private String billerCode;

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "settlement_time")
    private LocalDateTime settlementTime;

    @Column(name = "midtrans_response", columnDefinition = "TEXT")
    private String midtransResponse;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentStatus {
        PENDING,
        CAPTURE,
        SETTLEMENT,
        DENY,
        CANCEL,
        EXPIRE,
        REFUND,
        PARTIAL_REFUND,
        AUTHORIZE,
        FAILURE
    }
}
