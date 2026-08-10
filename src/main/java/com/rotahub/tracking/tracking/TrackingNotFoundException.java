package com.rotahub.tracking.tracking;

import java.util.UUID;

public class TrackingNotFoundException extends RuntimeException {

    public TrackingNotFoundException(UUID orderId) {
        super("Tracking not found for order: " + orderId);
    }
}
