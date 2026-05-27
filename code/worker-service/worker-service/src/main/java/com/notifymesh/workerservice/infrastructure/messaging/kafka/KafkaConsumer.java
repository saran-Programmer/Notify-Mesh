package com.notifymesh.workerservice.infrastructure.messaging.kafka;

import com.notifymesh.workerservice.config.KafkaConstants;
import com.notifymesh.workerservice.dto.NotificationEvent;
import com.notifymesh.workerservice.infrastructure.queue.WorkerQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private WorkerQueue workerQueue;

    @KafkaListener(topics = "${kafka.topics.high}", containerFactory = KafkaConstants.CONTAINER_FACTORY)
    public void consumeHigh(NotificationEvent event) {

        log.info("Received HIGH priority notification event: id={}", event.getId());
        workerQueue.enqueue(event);
    }

    @KafkaListener(topics = "${kafka.topics.medium}", containerFactory = KafkaConstants.CONTAINER_FACTORY)
    public void consumeMedium(NotificationEvent event) {
        log.info("Received MEDIUM priority notification event: id={}", event.getId());
        workerQueue.enqueue(event);
    }

    @KafkaListener(topics = "${kafka.topics.low}", containerFactory = KafkaConstants.CONTAINER_FACTORY)
    public void consumeLow(NotificationEvent event) {
        log.info("Received LOW priority notification event: id={}", event.getId());
        workerQueue.enqueue(event);
    }
}
