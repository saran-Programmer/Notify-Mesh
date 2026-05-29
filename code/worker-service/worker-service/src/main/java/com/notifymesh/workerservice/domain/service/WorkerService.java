package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.domain.service.delivery.DeliveryStrategyResolver;
import com.notifymesh.workerservice.dto.NotificationEvent;
import com.notifymesh.workerservice.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerService {

    private final AttachmentService attachmentService;

    private final NotificationUpdateService notificationUpdateService;

    private final DeliveryStrategyResolver deliveryStrategyResolver;

    public void handleNotification(NotificationEvent event) {

        List<FileAttachment> attachments;

        try {

            attachments = resolveAttachments(event);
            deliveryStrategyResolver.resolve(event.getChannel()).send(event, attachments);
            notificationUpdateService.updateSuccess(event.getId(), event.getRetryCount());

        } catch (Exception ex) {

            notificationUpdateService.updateFailure(WorkerMapper.toFailureContext(event, ex));
            return;
        }
    }

    private List<FileAttachment> resolveAttachments(NotificationEvent event) {
        List<String> urls = event.getAttachments();
        if (urls == null || urls.isEmpty()) {
            return Collections.emptyList();
        }
        return attachmentService.downloadAttachments(urls);
    }
}
