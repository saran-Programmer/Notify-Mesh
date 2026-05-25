package com.notifymesh.notificationservice.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifymesh.notificationservice.dto.NotificationEvent;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class NotificationEventSerializer implements Serializer<NotificationEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, NotificationEvent data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize NotificationEvent", e);
        }
    }
}
