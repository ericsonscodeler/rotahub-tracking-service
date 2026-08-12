package com.rotahub.tracking.tracking.event;

import com.rotahub.tracking.tracking.Position;
import com.rotahub.tracking.tracking.TrackingStatus;

import java.time.Instant;
import java.util.UUID;

public record TrackingStatusChangedEvent(
    UUID eventId,
    String eventType,
    Instant occurredAt,
    UUID orderId,
    String trackingId,
    TrackingStatus status,
    Position position,
    String note
) {

    public static TrackingStatusChangedEvent of(
        UUID orderId, String trackingId, TrackingStatus status, Position position, String note
    ) {
        return new TrackingStatusChangedEvent(
            UUID.randomUUID(),
            "tracking.status-changed",
            Instant.now(),
            orderId,
            trackingId,
            status,
            position,
            note
        );
    }
}
