package com.notifymesh.notificationservice.infrastructure.repository;

import com.notifymesh.notificationservice.infrastructure.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByExternalId(String externalId);

    boolean existsByExternalIdAndIdNot(String externalId, Long id);

    Optional<NotificationSummary> findSummaryById(Long id);

    Optional<NotificationSummary> findSummaryByExternalId(String externalId);
}
