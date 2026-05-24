package com.notifymesh.notificationservice.infrastructure.repository;

import com.notifymesh.notificationservice.domain.valueobject.NotificationStatus;

import java.time.LocalDateTime;

public interface NotificationSummary {

    Long getId();

    String getExternalId();

    NotificationStatus getStatus();

    LocalDateTime getSentAt();

    LocalDateTime getScheduledAt();
}
