package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final AttachmentService attachmentService;

    public void handleNotification(NotificationEvent event) {
        try {
            List<FileAttachment> attachments = resolveAttachments(event);
         } catch (Exception ex) {
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
