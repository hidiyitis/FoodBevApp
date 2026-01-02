package com.foodbev.FoodBevApp.controller.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodbev.FoodBevApp.dto.payment.MidtransNotificationRequest;
import com.foodbev.FoodBevApp.dto.payment.PaymentResponse;
import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.payment.Payment;
import com.foodbev.FoodBevApp.entity.user.User;
import com.foodbev.FoodBevApp.repository.user.UserRepository;
import com.foodbev.FoodBevApp.service.order.OrderService;
import com.foodbev.FoodBevApp.service.payment.PaymentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @Value("${midtrans.client-key}")
    private String clientKey;

    @Value("${midtrans.is-production:false}")
    private boolean isProduction;

    /**
     * Show payment page with Midtrans Snap
     */
    @GetMapping({"/payment/{orderId}", "/user/order/payment/{orderId}"})
    public String showPaymentPage(@PathVariable Long orderId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(orderId, user);
        PaymentResponse payment = paymentService.createPayment(order);

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("clientKey", clientKey);
        model.addAttribute("isProduction", isProduction);

        return "order/payment";
    }

    /**
     * Create payment and get Snap token (AJAX)
     */
    @PostMapping("/payment/create/{orderId}")
    @ResponseBody
    public ResponseEntity<PaymentResponse> createPayment(@PathVariable Long orderId,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(orderId, user);
        PaymentResponse payment = paymentService.createPayment(order);

        return ResponseEntity.ok(payment);
    }

    /**
     * Handle Midtrans notification webhook
     */
    @PostMapping("/payment/notification")
    @ResponseBody
    public ResponseEntity<String> handleNotification(@RequestBody MidtransNotificationRequest notification) {
        log.info("Received Midtrans notification: orderId={}, status={}", 
                notification.getOrderId(), notification.getTransactionStatus());

        try {
            paymentService.handleNotification(notification);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Failed to handle notification", e);
            return ResponseEntity.badRequest().body("Failed to process notification");
        }
    }

    /**
     * Check payment status (AJAX)
     */
    @GetMapping("/payment/status/{orderId}")
    @ResponseBody
    public ResponseEntity<PaymentResponse> checkStatus(@PathVariable Long orderId) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            PaymentResponse response = paymentService.checkPaymentStatus(payment.getMidtransOrderId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to check payment status", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Payment finish callback page - Success
     * Also syncs payment status from Midtrans API since webhook may not reach localhost
     */
    @GetMapping("/payment/finish")
    public String paymentFinish(@RequestParam(required = false) String order_id,
                                @RequestParam(required = false) String status_code,
                                @RequestParam(required = false) String transaction_status,
                                Model model) {
        log.info("Payment finish callback: orderId={}, statusCode={}, transactionStatus={}", 
                order_id, status_code, transaction_status);

        try {
            // Sync payment status from Midtrans API (important for localhost without webhook)
            PaymentResponse paymentResponse = paymentService.checkPaymentStatus(order_id);
            
            Payment payment = paymentService.getPaymentByMidtransOrderId(order_id);
            model.addAttribute("payment", paymentResponse);
            model.addAttribute("order", payment.getOrder());
            model.addAttribute("success", true);
            
            // Redirect to success page for successful payment
            return "payment/success";
        } catch (Exception e) {
            log.error("Error in payment finish", e);
            model.addAttribute("success", false);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat memproses pembayaran");
            return "payment/error";
        }
    }

    /**
     * Payment pending callback page
     * Also syncs payment status from Midtrans API
     */
    @GetMapping("/payment/pending")
    public String paymentPending(@RequestParam(required = false) String order_id,
                                 Model model) {
        log.info("Payment pending callback: orderId={}", order_id);

        try {
            // Sync payment status from Midtrans API
            PaymentResponse paymentResponse = paymentService.checkPaymentStatus(order_id);
            
            Payment payment = paymentService.getPaymentByMidtransOrderId(order_id);
            model.addAttribute("payment", paymentResponse);
            model.addAttribute("order", payment.getOrder());
        } catch (Exception e) {
            log.error("Error in payment pending", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan");
        }

        return "payment/pending";
    }

    /**
     * Payment error callback page
     */
    @GetMapping("/payment/error")
    public String paymentError(@RequestParam(required = false) String order_id,
                               Model model) {
        log.info("Payment error callback: orderId={}", order_id);

        try {
            if (order_id != null) {
                Payment payment = paymentService.getPaymentByMidtransOrderId(order_id);
                model.addAttribute("payment", paymentService.toPaymentResponse(payment));
                model.addAttribute("order", payment.getOrder());
            }
        } catch (Exception e) {
            log.error("Error in payment error callback", e);
        }

        model.addAttribute("errorMessage", "Pembayaran gagal atau dibatalkan");

        return "payment/error";
    }

    /**
     * Cancel payment
     */
    @PostMapping("/payment/cancel/{orderId}")
    @ResponseBody
    public ResponseEntity<String> cancelPayment(@PathVariable Long orderId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify order belongs to user
        orderService.getOrderById(orderId, user);

        boolean cancelled = paymentService.cancelPayment(orderId);
        
        if (cancelled) {
            return ResponseEntity.ok("Payment cancelled");
        } else {
            return ResponseEntity.badRequest().body("Failed to cancel payment");
        }
    }
}
