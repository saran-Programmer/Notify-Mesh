package com.notifymesh.notificationservice.dto;

import com.notifymesh.notificationservice.domain.valueobject.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateMetadata {

    private String templateName;

    private String channelType;

    private TemplateType templateType;
}
