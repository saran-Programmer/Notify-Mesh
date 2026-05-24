package com.notifymesh.notificationservice.controller;

import com.notifymesh.notificationservice.domain.service.NotificationService;
import com.notifymesh.notificationservice.dto.NotificationRequest;
import com.notifymesh.notificationservice.dto.NotificationResponse;
import com.notifymesh.notificationservice.dto.UpdateNotificationRequest;
import com.notifymesh.notificationservice.validator.NotificationRequestValidator;
import com.notifymesh.notificationservice.validator.UpdateNotificationRequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRequestValidator notificationRequestValidator;
    private final UpdateNotificationRequestValidator updateNotificationRequestValidator;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        notificationRequestValidator.validate(request);
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/external/{externalId}")
    public ResponseEntity<NotificationResponse> getNotificationByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(notificationService.getNotificationByExternalId(externalId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Long id, @Valid @RequestBody UpdateNotificationRequest request) {
        updateNotificationRequestValidator.validate(request);
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
