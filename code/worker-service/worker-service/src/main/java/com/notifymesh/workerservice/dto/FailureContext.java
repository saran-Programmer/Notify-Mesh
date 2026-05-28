package com.notifymesh.workerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FailureContext {

    private Long notificationId;

    private Integer attemptNumber;

    private String errorMessage;

    private Integer retryCount;

    private Integer maxRetries;

    private Integer baseDelaySeconds;

    private Integer multiplier;
}
