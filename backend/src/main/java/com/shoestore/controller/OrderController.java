package com.shoestore.controller;

import com.shoestore.dto.OrderDto;
import com.shoestore.service.OrderService;
import com.shoestore.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        try {
            List<OrderDto> orders = orderService.getUserOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto) {
        try {
            OrderDto createdOrder = orderService.createOrder(orderDto);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{orderId}/payment")
    public ResponseEntity<?> createPaymentOrder(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            String razorpayOrderId = paymentService.createOrder(
                    order.getTotalAmount(), "INR", "order_" + orderId);
            return ResponseEntity.ok().body("{\"razorpayOrderId\": \"" + razorpayOrderId + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{orderId}/verify-payment")
    public ResponseEntity<?> verifyPayment(@PathVariable Long orderId, 
                                         @RequestParam String paymentId,
                                         @RequestParam String signature) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            boolean isValid = paymentService.verifyPayment("order_" + orderId, paymentId, signature);
            
            if (isValid) {
                orderService.updatePaymentStatus(orderId, paymentId, "captured");
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Payment verification failed");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}

