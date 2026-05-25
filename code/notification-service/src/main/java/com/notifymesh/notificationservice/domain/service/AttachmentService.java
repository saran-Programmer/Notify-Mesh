package com.notifymesh.notificationservice.domain.service;

import com.notifymesh.notificationservice.constant.CacheNames;
import com.notifymesh.notificationservice.dto.AttachmentResponse;
import com.notifymesh.notificationservice.exception.AttachmentNotAccessibleException;
import com.notifymesh.notificationservice.infrastructure.client.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final S3Service s3Service;
    private final CacheManager cacheManager;

    public List<AttachmentResponse> uploadAttachments(List<MultipartFile> files) {
        List<AttachmentResponse> responses = new ArrayList<>();
        Cache attachmentCache = cacheManager.getCache(CacheNames.ATTACHMENT_URL);
        for (MultipartFile file : files) {
            try {
                String s3Key = s3Service.upload(file);
                if (attachmentCache != null) {
                    attachmentCache.put(s3Key, s3Key);
                }
                responses.add(AttachmentResponse.builder().s3Key(s3Key).success(true).build());
            } catch (Exception e) {
                responses.add(AttachmentResponse.builder().success(false)
                        .errorMessage(e.getMessage()).build());
            }
        }
        return responses;
    }

    public void validateAndEvictAttachments(List<String> s3Keys) {
        Cache attachmentCache = cacheManager.getCache(CacheNames.ATTACHMENT_URL);
        for (String s3Key : s3Keys) {
            Cache.ValueWrapper cached = attachmentCache != null ? attachmentCache.get(s3Key) : null;
            if (cached == null) {
                throw new AttachmentNotAccessibleException(
                        "attachment '" + s3Key + "' was not uploaded via this service or has already been consumed");
            }
        }
        if (attachmentCache != null) {
            s3Keys.forEach(attachmentCache::evict);
        }
    }
}
