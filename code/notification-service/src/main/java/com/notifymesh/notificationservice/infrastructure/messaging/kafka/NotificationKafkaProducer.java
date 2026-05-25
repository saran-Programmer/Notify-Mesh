package com.notifymesh.notificationservice.infrastructure.messaging.kafka;

import com.notifymesh.notificationservice.config.KafkaProperties;
import com.notifymesh.notificationservice.domain.valueobject.Priority;
import com.notifymesh.notificationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publish(NotificationEvent event) {
        String topic = resolveTopic(event.getPriority());
        kafkaTemplate.send(topic, event.getId().toString(), event);
    }

    private String resolveTopic(Priority priority) {
        return switch (priority) {
            case HIGH -> kafkaProperties.getTopics().getHigh();
            case MEDIUM -> kafkaProperties.getTopics().getMedium();
            case LOW -> kafkaProperties.getTopics().getLow();
        };
    }
}
