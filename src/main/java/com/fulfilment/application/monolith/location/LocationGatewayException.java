package com.fulfilment.application.monolith.location;

import jakarta.ws.rs.WebApplicationException;

public class LocationGatewayException extends WebApplicationException {
    public LocationGatewayException(String message, int code) {
        super(message, code);
    }
}
