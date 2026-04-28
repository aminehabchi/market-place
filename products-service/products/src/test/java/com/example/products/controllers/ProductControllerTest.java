package com.example.products.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.example.products.dto.CreateProdutDto;
import com.example.products.dto.UpdateProcutDto;
import com.example.products.models.Product;
import com.example.products.services.ProductService;
import com.example.shared.common.utils.ApiResponse;

@SuppressWarnings("null")
class ProductControllerTest {
    private ProductService productService;
    private ProductController controller;

    private UUID productId;
    private String userId;
    private Product testProduct;
    private CreateProdutDto createDto;
    private UpdateProcutDto updateDto;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        controller = new ProductController(productService);

        productId = UUID.randomUUID();
        userId = "user-123";

        testProduct = new Product();
        testProduct.setId(productId);
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product description");
        testProduct.setPrice(29.99);
        testProduct.setQuantity(10);
        testProduct.setUserId(userId);

        createDto = new CreateProdutDto();
        createDto.setName("New Product");
        createDto.setDescription("New product description");
        createDto.setPrice(49.99);

        updateDto = new UpdateProcutDto();
        updateDto.setName("Updated Product");
        updateDto.setDescription("Updated description");
        updateDto.setPrice(39.99);
    }

    private Authentication authentication(String principal) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        return authentication;
    }

    @Test
    void getProductsReturnsApiResponse() {
        when(productService.getAllProducts()).thenReturn(List.of(testProduct));

        ResponseEntity<ApiResponse<List<Product>>> response = controller.getProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().data().size());
        assertEquals("Test Product", response.getBody().data().get(0).getName());
        verify(productService).getAllProducts();
    }

    @Test
    void getMyProductsUsesAuthenticatedPrincipal() {
        when(productService.getMyProducts(userId)).thenReturn(List.of(testProduct));

        ResponseEntity<ApiResponse<List<Product>>> response = controller.getMyProducts(authentication(userId));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user-123", response.getBody().data().get(0).getUserId());
        verify(productService).getMyProducts(userId);
    }

    @Test
    void getProductByIdSuccess() {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        ResponseEntity<ApiResponse<Product>> response = controller.getProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().data().getId());
        verify(productService).getProductById(productId);
    }

    @Test
    void getProductByIdReturnsNotFoundWhenServiceReturnsNull() {
        when(productService.getProductById(productId)).thenReturn(null);

        ResponseEntity<ApiResponse<Product>> response = controller.getProduct(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().success());
    }

    @Test
    void createProductReturnsCreatedResponse() {
        Product createdProduct = new Product(createDto, userId);
        createdProduct.setId(productId);
        when(productService.createProduct(any(CreateProdutDto.class), eq(userId))).thenReturn(createdProduct);

        ResponseEntity<ApiResponse<Product>> response = controller.createProduct(createDto, authentication(userId));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Product", response.getBody().data().getName());
        verify(productService).createProduct(createDto, userId);
    }

    @Test
    void deleteProductReturnsNoContentForOwner() {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        ResponseEntity<ApiResponse<Product>> response = controller.deleteProduct(productId, authentication(userId));

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void deleteProductReturnsForbiddenForDifferentUser() {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        ResponseEntity<ApiResponse<Product>> response = controller.deleteProduct(productId, authentication("different-user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().success());
    }

    @Test
    void updateProductReturnsOkForOwner() {
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(39.99);
        updatedProduct.setUserId(userId);

        when(productService.getProductById(productId)).thenReturn(testProduct);
        when(productService.updateProduct(testProduct, updateDto)).thenReturn(updatedProduct);

        ResponseEntity<ApiResponse<Product>> response = controller.updateProduct(productId, updateDto, authentication(userId));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Product", response.getBody().data().getName());
        verify(productService).updateProduct(testProduct, updateDto);
    }

    @Test
    void updateProductReturnsForbiddenForDifferentUser() {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        ResponseEntity<ApiResponse<Product>> response = controller.updateProduct(productId, updateDto, authentication("different-user"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().success());
    }
}