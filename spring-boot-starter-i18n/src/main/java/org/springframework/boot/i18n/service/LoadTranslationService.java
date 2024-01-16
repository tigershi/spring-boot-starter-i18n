package org.springframework.boot.i18n.service;

import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.model.TranslationMsg;

import java.util.Map;

public interface LoadTranslationService {
    public Map<String, TranslationMsg> loadLocaleTranslations(SpringI18nConfig config);
}
