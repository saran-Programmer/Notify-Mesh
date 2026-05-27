package com.notifymesh.workerservice.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifymesh.workerservice.dto.NotificationEvent;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class NotificationEventDeserializer implements Deserializer<NotificationEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NotificationEvent deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, NotificationEvent.class);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize NotificationEvent", e);
        }
    }
}
