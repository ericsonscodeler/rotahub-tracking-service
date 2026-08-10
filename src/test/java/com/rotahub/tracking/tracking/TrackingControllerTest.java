package com.rotahub.tracking.tracking;

import com.rotahub.tracking.TestcontainersConfiguration;
import com.rotahub.tracking.tracking.event.DeliveryCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackingControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private TopicExchange rotahubEventsExchange;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private Queue testQueue;

    @BeforeEach
    void setUp() {
        trackingRepository.deleteAll();
        testQueue = amqpAdmin.declareQueue();
        Binding binding = BindingBuilder.bind(testQueue).to(rotahubEventsExchange).with("delivery.completed");
        amqpAdmin.declareBinding(binding);
    }

    @Test
    void createsTrackingWithAwaitingPickupStatus() throws Exception {
        HttpResponse<String> response = createTracking(UUID.randomUUID());

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).contains("\"status\":\"AWAITING_PICKUP\"");
    }

    @Test
    void rejectsDuplicateTrackingForSameOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        createTracking(orderId);

        HttpResponse<String> response = createTracking(orderId);

        assertThat(response.statusCode()).isEqualTo(409);
    }

    @Test
    void getsTrackingByOrderId() throws Exception {
        UUID orderId = UUID.randomUUID();
        createTracking(orderId);

        HttpResponse<String> response = get("/trackings/" + orderId);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains(orderId.toString());
    }

    @Test
    void returnsNotFoundForUnknownOrder() throws Exception {
        HttpResponse<String> response = get("/trackings/" + UUID.randomUUID());

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    void addsEventAndUpdatesStatus() throws Exception {
        UUID orderId = UUID.randomUUID();
        createTracking(orderId);

        HttpResponse<String> response = post("/trackings/" + orderId + "/events",
            "{\"status\":\"PICKED_UP\",\"timestamp\":\"2026-08-10T18:00:00Z\",\"note\":\"picked up\"}");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"PICKED_UP\"");
    }

    @Test
    void rejectsInvalidStatusValue() throws Exception {
        UUID orderId = UUID.randomUUID();
        createTracking(orderId);

        HttpResponse<String> response = post("/trackings/" + orderId + "/events",
            "{\"status\":\"NOT_A_STATUS\",\"timestamp\":\"2026-08-10T18:00:00Z\"}");

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    void publishesDeliveryCompletedEventWhenStatusIsDelivered() throws Exception {
        UUID orderId = UUID.randomUUID();
        createTracking(orderId);

        HttpResponse<String> response = post("/trackings/" + orderId + "/events",
            "{\"status\":\"DELIVERED\",\"position\":{\"lat\":-23.55,\"lng\":-46.63},\"timestamp\":\"2026-08-10T20:00:00Z\"}");

        assertThat(response.statusCode()).isEqualTo(200);

        Object payload = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);

        assertThat(payload).isInstanceOf(DeliveryCompletedEvent.class);
        DeliveryCompletedEvent event = (DeliveryCompletedEvent) payload;
        assertThat(event.orderId()).isEqualTo(orderId);
        assertThat(event.eventType()).isEqualTo("delivery.completed");
    }

    private HttpResponse<String> createTracking(UUID orderId) throws Exception {
        return post("/trackings", "{\"orderId\":\"" + orderId + "\"}");
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl() + path)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
