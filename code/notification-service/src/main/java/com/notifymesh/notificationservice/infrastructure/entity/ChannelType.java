package com.notifymesh.notificationservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer defaultRetryCount;

    @Column(nullable = false)
    private Integer rateLimitPerMin;

    @Column(nullable = false)
    private Integer retryDefaultMultiplier;
   
    @Column(nullable = false)
    private Integer baseRetryDelaySeconds;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;
}
