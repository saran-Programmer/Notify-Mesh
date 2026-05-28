package com.notifymesh.workerservice.infrastructure.messaging.kafka;

import com.notifymesh.workerservice.config.KafkaConstants;
import com.notifymesh.workerservice.dto.NotificationEvent;
import com.notifymesh.workerservice.infrastructure.queue.WorkerQueue;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final WorkerQueue workerQueue;

    @KafkaListener(topics = "${kafka.topics.high}", containerFactory = KafkaConstants.CONTAINER_FACTORY)
    public void consumeHigh(NotificationEvent event) {
        workerQueue.enqueue(event);
    }

    @KafkaListener(topics = "${kafka.topics.medium}", containerFactory = KafkaConstants.CONTAINER_FACTORY)
    public void consumeMedium(NotificationEvent event) {
        workerQueue.enqueue(event);
    }

    @KafkaListener(topics = "${kafka.topics.low}", containerFactory = KafkaConstants.CONTAINER_FACTORY)
    public void consumeLow(NotificationEvent event) {
        workerQueue.enqueue(event);
    }
}
