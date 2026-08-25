package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    LocationGateway locationGateway = new LocationGateway();

    var location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    assertEquals("ZWOLLE-001", location.identification);
  }

  @Test
  public void testWhenResolveUnknownLocationShouldReturnNull() {
    LocationGateway locationGateway = new LocationGateway();

    var location = locationGateway.resolveByIdentifier("UNKNOWN-001");

    assertNull(location);
  }
}
