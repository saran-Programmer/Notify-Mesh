package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.infrastructure.client.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final S3Service s3Service;

    public List<FileAttachment> downloadAttachments(List<String> urls) {
        List<FileAttachment> attachments = new ArrayList<>();

        for (String url : urls) {
            String fileName = url.contains("/")
                    ? url.substring(url.lastIndexOf('/') + 1)
                    : url;

            byte[] bytes = s3Service.downloadFile(url);

            attachments.add(FileAttachment.builder()
                    .fileName(fileName)
                    .fileBytes(bytes)
                    .build());
        }

        return attachments;
    }
}
