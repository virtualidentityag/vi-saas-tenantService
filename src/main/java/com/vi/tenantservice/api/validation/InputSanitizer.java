package com.vi.tenantservice.api.validation;

import org.owasp.html.HtmlPolicyBuilder;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    public String sanitize(String input) {
      var sanitizer= new HtmlPolicyBuilder().toFactory();
      return sanitizer.sanitize(input);
    }

    public String sanitizeAllowingFormatting(String input) {
      var sanitizer = new HtmlPolicyBuilder().allowStyling().allowCommonInlineFormattingElements().allowCommonBlockElements().toFactory();
      return sanitizer.sanitize(input);
    }
}
