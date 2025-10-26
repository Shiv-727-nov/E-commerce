package com.shoestore.config;

import com.shoestore.entity.*;
import com.shoestore.repository.ProductRepository;
import com.shoestore.repository.OrderRepository;
import com.shoestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@shoestore.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        // Create sample products
        if (productRepository.count() == 0) {
            createSampleProducts();
        }

        // Seed demo delivered orders for dashboard visualization
        if (orderRepository.count() == 0) {
            seedDemoOrders();
        }
    }

    private void createSampleProducts() {
        // Men's Sneakers
        Product product1 = new Product();
        product1.setName("Air Max 270");
        product1.setDescription("The Air Max 270 delivers visible cushioning under every step. The design draws inspiration from Air Max icons, showcasing Nike's greatest innovation with its large window and fresh array of colors.");
        product1.setPrice(new BigDecimal("1500.00"));
        product1.setStockQuantity(50);
        product1.setBrand("Nike");
        product1.setCategory(Category.SNEAKERS);
        product1.setGender(Gender.MEN);
        product1.setImageUrls(Arrays.asList(
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
            "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=500"
        ));
        product1.setColor("Black");
        product1.setSize("M");
        product1.setMaterial("Mesh");
        productRepository.save(product1);

        // Women's Running Shoes
        Product product2 = new Product();
        product2.setName("React Infinity Run");
        product2.setDescription("The Nike React Infinity Run is designed to help reduce injury and keep you running. More foam means better cushioning under every step.");
        product2.setPrice(new BigDecimal("1600.00"));
        product2.setStockQuantity(30);
        product2.setBrand("Nike");
        product2.setCategory(Category.RUNNING);
        product2.setGender(Gender.WOMEN);
        product2.setImageUrls(Arrays.asList(
            "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500",
            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500"
        ));
        product2.setColor("White");
        product2.setSize("S");
        product2.setMaterial("Flyknit");
        productRepository.save(product2);

        // Kids' Basketball Shoes
        Product product3 = new Product();
        product3.setName("LeBron Witness 5");
        product3.setDescription("The LeBron Witness 5 delivers the perfect combination of comfort and performance for young athletes.");
        product3.setPrice(new BigDecimal("800.00"));
        product3.setStockQuantity(25);
        product3.setBrand("Nike");
        product3.setCategory(Category.BASKETBALL);
        product3.setGender(Gender.KIDS);
        product3.setImageUrls(Arrays.asList(
            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500",
            "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=500"
        ));
        product3.setColor("Red");
        product3.setSize("XS");
        product3.setMaterial("Synthetic");
        productRepository.save(product3);

        // Men's Casual Shoes
        Product product4 = new Product();
        product4.setName("Classic Leather Sneakers");
        product4.setDescription("Timeless design meets modern comfort in these classic leather sneakers. Perfect for everyday wear.");
        product4.setPrice(new BigDecimal("1200.00"));
        product4.setStockQuantity(40);
        product4.setBrand("Adidas");
        product4.setCategory(Category.CASUAL);
        product4.setGender(Gender.MEN);
        product4.setImageUrls(Arrays.asList(
            "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500",
            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500"
        ));
        product4.setColor("Brown");
        product4.setSize("L");
        product4.setMaterial("Leather");
        productRepository.save(product4);

        // Women's Heels
        Product product5 = new Product();
        product5.setName("Elegant Stiletto Heels");
        product5.setDescription("Make a statement with these elegant stiletto heels. Perfect for special occasions and formal events.");
        product5.setPrice(new BigDecimal("900.00"));
        product5.setStockQuantity(20);
        product5.setBrand("Puma");
        product5.setCategory(Category.HEELS);
        product5.setGender(Gender.WOMEN);
        product5.setImageUrls(Arrays.asList(
            "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=500",
            "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=500"
        ));
        product5.setColor("Black");
        product5.setSize("M");
        product5.setMaterial("Synthetic Leather");
        productRepository.save(product5);

        // Kids' Running Shoes
        Product product6 = new Product();
        product6.setName("Kids Running Shoes");
        product6.setDescription("Comfortable and durable running shoes designed specifically for kids. Perfect for school and play.");
        product6.setPrice(new BigDecimal("600.00"));
        product6.setStockQuantity(35);
        product6.setBrand("Reebok");
        product6.setCategory(Category.RUNNING);
        product6.setGender(Gender.KIDS);
        product6.setImageUrls(Arrays.asList(
            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500",
            "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=500"
        ));
        product6.setColor("Blue");
        product6.setSize("S");
        product6.setMaterial("Mesh");
        productRepository.save(product6);
    }

    private void seedDemoOrders() {
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setEmail("admin@shoestore.com");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setFirstName("Admin");
            u.setLastName("User");
            u.setRole(Role.ADMIN);
            return userRepository.save(u);
        });

        // Helper to create a delivered order with items
        createDeliveredOrder(admin, List.of(
            new ProductQuantity(Gender.MEN, 2),
            new ProductQuantity(Gender.MEN, 1)
        ));

        createDeliveredOrder(admin, List.of(
            new ProductQuantity(Gender.WOMEN, 1),
            new ProductQuantity(Gender.WOMEN, 2)
        ));

        createDeliveredOrder(admin, List.of(
            new ProductQuantity(Gender.KIDS, 3)
        ));
    }

    private void createDeliveredOrder(User user, List<ProductQuantity> itemsSpec) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress("123 Demo Street");
        order.setCity("Demo City");
        order.setState("CA");
        order.setZipCode("90001");
        order.setCountry("USA");
        order.setPhoneNumber("+1-555-0000");
        order.setStatus(OrderStatus.DELIVERED);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (ProductQuantity pq : itemsSpec) {
            // pick any active product for the gender
            Product product = productRepository.findByGenderAndIsActiveTrue(pq.gender)
                    .stream().findFirst().orElse(null);
            if (product == null) continue;
            OrderItem oi = new OrderItem(order, product, pq.quantity, product.getPrice());
            orderItems.add(oi);
            total = total.add(product.getPrice().multiply(new BigDecimal(pq.quantity)));
        }

        if (orderItems.isEmpty()) return;

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
    }

    private static class ProductQuantity {
        final Gender gender;
        final int quantity;
        ProductQuantity(Gender gender, int quantity) {
            this.gender = gender;
            this.quantity = quantity;
        }
    }
}

