package com.notifymesh.workerservice.infrastructure.repository;

import com.notifymesh.workerservice.infrastructure.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
