package com.rotahub.tracking.tracking.dto;

import com.rotahub.tracking.tracking.Position;
import com.rotahub.tracking.tracking.Tracking;
import com.rotahub.tracking.tracking.TrackingEvent;
import com.rotahub.tracking.tracking.TrackingStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TrackingResponse(
    String id,
    UUID orderId,
    TrackingStatus status,
    Position position,
    List<TrackingEvent> history,
    Instant createdAt
) {

    public static TrackingResponse from(Tracking tracking) {
        return new TrackingResponse(
            tracking.getId(),
            tracking.getOrderId(),
            tracking.getStatus(),
            tracking.getPosition(),
            tracking.getHistory(),
            tracking.getCreatedAt()
        );
    }
}
