package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.infrastructure.client.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain service responsible for resolving attachment S3 keys into
 * in-memory {@link FileAttachment} objects.
 *
 * <p>For each S3 key supplied by a {@code NotificationEvent}, this service
 * delegates to {@link S3Service} to fetch the raw bytes and wraps the result
 * in a {@link FileAttachment} ready for downstream channel handlers
 * (e.g. email attachment, SMS MMS payload).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final S3Service s3Service;

    /**
     * Downloads every S3 object referenced in {@code urls} (S3 keys)
     * and returns them as a list of {@link FileAttachment} instances,
     * preserving the original order.
     *
     * @param urls list of S3 keys (e.g. {@code attachments/uuid/file.pdf})
     * @return ordered list of downloaded attachments
     */
    public List<FileAttachment> downloadAttachments(List<String> urls) {
        log.debug("Downloading {} attachment(s)", urls.size());

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
