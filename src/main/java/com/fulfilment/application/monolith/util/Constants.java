package com.fulfilment.application.monolith.util;

public class Constants {
    // Warehouse
    public static final String ID_NOT_FOUNT = "Warehouse with the specified ID %d does not exist.";
    public static final String BUSINESS_CODE_EXISTS = "A warehouse with the specified business unit code %s already exists.";
    public static final String WAREHOUSE_EXCEEDS_CAPACITY = "The warehouse stock cannot exceed its capacity.";

    public static final String VALIDATION_FAILED = "Warehouse validation failed.";

    // Location
    public static final String INVALID_LOCATION_IDENTIFIER = "Identifier cannot be null or empty";
    public static final String LOCATION_NOT_FOUND = "Location with identifier %s not found";
    public static final String INVALID_LOCATION = "The specified location is invalid.";
    public static final String MAX_NO_OF_WAREHOUSES_REACHED = "The maximum number of warehouses for this location has been reached.";
    public static final String WAREHOUSE_CAPACITY_EXCEEDS_MAX_CAPACITY = "The warehouse capacity exceeds the maximum capacity for this location.";

    // Product
    public static final String INVALID_PRODUCT_ID = "Id was invalidly set on request.";
    public static final String PRODUCT_ID_NOT_EXISTS = "Product with id of %d does not exist.";
    public static final String PRODUCT_NAME_NOT_FOUND = "Product Name was not set on request.";

    // Store
    public static final String INVALID_STORE_ID = "Id was invalidly set on request.";
    public static final String STORE_ID_NOT_EXISTS = "Store with id of %d does not exist.";
    public static final String STORE_NAME_NOT_FOUND = "Store Name was not set on request.";

}
