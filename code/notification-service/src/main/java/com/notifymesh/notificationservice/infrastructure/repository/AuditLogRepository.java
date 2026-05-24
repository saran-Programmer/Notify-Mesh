package com.notifymesh.notificationservice.infrastructure.repository;

import com.notifymesh.notificationservice.infrastructure.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
