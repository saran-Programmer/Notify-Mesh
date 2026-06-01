package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.domain.service.delivery.DeliveryStrategyResolver;
import com.notifymesh.workerservice.domain.valueobject.Channel;
import com.notifymesh.workerservice.domain.valueobject.Mode;
import com.notifymesh.workerservice.dto.NotificationEvent;
import com.notifymesh.workerservice.dto.TemplateResponse;
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

    private final TemplateCacheService templateCacheService;

    private final TemplateRenderer templateRenderer;

    public void handleNotification(NotificationEvent event) {

        boolean marked = notificationUpdateService.markAsProcessing(event.getId());

        if (!marked) {

            log.warn("Notification {} not found, skipping delivery", event.getId());
            return;
        }

        List<FileAttachment> attachments;

        try {

            String renderedContent = resolveContent(event);
            event.setContent(renderedContent);

            attachments = resolveAttachments(event);
            deliveryStrategyResolver.resolve(event.getChannel()).send(event, attachments);
            notificationUpdateService.updateSuccess(event.getId(), event.getRetryCount());

        } catch (Exception ex) {

            notificationUpdateService.updateFailure(WorkerMapper.toFailureContext(event, ex));
        }
    }

    private String resolveContent(NotificationEvent event) {

        if (event.getMode() == Mode.RAW) return event.getContent();
        
        TemplateResponse template = templateCacheService.getTemplate(
                event.getTemplateName(), event.getChannel().name());

        return templateRenderer.render(template.getBody(), event.getParameters());
    }

    private List<FileAttachment> resolveAttachments(NotificationEvent event) {
        List<String> urls = event.getAttachments();
        if (urls == null || urls.isEmpty()) {
            return Collections.emptyList();
        }
        return attachmentService.downloadAttachments(urls);
    }
}
