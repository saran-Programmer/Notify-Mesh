package com.notifymesh.templateservice.validator;

import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.domain.valueobject.TemplateType;
import com.notifymesh.templateservice.exception.TemplateValidationException;
import org.springframework.stereotype.Component;

@Component
public class TemplateValidator {

    public void validate(String templateName, Channel channelType, String subject, TemplateType templateType) {
        if (channelType == Channel.EMAIL) {
            if (subject == null || subject.isBlank()) {
                throw new TemplateValidationException("subject is required for EMAIL channel");
            }
        } else {
            if (subject != null && !subject.isBlank()) {
                throw new TemplateValidationException("subject must not be provided for " + channelType + " channel");
            }
        }

        if (templateType == TemplateType.HTML && channelType != Channel.EMAIL) {
            throw new TemplateValidationException(
                    "HTML template type is not supported for " + channelType + " channel. Only TEXT is allowed.");
        }
    }
}
