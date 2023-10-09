package com.vi.tenantservice.api.validation;

import com.vi.tenantservice.api.model.PlaceholderDTO;
import java.util.List;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

  public String sanitize(String input) {
    var sanitizer = new HtmlPolicyBuilder().toFactory();
    return sanitizer.sanitize(input);
  }

  public String sanitizeAllowingFormatting(String input) {
    var sanitizer =
        new HtmlPolicyBuilder()
            .allowStyling()
            .allowCommonInlineFormattingElements()
            .allowCommonBlockElements()
            .toFactory();
    return sanitizer.sanitize(input);
  }

  public String sanitizeAllowingFormattingAndLinks(String input) {
    var sanitizer = getSanitizer();
    return sanitizer.sanitize(input);
  }

  public List<PlaceholderDTO> sanitizeAllowingFormattingAndLinks(
      List<PlaceholderDTO> placeholderDTOs) {
    var sanitizer = getSanitizer();

    return placeholderDTOs.stream()
        .map(
            placeholder ->
                new PlaceholderDTO()
                    .key(sanitizer.sanitize(placeholder.getKey()))
                    .value(sanitizer.sanitize(placeholder.getValue())))
        .toList();
  }

  private static PolicyFactory getSanitizer() {
    return new HtmlPolicyBuilder()
        .allowStyling()
        .allowStandardUrlProtocols()
        .allowCommonInlineFormattingElements()
        .allowCommonBlockElements()
        .allowElements("a")
        .allowAttributes("href", "target")
        .onElements("a")
        .allowElements("img")
        .allowAttributes("src", "width", "height")
        .onElements("img")
        .toFactory();
  }
}
