package com.foodbev.FoodBevApp.service.payment;

import com.foodbev.FoodBevApp.dto.payment.MidtransNotificationRequest;
import com.foodbev.FoodBevApp.dto.payment.PaymentResponse;
import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.payment.Payment;

public interface PaymentService {

    /**
     * @param order
     * @return
     */
    PaymentResponse createPayment(Order order);

    /**
     * @param orderId 
     * @return 
     */
    Payment getPaymentByOrderId(Long orderId);

    /**
    
     * @param midtransOrderId 
     * @return
     */
    Payment getPaymentByMidtransOrderId(String midtransOrderId);

    /**
     * @param notification 
     */
    void handleNotification(MidtransNotificationRequest notification);

    /**
    
     * @param midtransOrderId 
     * @return 
     */
    PaymentResponse checkPaymentStatus(String midtransOrderId);

    /**
     
     * @param orderId 
     * @return 
     */
    boolean cancelPayment(Long orderId);

    /**
     * @param notification 
     * @return
     */
    boolean verifySignature(MidtransNotificationRequest notification);

    /**
     
     * @param payment 
     * @return
     */
    PaymentResponse toPaymentResponse(Payment payment);
}
