package com.vi.tenantservice.api.config;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@org.springframework.context.annotation.Configuration
public class FreeMarkerConfig {

  @Bean
  public freemarker.template.Configuration freemarkerConfiguration()
      throws TemplateException, IOException {
    Configuration configuration = new FreeMarkerConfigurationFactoryBean().createConfiguration();
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER); // Set to ignore missing variables

    StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
    configuration.setTemplateLoader(stringTemplateLoader);
    return configuration;
  }
}
