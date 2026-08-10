package com.rotahub.tracking.tracking.event;

import com.rotahub.tracking.tracking.Position;

import java.time.Instant;
import java.util.UUID;

public record DeliveryCompletedEvent(
    UUID eventId,
    String eventType,
    Instant occurredAt,
    UUID orderId,
    String trackingId,
    Instant deliveredAt,
    Position finalPosition
) {

    public static DeliveryCompletedEvent of(UUID orderId, String trackingId, Instant deliveredAt, Position finalPosition) {
        return new DeliveryCompletedEvent(
            UUID.randomUUID(),
            "delivery.completed",
            Instant.now(),
            orderId,
            trackingId,
            deliveredAt,
            finalPosition
        );
    }
}
