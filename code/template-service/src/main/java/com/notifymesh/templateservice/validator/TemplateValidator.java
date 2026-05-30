package com.notifymesh.templateservice.validator;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.exception.TemplateValidationException;
import org.springframework.stereotype.Component;

@Component
public class TemplateValidator {

    public void validate(String templateName, Channel channelType, String subject) {
        if (channelType == Channel.EMAIL) {
            if (subject == null || subject.isBlank()) {
                throw new TemplateValidationException("subject is required for EMAIL channel");
            }
        } else {
            if (subject != null && !subject.isBlank()) {
                throw new TemplateValidationException("subject must not be provided for " + channelType + " channel");
            }
        }
    }
}
