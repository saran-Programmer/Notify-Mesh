package com.notifymesh.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentResponse {

    private String s3Key;

    private String url;

    private String name;

    private String type;

    private Integer size;

    private boolean success;

    private String errorMessage;
}
