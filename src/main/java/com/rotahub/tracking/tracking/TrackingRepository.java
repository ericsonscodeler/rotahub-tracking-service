package com.rotahub.tracking.tracking;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrackingRepository extends MongoRepository<Tracking, String> {

    Optional<Tracking> findByOrderId(UUID orderId);
}
