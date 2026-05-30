package com.notifymesh.templateservice.domain.service;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.dto.CreateTemplateRequest;
import com.notifymesh.templateservice.dto.TemplateResponse;
import com.notifymesh.templateservice.dto.UpdateTemplateRequest;
import com.notifymesh.templateservice.exception.TemplateDuplicateException;
import com.notifymesh.templateservice.exception.TemplateNotFoundException;
import com.notifymesh.templateservice.infrastructure.entity.Template;
import com.notifymesh.templateservice.infrastructure.repository.TemplateRepository;
import com.notifymesh.templateservice.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateResponse getTemplate(String templateName, Channel channelType, Integer version, boolean isDeletedIncluded) {
        Optional<Template> templateOpt = version != null
                ? templateRepository.findByTemplateNameAndChannelTypeAndVersion(templateName, channelType, version)
                : templateRepository.findByTemplateNameAndChannelType(templateName, channelType);

        Template template = templateOpt.orElseThrow(() ->
                new TemplateNotFoundException("Template not found: " + templateName + " / " + channelType));

        if (!isDeletedIncluded && !template.getIsActive()) {
            throw new TemplateNotFoundException("Template not found: " + templateName + " / " + channelType);
        }

        return TemplateMapper.toResponse(template);
    }

    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        templateRepository.findByTemplateNameAndChannelType(request.getTemplateName(), request.getChannelType())
                .ifPresent(existing -> {
                    throw new TemplateDuplicateException(
                            "Template already exists: " + request.getTemplateName() + " / " + request.getChannelType());
                });

        Template template = TemplateMapper.toTemplate(request);
        LocalDateTime now = LocalDateTime.now();
        template.setVersion(1);
        template.setIsActive(true);
        template.setCreatedDate(now);
        template.setLastModifiedDate(now);

        templateRepository.save(template);
        return TemplateMapper.toResponse(template);
    }

    public TemplateResponse updateTemplate(String templateName, Channel channelType, UpdateTemplateRequest request) {
        Template existing = templateRepository.findByTemplateNameAndChannelType(templateName, channelType)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + templateName + " / " + channelType));

        Template updated = TemplateMapper.toUpdatedTemplate(existing, request, LocalDateTime.now());

        templateRepository.save(updated);
        return TemplateMapper.toResponse(updated);
    }

    public void deleteTemplate(String templateName, Channel channelType) {
        templateRepository.softDelete(templateName, channelType);
    }
}
