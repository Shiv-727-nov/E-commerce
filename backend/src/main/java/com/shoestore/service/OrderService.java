package com.shoestore.service;

import com.shoestore.dto.CartItemDto;
import com.shoestore.dto.OrderDto;
import com.shoestore.dto.OrderItemDto;
import com.shoestore.dto.ProductDto;
import com.shoestore.entity.*;
import com.shoestore.repository.OrderRepository;
import com.shoestore.repository.ProductRepository;
import com.shoestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartService cartService;
    
    public List<OrderDto> getUserOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllOrderByOrderDateDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public OrderDto createOrder(OrderDto orderDto) {
        User user = getCurrentUser();
        List<CartItemDto> cartItems = cartService.getCartItems();
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create order
        Order order = new Order(user, totalAmount, orderDto.getShippingAddress(),
                orderDto.getCity(), orderDto.getState(), orderDto.getZipCode(), orderDto.getCountry());
        order.setPhoneNumber(orderDto.getPhoneNumber());
        
        // Create order items and update stock
        for (CartItemDto cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), product.getPrice());
            order.getOrderItems().add(orderItem);
            
            // Update stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cartService.clearCart();
        
        return convertToDto(savedOrder);
    }
    
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }
    
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        return convertToDto(order);
    }
    
    public void updatePaymentStatus(Long orderId, String paymentId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setPaymentId(paymentId);
        order.setPaymentStatus(paymentStatus);
        
        if ("captured".equals(paymentStatus)) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        
        orderRepository.save(order);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setState(order.getState());
        dto.setZipCode(order.getZipCode());
        dto.setCountry(order.getCountry());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setPaymentId(order.getPaymentId());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderDate(order.getOrderDate());
        
        // Convert order items
        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(this::convertOrderItemToDto)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDtos);
        
        return dto;
    }
    
    private OrderItemDto convertOrderItemToDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        
        // Convert product to DTO
        ProductDto productDto = new ProductDto();
        productDto.setId(orderItem.getProduct().getId());
        productDto.setName(orderItem.getProduct().getName());
        productDto.setDescription(orderItem.getProduct().getDescription());
        productDto.setPrice(orderItem.getProduct().getPrice());
        productDto.setStockQuantity(orderItem.getProduct().getStockQuantity());
        productDto.setBrand(orderItem.getProduct().getBrand());
        productDto.setCategory(orderItem.getProduct().getCategory());
        productDto.setGender(orderItem.getProduct().getGender());
        productDto.setImageUrls(orderItem.getProduct().getImageUrls());
        productDto.setColor(orderItem.getProduct().getColor());
        productDto.setSize(orderItem.getProduct().getSize());
        productDto.setMaterial(orderItem.getProduct().getMaterial());
        productDto.setIsActive(orderItem.getProduct().getIsActive());
        
        dto.setProduct(productDto);
        return dto;
    }
}

