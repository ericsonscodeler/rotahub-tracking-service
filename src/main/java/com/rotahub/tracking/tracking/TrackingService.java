package com.rotahub.tracking.tracking;

import com.rotahub.tracking.tracking.event.DeliveryCompletedEvent;
import com.rotahub.tracking.tracking.event.TrackingStatusChangedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String deliveryCompletedRoutingKey;
    private final String trackingStatusChangedRoutingKey;

    public TrackingService(
        TrackingRepository trackingRepository,
        RabbitTemplate rabbitTemplate,
        @Value("${rotahub.events.exchange}") String exchange,
        @Value("${rotahub.events.delivery-completed-routing-key}") String deliveryCompletedRoutingKey,
        @Value("${rotahub.events.tracking-status-changed-routing-key}") String trackingStatusChangedRoutingKey
    ) {
        this.trackingRepository = trackingRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.deliveryCompletedRoutingKey = deliveryCompletedRoutingKey;
        this.trackingStatusChangedRoutingKey = trackingStatusChangedRoutingKey;
    }

    public Tracking create(UUID orderId) {
        return trackingRepository.save(new Tracking(orderId));
    }

    public Tracking getByOrderId(UUID orderId) {
        return trackingRepository.findByOrderId(orderId)
            .orElseThrow(() -> new TrackingNotFoundException(orderId));
    }

    public Tracking addEvent(UUID orderId, TrackingStatus status, Position position, Instant timestamp, String note) {
        Tracking tracking = getByOrderId(orderId);
        tracking.applyEvent(status, position, timestamp, note);
        Tracking saved = trackingRepository.save(tracking);

        publishStatusChanged(saved, status, position, note);
        if (status == TrackingStatus.DELIVERED) {
            publishDeliveryCompleted(saved, timestamp);
        }

        return saved;
    }

    private void publishStatusChanged(Tracking tracking, TrackingStatus status, Position position, String note) {
        TrackingStatusChangedEvent event = TrackingStatusChangedEvent.of(
            tracking.getOrderId(), tracking.getId(), status, position, note);
        rabbitTemplate.convertAndSend(exchange, trackingStatusChangedRoutingKey, event);
    }

    private void publishDeliveryCompleted(Tracking tracking, Instant deliveredAt) {
        DeliveryCompletedEvent event = DeliveryCompletedEvent.of(
            tracking.getOrderId(), tracking.getId(), deliveredAt, tracking.getPosition());
        rabbitTemplate.convertAndSend(exchange, deliveryCompletedRoutingKey, event);
    }
}
