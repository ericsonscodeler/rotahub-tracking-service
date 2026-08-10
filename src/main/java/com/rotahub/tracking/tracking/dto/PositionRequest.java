package com.rotahub.tracking.tracking.dto;

import jakarta.validation.constraints.NotNull;

public record PositionRequest(
    @NotNull Double lat,
    @NotNull Double lng
) {
}
