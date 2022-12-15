package com.vi.tenantservice.api.converter;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.vi.tenantservice.api.util.JsonConverter.convertMapFromJson;

@Slf4j
public class TranslationResolver {

    public static final String DE = "de";

    static String getTranslatedStringFromMap(String jsonValue, String lang) {
        Map<String, String> translations = convertMapFromJson(jsonValue);
        if (lang == null || !translations.containsKey(lang)) {
            if (translations.containsKey(DE)) {
                return translations.get(DE);
            } else {
                log.warn("Default translation for value not available");
                return "";
            }
        } else {
            return translations.get(lang);
        }
    }
}
