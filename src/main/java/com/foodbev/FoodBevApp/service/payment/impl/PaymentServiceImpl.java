package com.foodbev.FoodBevApp.service.payment.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodbev.FoodBevApp.dto.payment.MidtransNotificationRequest;
import com.foodbev.FoodBevApp.dto.payment.MidtransSnapRequest;
import com.foodbev.FoodBevApp.dto.payment.MidtransSnapResponse;
import com.foodbev.FoodBevApp.dto.payment.PaymentResponse;
import com.foodbev.FoodBevApp.entity.order.Order;
import com.foodbev.FoodBevApp.entity.order.OrderItem;
import com.foodbev.FoodBevApp.entity.payment.Payment;
import com.foodbev.FoodBevApp.entity.payment.Payment.PaymentStatus;
import com.foodbev.FoodBevApp.repository.order.OrderRepository;
import com.foodbev.FoodBevApp.repository.payment.PaymentRepository;
import com.foodbev.FoodBevApp.service.payment.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${midtrans.server-key}")
    private String serverKey;

    @Value("${midtrans.client-key}")
    private String clientKey;

    @Value("${midtrans.is-production:false}")
    private boolean isProduction;

    @Value("${midtrans.snap-url:https://app.sandbox.midtrans.com/snap/v1/transactions}")
    private String snapUrl;

    @Value("${midtrans.api-url:https://api.sandbox.midtrans.com/v2}")
    private String apiUrl;

    @Value("${app.base-url:http://localhost:8081}")
    private String appBaseUrl;

    @Override
    @Transactional
    public PaymentResponse createPayment(Order order) {
        // Check if payment already exists for this order
        if (paymentRepository.existsByOrderId(order.getId())) {
            Payment existingPayment = paymentRepository.findByOrderId(order.getId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            // If pending, return existing payment
            if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                return toPaymentResponse(existingPayment);
            }
        }

        // Generate unique Midtrans order ID
        String midtransOrderId = "ORDER-" + order.getId() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Build Snap request
        MidtransSnapRequest snapRequest = buildSnapRequest(order, midtransOrderId);

        // Call Midtrans Snap API
        MidtransSnapResponse snapResponse = callMidtransSnapApi(snapRequest);

        // Create payment entity
        Payment payment = Payment.builder()
                .order(order)
                .midtransOrderId(midtransOrderId)
                .snapToken(snapResponse.getToken())
                .redirectUrl(snapResponse.getRedirectUrl())
                .grossAmount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        try {
            payment.setMidtransResponse(objectMapper.writeValueAsString(snapResponse));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize Midtrans response", e);
        }

        payment = paymentRepository.save(payment);

        log.info("Payment created for order {} with Midtrans order ID: {}", order.getId(), midtransOrderId);

        return toPaymentResponse(payment);
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    @Override
    public Payment getPaymentByMidtransOrderId(String midtransOrderId) {
        return paymentRepository.findByMidtransOrderId(midtransOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for Midtrans order: " + midtransOrderId));
    }

    @Override
    @Transactional
    public void handleNotification(MidtransNotificationRequest notification) {
        log.info("Received Midtrans notification for order: {}, status: {}", 
                notification.getOrderId(), notification.getTransactionStatus());

        // Verify signature
        if (!verifySignature(notification)) {
            log.error("Invalid signature for order: {}", notification.getOrderId());
            throw new RuntimeException("Invalid Midtrans signature");
        }

        Payment payment = getPaymentByMidtransOrderId(notification.getOrderId());
        
        // Update payment status based on transaction status
        PaymentStatus newStatus = mapTransactionStatus(notification.getTransactionStatus());
        payment.setStatus(newStatus);
        payment.setTransactionId(notification.getTransactionId());
        payment.setPaymentType(notification.getPaymentType());
        payment.setFraudStatus(notification.getFraudStatus());

        // Set VA number if available
        if (notification.getVaNumbers() != null && notification.getVaNumbers().length > 0) {
            payment.setVaNumber(notification.getVaNumbers()[0].getVaNumber());
            payment.setBank(notification.getVaNumbers()[0].getBank());
        }

        // Set Mandiri Bill info
        if (notification.getBillKey() != null) {
            payment.setBillKey(notification.getBillKey());
            payment.setBillerCode(notification.getBillerCode());
        }

        // Set settlement time if available
        if (notification.getSettlementTime() != null) {
            payment.setSettlementTime(parseDateTime(notification.getSettlementTime()));
        }

        // Set expiry time if available
        if (notification.getExpiryTime() != null) {
            payment.setExpiryTime(parseDateTime(notification.getExpiryTime()));
        }

        try {
            payment.setMidtransResponse(objectMapper.writeValueAsString(notification));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize notification", e);
        }

        paymentRepository.save(payment);

        // Update order status based on payment status
        updateOrderStatus(payment);

        log.info("Payment {} updated to status: {}", payment.getId(), newStatus);
    }

    @Override
    public PaymentResponse checkPaymentStatus(String midtransOrderId) {
        String url = apiUrl + "/" + midtransOrderId + "/status";

        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String transactionStatus = jsonNode.path("transaction_status").asText();
            
            Payment payment = getPaymentByMidtransOrderId(midtransOrderId);
            PaymentStatus newStatus = mapTransactionStatus(transactionStatus);
            
            if (payment.getStatus() != newStatus) {
                payment.setStatus(newStatus);
                payment.setTransactionId(jsonNode.path("transaction_id").asText());
                payment.setPaymentType(jsonNode.path("payment_type").asText());
                paymentRepository.save(payment);
                updateOrderStatus(payment);
            }

            return toPaymentResponse(payment);
        } catch (Exception e) {
            log.error("Failed to check payment status for order: {}", midtransOrderId, e);
            throw new RuntimeException("Failed to check payment status", e);
        }
    }

    @Override
    @Transactional
    public boolean cancelPayment(Long orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Cannot cancel payment with status: {}", payment.getStatus());
            return false;
        }

        String url = apiUrl + "/" + payment.getMidtransOrderId() + "/cancel";

        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            payment.setStatus(PaymentStatus.CANCEL);
            paymentRepository.save(payment);
            
            // Update order status
            Order order = payment.getOrder();
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            
            log.info("Payment cancelled for order: {}", orderId);
            return true;
        } catch (Exception e) {
            log.error("Failed to cancel payment for order: {}", orderId, e);
            return false;
        }
    }

    @Override
    public boolean verifySignature(MidtransNotificationRequest notification) {
        try {
            String data = notification.getOrderId() + 
                         notification.getStatusCode() + 
                         notification.getGrossAmount() + 
                         serverKey;
            
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            String calculatedSignature = hexString.toString();
            return calculatedSignature.equals(notification.getSignatureKey());
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-512 algorithm not available", e);
            return false;
        }
    }

    @Override
    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .transactionId(payment.getTransactionId())
                .midtransOrderId(payment.getMidtransOrderId())
                .snapToken(payment.getSnapToken())
                .redirectUrl(payment.getRedirectUrl())
                .paymentType(payment.getPaymentType())
                .grossAmount(payment.getGrossAmount())
                .status(payment.getStatus().name())
                .vaNumber(payment.getVaNumber())
                .bank(payment.getBank())
                .paymentCode(payment.getPaymentCode())
                .billKey(payment.getBillKey())
                .billerCode(payment.getBillerCode())
                .qrCodeUrl(payment.getQrCodeUrl())
                .expiryTime(payment.getExpiryTime())
                .settlementTime(payment.getSettlementTime())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private MidtransSnapRequest buildSnapRequest(Order order, String midtransOrderId) {
        // Transaction details
        MidtransSnapRequest.TransactionDetails transactionDetails = MidtransSnapRequest.TransactionDetails.builder()
                .orderId(midtransOrderId)
                .grossAmount(order.getTotalAmount().longValue())
                .build();

        // Customer details
        MidtransSnapRequest.CustomerDetails customerDetails = MidtransSnapRequest.CustomerDetails.builder()
                .firstName(order.getCustomerName())
                .email(order.getCustomerEmail() != null ? order.getCustomerEmail() : order.getUser().getEmail())
                .phone(order.getCustomerPhone())
                .build();

        // Item details
        List<MidtransSnapRequest.ItemDetail> itemDetails = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            itemDetails.add(MidtransSnapRequest.ItemDetail.builder()
                    .id(item.getProduct().getId().toString())
                    .name(truncateString(item.getProduct().getName(), 50))
                    .price(item.getPriceAtOrder().longValue())
                    .quantity(item.getQuantity())
                    .build());
        }

        // Add tax as separate item if exists
        if (order.getTax() != null && order.getTax().compareTo(BigDecimal.ZERO) > 0) {
            itemDetails.add(MidtransSnapRequest.ItemDetail.builder()
                    .id("TAX")
                    .name("Pajak")
                    .price(order.getTax().longValue())
                    .quantity(1)
                    .build());
        }

        // Callbacks
        MidtransSnapRequest.Callbacks callbacks = MidtransSnapRequest.Callbacks.builder()
                .finish(appBaseUrl + "/payment/finish")
                .error(appBaseUrl + "/payment/error")
                .pending(appBaseUrl + "/payment/pending")
                .build();

        return MidtransSnapRequest.builder()
                .transactionDetails(transactionDetails)
                .customerDetails(customerDetails)
                .itemDetails(itemDetails)
                .callbacks(callbacks)
                .build();
    }

    private MidtransSnapResponse callMidtransSnapApi(MidtransSnapRequest request) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            log.debug("Midtrans Snap request: {}", requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<MidtransSnapResponse> response = restTemplate.exchange(
                    snapUrl, HttpMethod.POST, entity, MidtransSnapResponse.class);

            if (response.getBody() == null) {
                throw new RuntimeException("Empty response from Midtrans");
            }

            if (response.getBody().getErrorMessages() != null) {
                throw new RuntimeException("Midtrans error: " + 
                        String.join(", ", response.getBody().getErrorMessages()));
            }

            log.debug("Midtrans Snap response: token={}, redirectUrl={}", 
                    response.getBody().getToken(), response.getBody().getRedirectUrl());

            return response.getBody();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Snap request", e);
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = serverKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    private PaymentStatus mapTransactionStatus(String transactionStatus) {
        if (transactionStatus == null) {
            return PaymentStatus.PENDING;
        }

        return switch (transactionStatus.toLowerCase()) {
            case "capture" -> PaymentStatus.CAPTURE;
            case "settlement" -> PaymentStatus.SETTLEMENT;
            case "deny" -> PaymentStatus.DENY;
            case "cancel" -> PaymentStatus.CANCEL;
            case "expire" -> PaymentStatus.EXPIRE;
            case "refund" -> PaymentStatus.REFUND;
            case "partial_refund" -> PaymentStatus.PARTIAL_REFUND;
            case "authorize" -> PaymentStatus.AUTHORIZE;
            case "failure" -> PaymentStatus.FAILURE;
            default -> PaymentStatus.PENDING;
        };
    }

    private void updateOrderStatus(Payment payment) {
        Order order = payment.getOrder();
        
        switch (payment.getStatus()) {
            case SETTLEMENT, CAPTURE -> {
                order.setStatus(Order.OrderStatus.CONFIRMED);
            }
            case DENY, CANCEL, EXPIRE, FAILURE -> {
                order.setStatus(Order.OrderStatus.CANCELLED);
            }
            default -> {
                // Keep current order status for pending states
            }
        }
        
        orderRepository.save(order);
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
