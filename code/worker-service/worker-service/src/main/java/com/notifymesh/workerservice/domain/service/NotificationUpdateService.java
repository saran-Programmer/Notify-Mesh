package com.notifymesh.workerservice.domain.service;

import com.notifymesh.workerservice.dto.FailureContext;
import com.notifymesh.workerservice.infrastructure.repository.AuditLogRepository;
import com.notifymesh.workerservice.infrastructure.repository.NotificationRepository;
import com.notifymesh.workerservice.mapper.WorkerMapper;
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
        
        LocalDateTime now = LocalDateTime.now();
        boolean canRetry = context.getRetryCount() < context.getMaxRetries();

        if (canRetry) {
            LocalDateTime nextRetryAt = calculateNextRetryAt(
                    context.getRetryCount(),
                    context.getBaseDelaySeconds(),
                    context.getMultiplier());
            notificationRepository.markAsRetry(context.getNotificationId(), context.getRetryCount() + 1, nextRetryAt, now);
        } else {
            notificationRepository.markAsFailed(context.getNotificationId(), now);
        }

        auditLogRepository.save(WorkerMapper.toFailureAuditLog(context, now));
    }

    @Transactional
    public void updateSuccess(Long notificationId, Integer attemptNumber) {
        LocalDateTime now = LocalDateTime.now();

        notificationRepository.markAsSent(notificationId, now);

        auditLogRepository.save(WorkerMapper.toSuccessAuditLog(notificationId, attemptNumber, now));
    }

    private LocalDateTime calculateNextRetryAt(Integer retryCount, Integer baseDelaySeconds, Integer multiplier) {
        long delaySeconds = (long) baseDelaySeconds
                * (long) Math.pow(multiplier, retryCount);

        return LocalDateTime.now().plusSeconds(delaySeconds);
    }
}
