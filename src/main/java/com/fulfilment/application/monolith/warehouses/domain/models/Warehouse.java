package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class Warehouse {

  public Long id;
  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public ZonedDateTime creationAt = ZonedDateTime.now();

  public ZonedDateTime archivedAt;
}
