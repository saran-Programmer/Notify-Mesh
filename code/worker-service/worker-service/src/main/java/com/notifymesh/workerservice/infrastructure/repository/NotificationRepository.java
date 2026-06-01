package com.notifymesh.workerservice.infrastructure.repository;

import com.notifymesh.workerservice.domain.valueobject.NotificationStatus;
import com.notifymesh.workerservice.infrastructure.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.sentAt = :now, n.lastModifiedDate = :now WHERE n.id = :notificationId")
    void markAsSent(@Param("notificationId") Long notificationId,
                    @Param("now") LocalDateTime now,
                    @Param("status") NotificationStatus status);

    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.retryCount = :retryCount, n.nextRetryAt = :nextRetryAt, n.lastModifiedDate = :now WHERE n.id = :notificationId")
    void markAsRetry(@Param("notificationId") Long notificationId,
                     @Param("retryCount") Integer retryCount,
                     @Param("nextRetryAt") LocalDateTime nextRetryAt,
                     @Param("now") LocalDateTime now,
                     @Param("status") NotificationStatus status);

    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.lastModifiedDate = :now WHERE n.id = :notificationId")
    void markAsFailed(@Param("notificationId") Long notificationId,
                      @Param("now") LocalDateTime now,
                      @Param("status") NotificationStatus status);

    @Modifying
    @Query("UPDATE Notification n SET n.status = :status, n.processingStartedAt = :now, n.lastModifiedDate = :now WHERE n.id = :notificationId")
    int markAsProcessing(@Param("notificationId") Long notificationId,
                         @Param("now") LocalDateTime now,
                         @Param("status") NotificationStatus status);
}
