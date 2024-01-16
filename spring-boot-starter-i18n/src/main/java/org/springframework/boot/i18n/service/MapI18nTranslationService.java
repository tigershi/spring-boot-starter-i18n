package org.springframework.boot.i18n.service;

import java.util.Map;

public interface MapI18nTranslationService {
    /**
     * According to the locale String and translation base Dir, load the i18n translation
     * and map the translation to i18n java bean object
     * @param map the i18n java bean object map
     * @param translationBaseDir the i18n translation file's base dir
     * @param localeStr the language locale string
     */
    public void mapTranslationToI18nObj(Map<String, Object> map, String translationBaseDir, String localeStr);
}
