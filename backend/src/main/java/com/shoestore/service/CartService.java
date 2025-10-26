package com.shoestore.service;

import com.shoestore.dto.CartItemDto;
import com.shoestore.dto.ProductDto;
import com.shoestore.entity.CartItem;
import com.shoestore.entity.Product;
import com.shoestore.entity.User;
import com.shoestore.repository.CartItemRepository;
import com.shoestore.repository.ProductRepository;
import com.shoestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<CartItemDto> getCartItems() {
        User user = getCurrentUser();
        return cartItemRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CartItemDto addToCart(Long productId, Integer quantity) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
        
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            CartItem savedCartItem = cartItemRepository.save(cartItem);
            return convertToDto(savedCartItem);
        } else {
            CartItem newCartItem = new CartItem(user, product, quantity);
            CartItem savedCartItem = cartItemRepository.save(newCartItem);
            return convertToDto(savedCartItem);
        }
    }
    
    public CartItemDto updateCartItem(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        
        User currentUser = getCurrentUser();
        if (!cartItem.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }
        
        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        
        cartItem.setQuantity(quantity);
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return convertToDto(savedCartItem);
    }
    
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        
        User currentUser = getCurrentUser();
        if (!cartItem.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }
        
        cartItemRepository.delete(cartItem);
    }
    
    public void clearCart() {
        User user = getCurrentUser();
        cartItemRepository.deleteByUser(user);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private CartItemDto convertToDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setQuantity(cartItem.getQuantity());
        
        // Convert product to DTO
        ProductDto productDto = new ProductDto();
        productDto.setId(cartItem.getProduct().getId());
        productDto.setName(cartItem.getProduct().getName());
        productDto.setDescription(cartItem.getProduct().getDescription());
        productDto.setPrice(cartItem.getProduct().getPrice());
        productDto.setStockQuantity(cartItem.getProduct().getStockQuantity());
        productDto.setBrand(cartItem.getProduct().getBrand());
        productDto.setCategory(cartItem.getProduct().getCategory());
        productDto.setGender(cartItem.getProduct().getGender());
        productDto.setImageUrls(cartItem.getProduct().getImageUrls());
        productDto.setColor(cartItem.getProduct().getColor());
        productDto.setSize(cartItem.getProduct().getSize());
        productDto.setMaterial(cartItem.getProduct().getMaterial());
        productDto.setIsActive(cartItem.getProduct().getIsActive());
        
        dto.setProduct(productDto);
        return dto;
    }
}

