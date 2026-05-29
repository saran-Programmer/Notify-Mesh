package com.notifymesh.notificationservice.infrastructure.entity;

import com.notifymesh.notificationservice.domain.valueobject.AuditStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "notification")
@EqualsAndHashCode(exclude = "notification")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(nullable = false)
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status;

    @Column
    private String clientErrorMessage;

    private String workerId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
