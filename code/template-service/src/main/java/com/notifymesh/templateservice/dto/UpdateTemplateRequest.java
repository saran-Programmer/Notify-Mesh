package com.notifymesh.templateservice.dto;

import com.notifymesh.templateservice.domain.valueobject.TemplateType;
import com.notifymesh.templateservice.infrastructure.entity.TemplateAttachment;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTemplateRequest {

    private TemplateType templateType;

    private String subject;

    @NotBlank
    private String body;

    private List<TemplateAttachment> defaultAttachment;
}
