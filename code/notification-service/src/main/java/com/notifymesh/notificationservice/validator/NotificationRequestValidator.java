package com.notifymesh.notificationservice.validator;

import com.notifymesh.notificationservice.domain.valueobject.Channel;
import com.notifymesh.notificationservice.domain.valueobject.DeliveryType;
import com.notifymesh.notificationservice.domain.valueobject.Mode;
import com.notifymesh.notificationservice.dto.NotificationRequest;
import com.notifymesh.notificationservice.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationRequestValidator {

    public void validate(NotificationRequest request) {
        validateChannelRules(request);
        validateModeRules(request);
        validateDeliveryTypeRules(request);
    }

    private void validateChannelRules(NotificationRequest request) {
        if (request.getChannel() == Channel.EMAIL) {
            if (request.getSubject() == null || request.getSubject().isBlank()) {
                throw new ValidationException("subject is required for EMAIL channel");
            }
        } else {
            if (request.getSubject() != null && !request.getSubject().isBlank()) {
                throw new ValidationException("subject is not allowed for " + request.getChannel() + " channel");
            }
        }
    }

    private void validateModeRules(NotificationRequest request) {
        if (request.getMode() == Mode.TEMPLATE) {
            if (request.getTemplateName() == null || request.getTemplateName().isBlank()) {
                throw new ValidationException("templateName is required when mode is TEMPLATE");
            }
            if (request.getContent() != null && !request.getContent().isBlank()) {
                throw new ValidationException("content must not be provided when mode is TEMPLATE");
            }
        } else {
            if (request.getContent() == null || request.getContent().isBlank()) {
                throw new ValidationException("content is required when mode is RAW");
            }
            if (request.getTemplateName() != null && !request.getTemplateName().isBlank()) {
                throw new ValidationException("templateName must not be provided when mode is RAW");
            }
            if (request.getParameter() != null && !request.getParameter().isEmpty()) {
                throw new ValidationException("parameter must not be provided when mode is RAW");
            }
        }
    }

    private void validateDeliveryTypeRules(NotificationRequest request) {
        if (request.getDeliveryType() == DeliveryType.SCHEDULED) {
            if (request.getScheduledAt() == null) {
                throw new ValidationException("scheduledAt is required when deliveryType is SCHEDULED");
            }
            if (request.getScheduledAt().isBefore(LocalDateTime.now())) {
                throw new ValidationException("scheduledAt must be in the future");
            }
        } else {
            if (request.getScheduledAt() != null) {
                throw new ValidationException("scheduledAt must not be provided when deliveryType is DIRECT");
            }
        }
    }
}
