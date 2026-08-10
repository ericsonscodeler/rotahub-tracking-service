package com.rotahub.tracking.tracking.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTrackingRequest(
    @NotNull UUID orderId
) {
}
