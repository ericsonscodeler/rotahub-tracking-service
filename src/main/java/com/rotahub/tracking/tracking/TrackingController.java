package com.rotahub.tracking.tracking;

import com.rotahub.tracking.tracking.dto.AddTrackingEventRequest;
import com.rotahub.tracking.tracking.dto.CreateTrackingRequest;
import com.rotahub.tracking.tracking.dto.TrackingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/trackings")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackingResponse create(@Valid @RequestBody CreateTrackingRequest request) {
        return TrackingResponse.from(trackingService.create(request.orderId()));
    }

    @GetMapping("/{orderId}")
    public TrackingResponse getByOrderId(@PathVariable UUID orderId) {
        return TrackingResponse.from(trackingService.getByOrderId(orderId));
    }

    @PostMapping("/{orderId}/events")
    public TrackingResponse addEvent(@PathVariable UUID orderId, @Valid @RequestBody AddTrackingEventRequest request) {
        Position position = request.position() != null
            ? new Position(request.position().lat(), request.position().lng())
            : null;
        Tracking tracking = trackingService.addEvent(orderId, request.status(), position, request.timestamp(), request.note());
        return TrackingResponse.from(tracking);
    }
}
