package com.fulfilment.application.monolith.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.products.ProductResource;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.stores.StoreResource;

class StoreResourceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private LegacyStoreManagerGateway legacyStoreManagerGateway;

    @InjectMocks
    private StoreResource storeResource;
    private StoreResource.ErrorMapper errorMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        errorMapper = new StoreResource.ErrorMapper();
        errorMapper.objectMapper = objectMapper;
    }

    @Test
    void testGetAllStores() {
        final Store store1 = createStoreMock(1L,"Store1", 100);
        Store store2 = createStoreMock(2L,"Store2", 200);
        List<Store> stores = Arrays.asList(store1, store2);

        when(storeRepository.listAll(any())).thenReturn(stores);

        List<Store> result = storeResource.get();

        assertEquals(2, result.size());
        assertEquals("Store1", result.get(0).name);
        verify(storeRepository, times(1)).listAll(any());
    }

    private static Store createStoreMock(Long id, String name, int quantityProductsInStock) {
        Store store = new Store();
        store.id = id;
        store.name = name;
        store.quantityProductsInStock = quantityProductsInStock;
        return store;
    }

    @Test
    void testGetSingleStore() {
        Store store = createStoreMock(1L,"Store1", 100);

        when(storeRepository.findById(1L)).thenReturn(store);

        Store result = storeResource.getSingle(1L);

        assertEquals("Store1", result.name);
        verify(storeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSingleStoreNotFound() {
        when(storeRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.getSingle(1L));

        assertEquals(404, exception.getResponse().getStatus());
        verify(storeRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateStore() {
        Store store = createStoreMock(null,"Store1", 100);

        doNothing().when(storeRepository).persist(store);
        doNothing().when(legacyStoreManagerGateway).createStoreOnLegacySystem(store);

        Response response = storeResource.create(store);

        assertEquals(201, response.getStatus());
        verify(storeRepository, times(1)).persist(store);
        verify(legacyStoreManagerGateway, times(1)).createStoreOnLegacySystem(store);
    }

    @Test
    void testCreateStoreWithId() {
        Store store = createStoreMock(1L,"Store1", 100);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.create(store));

        assertEquals(422, exception.getResponse().getStatus());
        verify(storeRepository, never()).persist(any(Store.class));
        verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any(Store.class));
    }

    @Test
    void testUpdateStore() {
        Store existingStore = createStoreMock(1L,"Store1", 100);
        Store updatedStore = createStoreMock(1L,"UpdatedStore", 200);

        when(storeRepository.findById(1L)).thenReturn(existingStore);
        doNothing().when(legacyStoreManagerGateway).updateStoreOnLegacySystem(updatedStore);

        Store result = storeResource.update(1L, updatedStore);

        assertEquals("UpdatedStore", result.name);
        assertEquals(200, result.quantityProductsInStock);
        verify(storeRepository, times(1)).findById(1L);
        verify(legacyStoreManagerGateway, times(1)).updateStoreOnLegacySystem(updatedStore);
    }

    @Test
    void testUpdateStoreNotFound() {
        Store updatedStore = createStoreMock(1L,"UpdatedStore", 200);

        when(storeRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.update(1L, updatedStore));

        assertEquals(404, exception.getResponse().getStatus());
        verify(storeRepository, times(1)).findById(1L);
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any(Store.class));
    }

    @Test
    void testUpdateStoreWithoutName() {
        Store existingStore = createStoreMock(1L,"Store1", 100);
        Store updatedStore = createStoreMock(1L,null, 200);

        when(storeRepository.findById(1L)).thenReturn(existingStore);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.update(1L, updatedStore));

        assertEquals(422, exception.getResponse().getStatus());
        verify(storeRepository, times(0)).findById(1L);
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any(Store.class));
    }

    @Test
    void testDeleteStore() {
        Store store = createStoreMock(1L,"Store1", 100);

        when(storeRepository.findById(1L)).thenReturn(store);
        doNothing().when(storeRepository).delete(store);

        Response response = storeResource.delete(1L);

        assertEquals(204, response.getStatus());
        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, times(1)).delete(store);
    }

    @Test
    void testDeleteStoreNotFound() {
        when(storeRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.delete(1L));

        assertEquals(404, exception.getResponse().getStatus());
        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, never()).delete(any(Store.class));
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

    @Test
    void testPatchStoreSuccess() {
        Store existingStore = createStoreMock(1L, "Store1", 100);
        Store updatedStore = createStoreMock(1L, "UpdatedStore", 200);

        when(storeRepository.findById(1L)).thenReturn(existingStore);

        Store result = storeResource.patch(1L, updatedStore);

        assertEquals("UpdatedStore", result.name);
        assertEquals(200, result.quantityProductsInStock);
        verify(storeRepository, times(1)).findById(1L);
        verify(legacyStoreManagerGateway, times(1)).updateStoreOnLegacySystem(updatedStore);
    }

    @Test
    void testPatchStoreNotFound() {
        Store updatedStore = createStoreMock(1L, "UpdatedStore", 200);

        when(storeRepository.findById(1L)).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.patch(1L, updatedStore));

        assertEquals(404, exception.getResponse().getStatus());
        verify(storeRepository, times(1)).findById(1L);
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any(Store.class));
    }

    @Test
    void testPatchStoreWithoutName() {
        Store existingStore = createStoreMock(1L, "Store1", 100);
        Store updatedStore = createStoreMock(1L, null, 200);

        when(storeRepository.findById(1L)).thenReturn(existingStore);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> storeResource.patch(1L, updatedStore));

        assertEquals(422, exception.getResponse().getStatus());
        verify(storeRepository, times(0)).findById(1L);
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any(Store.class));
    }

    @Test
    void testPatchStorePartialUpdate() {
        Store existingStore = createStoreMock(1L, "Store1", 100);
        Store updatedStore = createStoreMock(1L, "UpdatedStore", 0); // Only name is updated

        when(storeRepository.findById(1L)).thenReturn(existingStore);

        Store result = storeResource.patch(1L, updatedStore);

        assertEquals("UpdatedStore", result.name);
        assertEquals(0, result.quantityProductsInStock); // Quantity remains unchanged
        verify(storeRepository, times(1)).findById(1L);
        verify(legacyStoreManagerGateway, times(1)).updateStoreOnLegacySystem(updatedStore);
    }
}