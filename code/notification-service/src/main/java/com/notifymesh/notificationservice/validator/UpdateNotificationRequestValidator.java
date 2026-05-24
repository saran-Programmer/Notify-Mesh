package com.notifymesh.notificationservice.validator;

import com.notifymesh.notificationservice.dto.UpdateNotificationRequest;
import com.notifymesh.notificationservice.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateNotificationRequestValidator {

    public void validate(UpdateNotificationRequest request) {
        validateAtLeastOneFieldPresent(request);
        validateNoBlankStrings(request);
        validateScheduledAt(request);
    }

    private void validateAtLeastOneFieldPresent(UpdateNotificationRequest request) {
        boolean hasRecipient = request.getRecipient() != null && !request.getRecipient().isBlank();
        boolean hasSubject = request.getSubject() != null && !request.getSubject().isBlank();
        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasScheduledAt = request.getScheduledAt() != null;
        boolean hasExternalId = request.getExternalId() != null && !request.getExternalId().isBlank();

        if (!hasRecipient && !hasSubject && !hasContent && !hasScheduledAt && !hasExternalId) {
            throw new ValidationException("at least one field must be provided for update");
        }
    }

    private void validateNoBlankStrings(UpdateNotificationRequest request) {
        if (request.getRecipient() != null && request.getRecipient().isBlank()) {
            throw new ValidationException("recipient must not be blank");
        }
        if (request.getSubject() != null && request.getSubject().isBlank()) {
            throw new ValidationException("subject must not be blank");
        }
        if (request.getContent() != null && request.getContent().isBlank()) {
            throw new ValidationException("content must not be blank");
        }
        if (request.getExternalId() != null && request.getExternalId().isBlank()) {
            throw new ValidationException("externalId must not be blank");
        }
    }

    private void validateScheduledAt(UpdateNotificationRequest request) {
        if (request.getScheduledAt() != null && request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("scheduledAt must be in the future");
        }
    }
}
