package com.notifymesh.notificationservice.mapper;

import com.notifymesh.notificationservice.domain.valueobject.AuditStatus;
import com.notifymesh.notificationservice.domain.valueobject.Channel;
import com.notifymesh.notificationservice.domain.valueobject.NotificationStatus;
import com.notifymesh.notificationservice.domain.valueobject.Priority;
import com.notifymesh.notificationservice.dto.AttachmentRequest;
import com.notifymesh.notificationservice.dto.NotificationEvent;
import com.notifymesh.notificationservice.dto.NotificationRequest;
import com.notifymesh.notificationservice.dto.NotificationResponse;
import com.notifymesh.notificationservice.infrastructure.entity.AuditLog;
import com.notifymesh.notificationservice.infrastructure.entity.Attachment;
import com.notifymesh.notificationservice.infrastructure.entity.ChannelType;
import com.notifymesh.notificationservice.infrastructure.entity.Notification;
import com.notifymesh.notificationservice.infrastructure.entity.PriorityTable;
import com.notifymesh.notificationservice.infrastructure.repository.NotificationSummary;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationMapper {

    private NotificationMapper() {}

    public static Notification toNotification(NotificationRequest request,
                                              ChannelType channelType,
                                              PriorityTable priority) {
        LocalDateTime now = LocalDateTime.now();
        return Notification.builder()
                .recipient(request.getRecipient())
                .channelType(channelType)
                .priority(priority)
                .mode(request.getMode())
                .deliveryType(request.getDeliveryType())
                .subject(request.getSubject())
                .content(request.getContent())
                .templateName(request.getTemplateName())
                .parameters(request.getParameter())
                .status(NotificationStatus.PENDING)
                .scheduledAt(request.getScheduledAt())
                .retryCount(0)
                .maxRetries(request.getMaxRetries())
                .externalId(request.getExternalId())
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    public static Attachment toAttachment(AttachmentRequest request, Notification notification) {
        LocalDateTime now = LocalDateTime.now();
        return Attachment.builder()
                .notification(notification)
                .name(request.getName())
                .type(request.getType())
                .s3Key(request.getS3Key())
                .size(request.getSize())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();
    }

    public static NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .externalId(notification.getExternalId())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .scheduledAt(notification.getScheduledAt())
                .build();
    }

    public static NotificationResponse toNotificationResponse(NotificationSummary summary) {
        return NotificationResponse.builder()
                .id(summary.getId())
                .externalId(summary.getExternalId())
                .status(summary.getStatus())
                .sentAt(summary.getSentAt())
                .scheduledAt(summary.getScheduledAt())
                .build();
    }

    public static NotificationEvent toNotificationEvent(Notification notification) {
        Map<String, String> stringParams = notification.getParameters() == null
                ? null
                : notification.getParameters().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));

        List<String> attachmentKeys = notification.getAttachments() == null
                ? Collections.emptyList()
                : notification.getAttachments().stream()
                        .map(Attachment::getS3Key)
                        .toList();

        return NotificationEvent.builder()
                .id(notification.getId())
                .channel(Channel.valueOf(notification.getChannelType().getName()))
                .priority(Priority.valueOf(notification.getPriority().getName()))
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .templateName(notification.getTemplateName())
                .parameters(stringParams)
                .retryCount(notification.getRetryCount())
                .attachments(attachmentKeys)
                .mode(notification.getMode())
                .baseDelaySeconds(notification.getPriority().getBaseDelaySeconds())
                .build();
    }

    public static AuditLog toAuditLog(Notification notification, AuditStatus status) {
        return AuditLog.builder()
                .notification(notification)
                .attemptNumber(1)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
