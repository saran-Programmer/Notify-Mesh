package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.domain.valueobject.LogStatus;
import com.notifymesh.workerservice.domain.valueobject.NotificationStatus;
import com.notifymesh.workerservice.dto.FailureContext;
import com.notifymesh.workerservice.infrastructure.entity.AuditLog;
import com.notifymesh.workerservice.infrastructure.entity.Notification;
import com.notifymesh.workerservice.infrastructure.repository.AuditLogRepository;
import com.notifymesh.workerservice.infrastructure.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationUpdateService {

    private final NotificationRepository notificationRepository;

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void updateFailure(FailureContext context) {

        Notification notification = notificationRepository
                .findById(context.getNotificationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found: id=" + context.getNotificationId()));

        boolean canRetry = context.getRetryCount() < context.getMaxRetries();

        if (canRetry) {
            LocalDateTime nextRetryAt = calculateNextRetryAt(
                    context.getRetryCount(),
                    context.getBaseDelaySeconds(),
                    context.getMultiplier());

            notification.setStatus(NotificationStatus.RETRY);
            notification.setRetryCount(context.getRetryCount() + 1);
            notification.setNextRetryAt(nextRetryAt);
        } else {
            notification.setStatus(NotificationStatus.FAILED);
        }

        notification.setLastModifiedDate(LocalDateTime.now());
        notificationRepository.save(notification);

        AuditLog auditLog = AuditLog.builder()
                .notificationId(context.getNotificationId())
                .attemptNumber(context.getAttemptNumber())
                .status(LogStatus.FAILURE)
                .clientErrorMessage(context.getErrorMessage())
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    private LocalDateTime calculateNextRetryAt(Integer retryCount, Integer baseDelaySeconds, Integer multiplier) {
        long delaySeconds = (long) baseDelaySeconds
                * (long) Math.pow(multiplier, retryCount);

        return LocalDateTime.now().plusSeconds(delaySeconds);
    }
}
