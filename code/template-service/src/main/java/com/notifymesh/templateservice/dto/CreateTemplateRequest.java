package com.notifymesh.templateservice.dto;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.infrastructure.entity.TemplateAttachment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTemplateRequest {

    @NotBlank
    private String templateName;

    @NotNull
    private Channel channelType;

    private String subject;

    @NotBlank
    private String body;

    private List<TemplateAttachment> defaultAttachment;
}
