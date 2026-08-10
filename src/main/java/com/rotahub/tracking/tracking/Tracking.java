package com.rotahub.tracking.tracking;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "trackings")
public class Tracking {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID orderId;

    private TrackingStatus status;

    private Position position;

    private List<TrackingEvent> history;

    private Instant createdAt;

    protected Tracking() {
    }

    public Tracking(UUID orderId) {
        this.orderId = orderId;
        this.status = TrackingStatus.AWAITING_PICKUP;
        this.history = new ArrayList<>();
        this.createdAt = Instant.now();
    }

    public void applyEvent(TrackingStatus status, Position position, Instant timestamp, String note) {
        this.status = status;
        this.position = position;
        this.history.add(new TrackingEvent(status, position, timestamp, note));
    }

    public String getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public TrackingStatus getStatus() {
        return status;
    }

    public Position getPosition() {
        return position;
    }

    public List<TrackingEvent> getHistory() {
        return history;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
