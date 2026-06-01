package com.notifymesh.templateservice.infrastructure.entity;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.domain.valueobject.TemplateType;
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
public class Template {

    private String templateName;

    private Integer version;

    private Channel channelType;

    private TemplateType templateType;

    private String subject;

    private String body;

    private Boolean isActive;

    private List<TemplateAttachment> defaultAttachment;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
