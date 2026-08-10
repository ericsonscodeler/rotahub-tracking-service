package com.rotahub.tracking.tracking.dto;

import com.rotahub.tracking.tracking.TrackingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AddTrackingEventRequest(
    @NotNull TrackingStatus status,
    @Valid PositionRequest position,
    @NotNull Instant timestamp,
    String note
) {
}
