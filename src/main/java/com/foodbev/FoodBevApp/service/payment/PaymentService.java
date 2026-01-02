package com.foodbev.FoodBevApp.service.payment;

import com.foodbev.FoodBevApp.dto.payment.MidtransNotificationRequest;
import com.foodbev.FoodBevApp.dto.payment.PaymentResponse;
import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.payment.Payment;

public interface PaymentService {

    /**
     * Create a new payment and get Midtrans Snap token
     * @param order the order to create payment for
     * @return PaymentResponse with snap token and redirect URL
     */
    PaymentResponse createPayment(Order order);

    /**
     * Get payment by order ID
     * @param orderId the order ID
     * @return Payment entity
     */
    Payment getPaymentByOrderId(Long orderId);

    /**
     * Get payment by Midtrans order ID
     * @param midtransOrderId the Midtrans order ID
     * @return Payment entity
     */
    Payment getPaymentByMidtransOrderId(String midtransOrderId);

    /**
     * Handle notification callback from Midtrans
     * @param notification the notification request from Midtrans
     */
    void handleNotification(MidtransNotificationRequest notification);

    /**
     * Check payment status from Midtrans API
     * @param midtransOrderId the Midtrans order ID
     * @return PaymentResponse with current status
     */
    PaymentResponse checkPaymentStatus(String midtransOrderId);

    /**
     * Cancel a pending payment
     * @param orderId the order ID
     * @return true if cancellation successful
     */
    boolean cancelPayment(Long orderId);

    /**
     * Verify signature from Midtrans notification
     * @param notification the notification to verify
     * @return true if signature is valid
     */
    boolean verifySignature(MidtransNotificationRequest notification);

    /**
     * Convert Payment entity to PaymentResponse DTO
     * @param payment the payment entity
     * @return PaymentResponse DTO
     */
    PaymentResponse toPaymentResponse(Payment payment);
}
