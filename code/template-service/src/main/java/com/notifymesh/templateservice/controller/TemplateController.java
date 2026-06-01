package com.notifymesh.templateservice.controller;

import com.notifymesh.templateservice.domain.service.TemplateService;
import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.dto.CreateTemplateRequest;
import com.notifymesh.templateservice.dto.TemplateResponse;
import com.notifymesh.templateservice.dto.UpdateTemplateRequest;
import com.notifymesh.templateservice.validator.TemplateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    private final TemplateValidator templateValidator;

    @GetMapping("/{templateName}")
    public ResponseEntity<TemplateResponse> getTemplate(
            @PathVariable String templateName,
            @RequestParam Channel channelType,
            @RequestParam(required = false) Integer version,
            @RequestParam(defaultValue = "false") boolean isDeletedIncluded) {
        return ResponseEntity.ok(templateService.getTemplate(templateName, channelType, version, isDeletedIncluded));
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {

        templateValidator.validate(request.getTemplateName(), request.getChannelType(), 
        request.getSubject(), request.getTemplateType());

        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    @PutMapping("/{templateName}")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable String templateName,
            @RequestParam Channel channelType,
            @Valid @RequestBody UpdateTemplateRequest request) {

        templateValidator.validate(templateName, channelType, request.getSubject(), 
        request.getTemplateType());
        
        return ResponseEntity.ok(templateService.updateTemplate(templateName, channelType, request));
    }

    @DeleteMapping("/{templateName}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable String templateName,
            @RequestParam Channel channelType) {
        templateService.deleteTemplate(templateName, channelType);
        return ResponseEntity.noContent().build();
    }
}
