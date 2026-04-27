package com.example.products.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.products.dto.CreateProdutDto;
import com.example.products.dto.UpdateProcutDto;
import com.example.products.models.Product;
import com.example.products.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@SuppressWarnings("null")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID productId;
    private String userId;
    private Product testProduct;
    private CreateProdutDto createDto;
    private UpdateProcutDto updateDto;

    @BeforeEach
    void setUp() {
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

    @Test
    void testGetProductsPublic() throws Exception {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Product"))
                .andExpect(jsonPath("$.data[0].price").value(29.99));

        verify(productService).getAllProducts();
    }

    @Test
    @WithMockUser(username = "user-123")
    void testGetMyProductsAuthenticated() throws Exception {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        when(productService.getMyProducts("user-123")).thenReturn(products);

        mockMvc.perform(get("/api/products/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value("user-123"));

        verify(productService).getMyProducts("user-123");
    }

    @Test
    void testGetProductByIdSuccess() throws Exception {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        mockMvc.perform(get("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Product"));

        verify(productService).getProductById(productId);
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(productId)).thenReturn(null);

        mockMvc.perform(get("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(productService).getProductById(productId);
    }

    @Test
    @WithMockUser(username = "user-123")
    void testCreateProductSuccess() throws Exception {
        Product createdProduct = new Product(createDto, userId);
        createdProduct.setId(productId);

        when(productService.createProduct(any(CreateProdutDto.class), eq(userId)))
                .thenReturn(createdProduct);

        mockMvc.perform(post("/api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.name").value("New Product"));

        verify(productService).createProduct(ArgumentMatchers.any(CreateProdutDto.class), eq(userId));
    }

    @Test
    @WithMockUser(username = "user-123")
    void testCreateProductInvalidData() throws Exception {
        CreateProdutDto invalidDto = new CreateProdutDto();
        invalidDto.setName(""); // Invalid - blank name
        invalidDto.setDescription("desc");
        invalidDto.setPrice(10.0);

        mockMvc.perform(post("/api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user-123")
    void testDeleteProductSuccess() throws Exception {
        when(productService.getProductById(productId)).thenReturn(testProduct);
        doNothing().when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }

    @Test
    @WithMockUser(username = "user-123")
    void testDeleteProductNotFound() throws Exception {
        when(productService.getProductById(productId)).thenReturn(null);

        mockMvc.perform(delete("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "different-user")
    void testDeleteProductForbidden() throws Exception {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        mockMvc.perform(delete("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403));
    }

    @Test
    @WithMockUser(username = "user-123")
    void testUpdateProductSuccess() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(39.99);
        updatedProduct.setUserId(userId);

        when(productService.getProductById(productId)).thenReturn(testProduct);
        when(productService.updateProduct(any(Product.class), any(UpdateProcutDto.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Product"))
                .andExpect(jsonPath("$.data.price").value(39.99));

        verify(productService).updateProduct(ArgumentMatchers.any(Product.class), ArgumentMatchers.any(UpdateProcutDto.class));
    }

    @Test
    @WithMockUser(username = "user-123")
    void testUpdateProductNotFound() throws Exception {
        when(productService.getProductById(productId)).thenReturn(null);

        mockMvc.perform(put("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "different-user")
    void testUpdateProductForbidden() throws Exception {
        when(productService.getProductById(productId)).thenReturn(testProduct);

        mockMvc.perform(put("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403));
    }
}
