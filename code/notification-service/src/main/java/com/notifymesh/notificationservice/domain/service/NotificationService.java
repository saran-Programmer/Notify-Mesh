package com.notifymesh.notificationservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notifymesh.notificationservice.domain.valueobject.NotificationStatus;
import com.notifymesh.notificationservice.dto.NotificationRequest;
import com.notifymesh.notificationservice.dto.NotificationResponse;
import com.notifymesh.notificationservice.exception.DuplicateExternalIdException;
import com.notifymesh.notificationservice.exception.NotFoundException;
import com.notifymesh.notificationservice.infrastructure.entity.Attachment;
import com.notifymesh.notificationservice.infrastructure.entity.ChannelType;
import com.notifymesh.notificationservice.infrastructure.entity.Notification;
import com.notifymesh.notificationservice.infrastructure.entity.PriorityTable;
import com.notifymesh.notificationservice.infrastructure.repository.ChannelTypeRepository;
import com.notifymesh.notificationservice.infrastructure.repository.NotificationRepository;
import com.notifymesh.notificationservice.infrastructure.repository.PriorityRepository;
import com.notifymesh.notificationservice.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ChannelTypeRepository channelTypeRepository;
    private final PriorityRepository priorityRepository;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {

        if (notificationRepository.existsByExternalId(request.getExternalId())) {
            throw new DuplicateExternalIdException(
                    "notification with externalId '" + request.getExternalId() + "' already exists");
        }

        ChannelType channelType = channelTypeRepository.findByName(request.getChannel().name())
                .orElseThrow(() -> new NotFoundException(
                        "channel type '" + request.getChannel().name() + "' not configured"));

        PriorityTable priority = priorityRepository.findByName(request.getPriority().name())
                .orElseThrow(() -> new NotFoundException(
                        "priority '" + request.getPriority().name() + "' not configured"));

        Notification notification = NotificationMapper.toNotification(request, channelType, priority);

        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = request.getAttachmentIds().stream()
                    .map(a -> NotificationMapper.toAttachment(a, notification))
                    .toList();
            notification.setAttachments(attachments);
        }

        Notification saved = notificationRepository.save(notification);

        return NotificationResponse.builder()
                .id(saved.getId())
                .externalId(saved.getExternalId())
                .status(NotificationStatus.PENDING)
                .build();
    }
}
