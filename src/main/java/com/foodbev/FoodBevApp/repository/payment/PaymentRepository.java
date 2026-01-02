package com.foodbev.FoodBevApp.repository.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.payment.Payment;
import com.foodbev.FoodBevApp.entity.payment.Payment.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrder(Order order);
    
    Optional<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByMidtransOrderId(String midtransOrderId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByOrderUserIdOrderByCreatedAtDesc(Long userId);
    
    boolean existsByOrderId(Long orderId);
}
