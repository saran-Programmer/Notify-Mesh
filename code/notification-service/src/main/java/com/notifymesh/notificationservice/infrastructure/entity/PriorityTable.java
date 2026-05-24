package com.notifymesh.notificationservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "priority")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriorityTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer value;

    @Column(nullable = false)
    private Integer maxDelaySeconds;

    @Column(nullable = false)
    private Integer baseDelaySeconds;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;
}
