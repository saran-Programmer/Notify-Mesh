package com.notifymesh.notificationservice.domain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notifymesh.notificationservice.domain.valueobject.Channel;
import com.notifymesh.notificationservice.domain.valueobject.DeliveryType;
import com.notifymesh.notificationservice.domain.valueobject.Mode;
import com.notifymesh.notificationservice.domain.valueobject.NotificationStatus;
import com.notifymesh.notificationservice.domain.valueobject.TemplateType;
import com.notifymesh.notificationservice.dto.NotificationEvent;
import com.notifymesh.notificationservice.dto.NotificationRequest;
import com.notifymesh.notificationservice.dto.NotificationResponse;
import com.notifymesh.notificationservice.dto.TemplateMetadata;
import com.notifymesh.notificationservice.dto.UpdateNotificationRequest;
import com.notifymesh.notificationservice.exception.DuplicateExternalIdException;
import com.notifymesh.notificationservice.exception.NotFoundException;
import com.notifymesh.notificationservice.exception.ValidationException;
import com.notifymesh.notificationservice.infrastructure.client.EventBridgeSchedulerService;
import com.notifymesh.notificationservice.infrastructure.client.TemplateServiceClient;
import com.notifymesh.notificationservice.infrastructure.entity.Notification;
import com.notifymesh.notificationservice.infrastructure.messaging.kafka.NotificationKafkaProducer;
import com.notifymesh.notificationservice.infrastructure.repository.NotificationRepository;
import com.notifymesh.notificationservice.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationPersistenceService notificationPersistenceService;

    private final NotificationKafkaProducer notificationKafkaProducer;

    private final EventBridgeSchedulerService eventBridgeSchedulerService;

    private final TemplateServiceClient templateServiceClient;

    public NotificationResponse createNotification(NotificationRequest request) {

        TemplateMetadata templateMetadata = null;

        if (request.getMode() == Mode.TEMPLATE) {

            templateMetadata = templateServiceClient.templateExists(
                    request.getTemplateName(), request.getChannel().name());

            if (templateMetadata.getTemplateType() == TemplateType.HTML && request.getChannel() != Channel.EMAIL) {

                throw new ValidationException(
                        "HTML template type is not supported for channel '" + request.getChannel() + "'. Only TEXT is allowed.");
            }
        }

        boolean isHtmlContent = templateMetadata != null && templateMetadata.getTemplateType() == TemplateType.HTML;

        Notification saved = notificationPersistenceService.save(request);

        NotificationEvent event = NotificationMapper.toNotificationEvent(saved, isHtmlContent);

        if (saved.getDeliveryType() == DeliveryType.DIRECT) {

            notificationKafkaProducer.publish(event);
        } else {

            eventBridgeSchedulerService.schedule(saved.getId(), saved.getScheduledAt(), event);
        }

        return NotificationMapper.toNotificationResponse(saved);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        return notificationRepository.findSummaryById(id)
                .map(NotificationMapper::toNotificationResponse)
                .orElseThrow(() -> new NotFoundException("notification with id '" + id + "' not found"));
    }

    @Transactional
    public NotificationResponse updateNotification(Long id, UpdateNotificationRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("notification with id '" + id + "' not found"));

        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new ValidationException(
                    "notification cannot be updated in status '" + notification.getStatus() + "'");
        }

        if (request.getExternalId() != null
                && notificationRepository.existsByExternalIdAndIdNot(request.getExternalId(), id)) {
            throw new DuplicateExternalIdException(
                    "notification with externalId '" + request.getExternalId() + "' already exists");
        }

        if (request.getSubject() != null && !Channel.EMAIL.name().equals(notification.getChannelType().getName())) {
            throw new ValidationException("subject can only be updated for EMAIL channel");
        }

        if (request.getSubject() != null && request.getSubject().isBlank()
                && Channel.EMAIL.name().equals(notification.getChannelType().getName())) {
            throw new ValidationException("subject must not be blank for EMAIL channel");
        }

        if (request.getContent() != null && notification.getMode() == Mode.TEMPLATE) {
            throw new ValidationException("content cannot be updated when mode is TEMPLATE");
        }

        if (request.getScheduledAt() != null && notification.getDeliveryType() == DeliveryType.DIRECT) {
            throw new ValidationException("scheduledAt cannot be updated when deliveryType is DIRECT");
        }

        if (request.getExternalId() != null) {
            notification.setExternalId(request.getExternalId());
        }
        if (request.getRecipient() != null) {
            notification.setRecipient(request.getRecipient());
        }
        if (request.getSubject() != null) {
            notification.setSubject(request.getSubject());
        }
        if (request.getContent() != null) {
            notification.setContent(request.getContent());
        }
        if (request.getScheduledAt() != null) {
            notification.setScheduledAt(request.getScheduledAt());
        }

        notification.setLastModifiedDate(LocalDateTime.now());

        return NotificationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new NotFoundException("notification with id '" + id + "' not found");
        }
        notificationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationByExternalId(String externalId) {
        return notificationRepository.findSummaryByExternalId(externalId)
                .map(NotificationMapper::toNotificationResponse)
                .orElseThrow(() -> new NotFoundException("notification with externalId '" + externalId + "' not found"));
    }
}
