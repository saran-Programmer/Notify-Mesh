package com.notifymesh.workerservice.domain.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplateRenderer {

    private final Configuration freemarkerConfig;

    public TemplateRenderer() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    public String render(String templateBody, Map<String, String> parameters) {

        try {

            Template template = new Template("inline", new StringReader(templateBody), freemarkerConfig);

            StringWriter writer = new StringWriter();
            template.process(parameters, writer);

            return writer.toString();

        } catch (IOException | TemplateException e) {
            throw new RuntimeException("failed to render template: " + e.getMessage(), e);
        }
    }
}
