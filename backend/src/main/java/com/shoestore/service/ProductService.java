package com.shoestore.service;

import com.shoestore.dto.ProductDto;
import com.shoestore.entity.Category;
import com.shoestore.entity.Gender;
import com.shoestore.entity.Product;
import com.shoestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<ProductDto> getAllProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getProductsByGender(Gender gender) {
        return productRepository.findByGenderAndIsActiveTrue(gender).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getProductsByCategory(Category category) {
        return productRepository.findByCategoryAndIsActiveTrue(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getProductsByGenderAndCategory(Gender gender, Category category) {
        return productRepository.findByGenderAndCategoryAndIsActiveTrue(gender, category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive)
                .map(this::convertToDto);
    }
    
    public ProductDto createProduct(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        product.setIsActive(true);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setStockQuantity(productDto.getStockQuantity());
        existingProduct.setBrand(productDto.getBrand());
        existingProduct.setCategory(productDto.getCategory());
        existingProduct.setGender(productDto.getGender());
        existingProduct.setImageUrls(productDto.getImageUrls());
        existingProduct.setColor(productDto.getColor());
        existingProduct.setSize(productDto.getSize());
        existingProduct.setMaterial(productDto.getMaterial());
        existingProduct.setIsActive(productDto.getIsActive());
        
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    public List<ProductDto> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public boolean updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getStockQuantity() >= quantity) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }
    
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setBrand(product.getBrand());
        dto.setCategory(product.getCategory());
        dto.setGender(product.getGender());
        dto.setImageUrls(product.getImageUrls());
        dto.setColor(product.getColor());
        dto.setSize(product.getSize());
        dto.setMaterial(product.getMaterial());
        dto.setIsActive(product.getIsActive());
        return dto;
    }
    
    private Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setBrand(dto.getBrand());
        product.setCategory(dto.getCategory());
        product.setGender(dto.getGender());
        product.setImageUrls(dto.getImageUrls());
        product.setColor(dto.getColor());
        product.setSize(dto.getSize());
        product.setMaterial(dto.getMaterial());
        return product;
    }
}

