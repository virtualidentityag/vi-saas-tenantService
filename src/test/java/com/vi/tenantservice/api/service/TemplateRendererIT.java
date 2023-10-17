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
class TemplateRendererIT {

  @Autowired private TemplateRenderer templateRenderer;

  @Test
  void processInMemoryTemplate_ShouldProcessTemplateAndReplacePlaceholders()
      throws TemplateException, IOException {
    // given
    String template = "<p>Hello, ${name}!</p>";
    String expectedOutput = "<p>Hello, James!</p>";
    Map<String, Object> dataModel = Map.of("name", "James");

    // when
    String processedTemplate = templateRenderer.renderTemplate(template, dataModel);

    // then
    assertThat(processedTemplate).isEqualTo(expectedOutput);
  }

  @Test
  void processInMemoryTemplate_ShouldProcessTemplateAndLeaveEmptyStringForNonExistingVariables()
      throws TemplateException, IOException {
    // given
    String template = "<p>Hello, ${name!} ${surname}!</p>";
    String expectedOutput = "<p>Hello, James !</p>";
    Map<String, Object> dataModel = Map.of("name", "James");

    // when
    String processedTemplate = templateRenderer.renderTemplate(template, dataModel);

    // then
    assertThat(processedTemplate).isEqualTo(expectedOutput);
  }

  @Test
  void processInMemoryTemplate_ShouldProcessTemplateAndProcessVariableThatContainsAnotherVariableInside()
      throws TemplateException, IOException {
    // given
    String template = "<p>Hello, ${name!} ${surname!}!</p>";
    String expectedOutput = "<p>Hello, ${another_variable} and ${one_more_variable} !</p>";
    Map<String, Object> dataModel = Map.of("name", "${another_variable} and ${one_more_variable}");

    // when
    String processedTemplate = templateRenderer.renderTemplate(template, dataModel);

    // then
    assertThat(processedTemplate).isEqualTo(expectedOutput);
  }

  @Test
  void processInMemoryTemplate_ShouldThrowIllegalArgumentException() {
    // given
    Map<String, Object> dataModel = Map.of("name", "James");

    // when, then
    assertThrows(
        IllegalArgumentException.class, () -> templateRenderer.renderTemplate(null, dataModel));
  }
}
