package com.fulfilment.application.monolith.warehouses.exception;

import jakarta.ws.rs.WebApplicationException;

public class WarehouseException extends WebApplicationException {
    public WarehouseException(String message, int code) {
        super(message, code);
    }
}
