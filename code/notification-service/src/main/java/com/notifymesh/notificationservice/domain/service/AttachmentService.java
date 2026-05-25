package com.notifymesh.notificationservice.domain.service;

import com.notifymesh.notificationservice.dto.AttachmentResponse;
import com.notifymesh.notificationservice.infrastructure.client.S3Service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final S3Service s3Service;

    public List<AttachmentResponse> uploadAttachments(List<MultipartFile> files) {
        List<AttachmentResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String s3Key = s3Service.upload(file);
                responses.add(AttachmentResponse.builder().s3Key(s3Key).success(true).build());
            } catch (Exception e) {
                responses.add(AttachmentResponse.builder().success(false)
                .errorMessage(e.getMessage()).build());
            }
        }
        return responses;
    }
}
