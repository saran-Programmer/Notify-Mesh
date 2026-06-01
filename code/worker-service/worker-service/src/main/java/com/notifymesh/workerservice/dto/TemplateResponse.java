package com.notifymesh.workerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateResponse {

    private String templateName;

    private String channelType;

    private Integer version;

    private String subject;

    private String body;

    private String templateType;

    private Boolean isActive;
}
