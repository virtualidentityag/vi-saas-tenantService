package com.vi.tenantservice.api.converter;

import com.google.common.collect.Maps;
import com.vi.tenantservice.api.model.PlaceholderDTO;
import com.vi.tenantservice.api.service.TemplateRenderer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ContentRenderer {

  private final @NonNull TemplateRenderer templateRenderer;

  public String renderContent(
      String singleLangContent, List<PlaceholderDTO> singleLangPlaceholders) {
    if (singleLangPlaceholders == null) {
      log.warn("No placeholders provided for content, skipping rendering");
      return singleLangContent;
    }
    Map<String, Object> dataModel =
        singleLangPlaceholders.stream()
            .collect(Collectors.toMap(PlaceholderDTO::getKey, PlaceholderDTO::getValue));

    try {
      return templateRenderer.renderTemplate(singleLangContent, dataModel);
    } catch (Exception e) {
      log.error("Cannot render template: ", e);
      throw new IllegalStateException(e);
    }
  }

  public Map<String, String> renderContentForAllLanguages(
      Map<String, String> multilingualContentToRender,
      Map<String, List<PlaceholderDTO>> placeholders) {
    if (multilingualContentToRender == null) {
      return Maps.newHashMap();
    }
    if (placeholders == null) {
      return multilingualContentToRender;
    }
    return multilingualContentToRender.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, entry -> tryRenderValue(entry, placeholders)));
  }

  private String tryRenderValue(
      Entry<String, String> contentForGivenLanguage,
      Map<String, List<PlaceholderDTO>> placeholders) {
    var lang = contentForGivenLanguage.getKey();
    if (placeholders.containsKey(lang)) {
      return renderContent(contentForGivenLanguage.getValue(), placeholders.get(lang));
    } else {
      return contentForGivenLanguage.getValue();
    }
  }
}
