package com.notifymesh.notificationservice.controller;

import com.notifymesh.notificationservice.domain.service.AttachmentService;
import com.notifymesh.notificationservice.dto.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<List<AttachmentResponse>> uploadAttachments(@RequestParam List<MultipartFile> files) {

        return ResponseEntity.ok(attachmentService.uploadAttachments(files));
    }
}
