package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductResourceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductResource productResource;

    private ProductResource.ErrorMapper errorMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        errorMapper = new ProductResource.ErrorMapper();
        errorMapper.objectMapper = objectMapper;
    }

    @Test
    void testGetAllProducts() {
        Product product1 = createMockProduct(1L, "Product1", "Description1", 10.0, 100);
        Product product2 = createMockProduct(2L, "Product2", "Description2", 20.0, 200);
        when(productRepository.listAll(any())).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productResource.get();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).listAll(any());
    }

    @Test
    void testGetSingleProduct() {
        Product product = createMockProduct(1L, "Product1", "Description1", 10.0, 100);
        when(productRepository.findById(1L)).thenReturn(product);

        Product result = productResource.getSingle(1L);

        assertNotNull(result);
        assertEquals("Product1", result.name);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSingleProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> productResource.getSingle(1L));

        assertEquals(404, exception.getResponse().getStatus());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateProduct() {
        Product product = createMockProduct(null, "Product1", "Description1", 10.0, 100);
        Response response = productResource.create(product);

        assertEquals(201, response.getStatus());
        verify(productRepository, times(1)).persist(product);
    }

    @Test
    void testCreateProductWithId() {
        Product product = createMockProduct(1L, "Product1", "Description1", 10.0, 100);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> productResource.create(product));

        assertEquals(422, exception.getResponse().getStatus());
        verify(productRepository, never()).persist(any(Product.class));
    }

    @Test
    void testUpdateProduct() {
        Product existingProduct = createMockProduct(1L, "Product1", "Description1", 10.0, 100);

        Product updatedProduct = createMockProduct(1L, "NewName", "NewDescription", 20.0, 200);

        when(productRepository.findById(1L)).thenReturn(existingProduct);

        Product result = productResource.update(1L, updatedProduct);

        assertEquals("NewName", result.name);
        assertEquals("NewDescription", result.description);
        verify(productRepository, times(1)).persist(existingProduct);
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedProduct = createMockProduct(1L, "NewName", "NewDescription", 20.0, 200);

        when(productRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> productResource.update(1L, updatedProduct));

        assertEquals(404, exception.getResponse().getStatus());
        verify(productRepository, never()).persist(any(Product.class));
    }

    @Test
    void testUpdateProductWithoutName() {
        Product existingProduct = createMockProduct(1L, "OldName", "OldDescription", 10.0, 100);
        Product updatedProduct = createMockProduct(1L, null, "NewDescription", 20.0, 200);

        when(productRepository.findById(1L)).thenReturn(existingProduct);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> productResource.update(1L, updatedProduct));

        assertEquals(422, exception.getResponse().getStatus());
        verify(productRepository, never()).persist(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        Product product = createMockProduct(1L, "Product1", "Description1", 10.0, 100);
        when(productRepository.findById(1L)).thenReturn(product);

        Response response = productResource.delete(1L);

        assertEquals(204, response.getStatus());
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> productResource.delete(1L));

        assertEquals(404, exception.getResponse().getStatus());
        verify(productRepository, never()).delete(any());
    }

    private static Product createMockProduct(Long id, String name, String description, double price, int stock) {
        Product product = new Product();
        product.id = id;
        product.name = name;
        product.description = description;
        product.price = BigDecimal.valueOf(price);
        product.stock = stock;
        return product;
    }

    @Test
    void testToResponseWithWebApplicationException() {
        // Arrange
        WebApplicationException exception = new WebApplicationException("Not Found", 404);

        // Act
        Response response = errorMapper.toResponse(exception);

        // Assert
        assertEquals(404, response.getStatus());
        ObjectNode responseBody = (ObjectNode) response.getEntity();
        assertEquals(WebApplicationException.class.getName(), responseBody.get("exceptionType").asText());
        assertEquals(404, responseBody.get("code").asInt());
        assertEquals("Not Found", responseBody.get("error").asText());
    }

    @Test
    void testToResponseWithGenericException() {
        // Arrange
        Exception exception = new Exception("Internal Server Error");

        // Act
        Response response = errorMapper.toResponse(exception);

        // Assert
        assertEquals(500, response.getStatus());
        ObjectNode responseBody = (ObjectNode) response.getEntity();
        assertEquals(Exception.class.getName(), responseBody.get("exceptionType").asText());
        assertEquals(500, responseBody.get("code").asInt());
        assertEquals("Internal Server Error", responseBody.get("error").asText());
    }
}
