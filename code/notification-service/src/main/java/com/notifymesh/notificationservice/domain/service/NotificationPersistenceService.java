package com.notifymesh.notificationservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notifymesh.notificationservice.dto.AttachmentRequest;
import com.notifymesh.notificationservice.dto.NotificationRequest;
import com.notifymesh.notificationservice.exception.DuplicateExternalIdException;
import com.notifymesh.notificationservice.infrastructure.entity.Attachment;
import com.notifymesh.notificationservice.infrastructure.entity.ChannelType;
import com.notifymesh.notificationservice.infrastructure.entity.Notification;
import com.notifymesh.notificationservice.infrastructure.entity.PriorityTable;
import com.notifymesh.notificationservice.infrastructure.repository.NotificationRepository;
import com.notifymesh.notificationservice.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPersistenceService {

    private final NotificationRepository notificationRepository;

    private final ReferenceDataService referenceDataService;

    private final AttachmentService attachmentService;

    @Transactional
    public Notification save(NotificationRequest request) {

        if (notificationRepository.existsByExternalId(request.getExternalId())) {

            throw new DuplicateExternalIdException(
                    "notification with externalId '" + request.getExternalId() + "' already exists");
        }

        ChannelType channelType = referenceDataService.getChannelByName(request.getChannel().name());

        PriorityTable priority = referenceDataService.getPriorityByName(request.getPriority().name());

        if (request.getMaxRetries() == null) {

            request.setMaxRetries(channelType.getDefaultRetryCount());
        }

        Notification notification = NotificationMapper.toNotification(request, channelType, priority);

        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {

            List<String> s3Keys = request.getAttachmentIds().stream()
                    .map(AttachmentRequest::getS3Key)
                    .toList();
            attachmentService.validateAndEvictAttachments(s3Keys);
            List<Attachment> attachments = request.getAttachmentIds().stream()
                    .map(a -> NotificationMapper.toAttachment(a, notification))
                    .toList();
            notification.setAttachments(attachments);
        }

        return notificationRepository.save(notification);
    }
}
