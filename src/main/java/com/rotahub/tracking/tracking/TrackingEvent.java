package com.rotahub.tracking.tracking;

import java.time.Instant;

public record TrackingEvent(TrackingStatus status, Position position, Instant timestamp, String note) {
}
