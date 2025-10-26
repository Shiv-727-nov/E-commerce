package com.shoestore.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {
    
    @Value("${razorpay.key-id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;
    
    public String createOrder(BigDecimal amount, String currency, String receipt) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);
        
        Order order = razorpay.orders.create(orderRequest);
        return order.get("id");
    }
    
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);
            
            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);
            return isValid;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getPaymentStatus(String paymentId) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        
        com.razorpay.Payment payment = razorpay.payments.fetch(paymentId);
        return payment.get("status");
    }
}
