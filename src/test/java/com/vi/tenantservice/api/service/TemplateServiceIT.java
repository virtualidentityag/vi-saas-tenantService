package com.vi.tenantservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=testing")
class TemplateServiceIT {

  @Autowired private TemplateService templateService;

  @Test
  void processInMemoryTemplate_ShouldProcessTemplateAndReplacePlaceholders()
      throws TemplateException, IOException {
    // given
    String template = "<p>Hello, ${name}!</p>";
    String expectedOutput = "<p>Hello, James!</p>";
    Map<String, Object> dataModel = Map.of("name", "James");

    // when
    String processedTemplate = templateService.processInMemoryTemplate(template, dataModel);

    // then
    assertThat(processedTemplate).isEqualTo(expectedOutput);
  }

  @Test
  void processInMemoryTemplate_ShouldProcessTemplateAndLeaveEmptyString()
      throws TemplateException, IOException {
    // given
    String template = "<p>Hello, ${name} ${surname}!</p>";
    String expectedOutput = "<p>Hello, James!</p>";
    Map<String, Object> dataModel = Map.of("name", "James");

    // when
    String processedTemplate = templateService.processInMemoryTemplate(template, dataModel);

    // then
    assertThat(processedTemplate).isEqualTo(expectedOutput);
  }

  @Test
  void processInMemoryTemplate_ShouldThrowIllegalArgumentException() {
    // given
    Map<String, Object> dataModel = Map.of("name", "James");

    // when, then
    assertThrows(
        IllegalArgumentException.class,
        () -> templateService.processInMemoryTemplate(null, dataModel));
  }
}
