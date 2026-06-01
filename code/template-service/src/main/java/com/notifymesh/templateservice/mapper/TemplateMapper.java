package com.notifymesh.templateservice.mapper;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.domain.valueobject.TemplateType;
import com.notifymesh.templateservice.dto.CreateTemplateRequest;
import com.notifymesh.templateservice.dto.TemplateResponse;
import com.notifymesh.templateservice.dto.UpdateTemplateRequest;
import com.notifymesh.templateservice.infrastructure.entity.Template;
import com.notifymesh.templateservice.infrastructure.entity.TemplateAttachment;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static com.notifymesh.templateservice.constants.TemplateFields.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateMapper {

    private TemplateMapper() {
        // Private constructor to prevent instantiation
    }

    public static Map<String, AttributeValue> toItem(Template template) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put(TEMPLATE_NAME, AttributeValue.builder().s(template.getTemplateName()).build());
        item.put(VERSION, AttributeValue.builder().n(String.valueOf(template.getVersion())).build());
        item.put(CHANNEL_TYPE, AttributeValue.builder().s(template.getChannelType().name()).build());
        item.put(TEMPLATE_TYPE, AttributeValue.builder().s(template.getTemplateType().name()).build());
        item.put(BODY, AttributeValue.builder().s(template.getBody()).build());
        item.put(IS_ACTIVE, AttributeValue.builder().bool(template.getIsActive()).build());
        item.put(CREATED_DATE, AttributeValue.builder().s(template.getCreatedDate().toString()).build());
        item.put(LAST_MODIFIED_DATE, AttributeValue.builder().s(template.getLastModifiedDate().toString()).build());

        if (template.getSubject() != null) {
            item.put(SUBJECT, AttributeValue.builder().s(template.getSubject()).build());
        }

        if (template.getDefaultAttachment() != null && !template.getDefaultAttachment().isEmpty()) {
            List<AttributeValue> attachments = template.getDefaultAttachment().stream()
                    .map(a -> {
                        Map<String, AttributeValue> attachmentMap = new HashMap<>();
                        attachmentMap.put(ATTACHMENT_FILE_NAME, AttributeValue.builder().s(a.getFileName()).build());
                        attachmentMap.put(ATTACHMENT_S3_KEY, AttributeValue.builder().s(a.getS3Key()).build());
                        return AttributeValue.builder().m(attachmentMap).build();
                    })
                    .collect(Collectors.toList());
            item.put(DEFAULT_ATTACHMENT, AttributeValue.builder().l(attachments).build());
        }

        return item;
    }

    public static Template fromItem(Map<String, AttributeValue> item) {
        Template template = new Template();

        template.setTemplateName(item.get(TEMPLATE_NAME).s());
        template.setVersion(Integer.parseInt(item.get(VERSION).n()));
        template.setChannelType(Channel.valueOf(item.get(CHANNEL_TYPE).s()));
        template.setTemplateType(TemplateType.valueOf(item.get(TEMPLATE_TYPE).s()));
        template.setBody(item.get(BODY).s());
        template.setIsActive(item.get(IS_ACTIVE).bool());
        template.setCreatedDate(LocalDateTime.parse(item.get(CREATED_DATE).s()));
        template.setLastModifiedDate(LocalDateTime.parse(item.get(LAST_MODIFIED_DATE).s()));

        if (item.containsKey(SUBJECT)) {
            template.setSubject(item.get(SUBJECT).s());
        }

        if (item.containsKey(DEFAULT_ATTACHMENT)) {
            List<TemplateAttachment> attachments = item.get(DEFAULT_ATTACHMENT).l().stream()
                    .map(av -> {
                        Map<String, AttributeValue> m = av.m();
                        return TemplateAttachment.builder()
                                .fileName(m.get(ATTACHMENT_FILE_NAME).s())
                                .s3Key(m.get(ATTACHMENT_S3_KEY).s())
                                .build();
                    })
                    .collect(Collectors.toList());
            template.setDefaultAttachment(attachments);
        }

        return template;
    }

    public static TemplateResponse toResponse(Template template) {
        return TemplateResponse.builder()
                .templateName(template.getTemplateName())
                .channelType(template.getChannelType())
                .templateType(template.getTemplateType())
                .version(template.getVersion())
                .subject(template.getSubject())
                .body(template.getBody())
                .defaultAttachment(template.getDefaultAttachment() != null
                        ? new ArrayList<>(template.getDefaultAttachment())
                        : new ArrayList<>())
                .isActive(template.getIsActive())
                .createdDate(template.getCreatedDate())
                .lastModifiedDate(template.getLastModifiedDate())
                .build();
    }

    public static Template toTemplate(CreateTemplateRequest request) {
        return Template.builder()
                .templateName(request.getTemplateName())
                .channelType(request.getChannelType())
                .templateType(request.getTemplateType())
                .subject(request.getSubject())
                .body(request.getBody())
                .defaultAttachment(request.getDefaultAttachment() != null
                        ? new ArrayList<>(request.getDefaultAttachment())
                        : new ArrayList<>())
                .build();
    }

    public static Template toUpdatedTemplate(Template existing, UpdateTemplateRequest request, LocalDateTime lastModifiedDate) {
        return Template.builder()
                .templateName(existing.getTemplateName())
                .channelType(existing.getChannelType())
                .templateType(request.getTemplateType() != null ? request.getTemplateType() : existing.getTemplateType())
                .version(existing.getVersion() + 1)
                .subject(request.getSubject())
                .body(request.getBody())
                .isActive(existing.getIsActive())
                .defaultAttachment(request.getDefaultAttachment() != null
                        ? new ArrayList<>(request.getDefaultAttachment())
                        : new ArrayList<>())
                .createdDate(existing.getCreatedDate())
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
