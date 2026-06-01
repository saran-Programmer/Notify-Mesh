package com.notifymesh.templateservice.dto;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.domain.valueobject.TemplateType;
import com.notifymesh.templateservice.infrastructure.entity.TemplateAttachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateResponse {

    private String templateName;

    private Channel channelType;

    private TemplateType templateType;

    private Integer version;

    private String subject;

    private String body;

    private List<TemplateAttachment> defaultAttachment;

    private Boolean isActive;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
