package com.notifymesh.workerservice.infrastructure.repository;

import com.notifymesh.workerservice.infrastructure.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
