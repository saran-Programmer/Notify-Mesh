package com.notifymesh.workerservice.mapper;

import com.notifymesh.workerservice.domain.valueobject.LogStatus;
import com.notifymesh.workerservice.dto.FailureContext;
import com.notifymesh.workerservice.dto.NotificationEvent;
import com.notifymesh.workerservice.infrastructure.entity.AuditLog;

import java.time.LocalDateTime;

public class WorkerMapper {

    private static final int BACKOFF_MULTIPLIER = 2;

    private WorkerMapper() {}

    public static FailureContext toFailureContext(NotificationEvent event, Exception ex) {
        return FailureContext.builder()
                .notificationId(event.getId())
                .attemptNumber(event.getRetryCount() + 1)
                .errorMessage(ex.getMessage())
                .retryCount(event.getRetryCount())
                .maxRetries(event.getMaxRetries())
                .baseDelaySeconds(event.getBaseDelaySeconds())
                .multiplier(BACKOFF_MULTIPLIER)
                .build();
    }

    public static AuditLog toFailureAuditLog(FailureContext context, LocalDateTime now) {
        return AuditLog.builder()
                .notificationId(context.getNotificationId())
                .attemptNumber(context.getAttemptNumber())
                .status(LogStatus.FAILURE)
                .clientErrorMessage(context.getErrorMessage())
                .createdAt(now)
                .build();
    }

    public static AuditLog toSuccessAuditLog(Long notificationId, Integer attemptNumber, LocalDateTime now) {
        return AuditLog.builder()
                .notificationId(notificationId)
                .attemptNumber(attemptNumber)
                .status(LogStatus.SUCCESS)
                .createdAt(now)
                .build();
    }
}
