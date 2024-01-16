package org.springframework.boot.i18n.service;

import org.springframework.boot.i18n.config.SpringI18nConfig;

import java.util.Map;

public interface MapI18nTranslationService {
    /**
     * According to the locale String and translation base Dir, load the i18n translation
     * and map the translation to i18n java bean object
     * @param map the i18n java bean object map
     */
    public void mapTranslationToI18nObj(Map<String, Object> map, SpringI18nConfig springI18nConfig);

    public void mapTranslationToI18nVal(Map<String, Object> map, SpringI18nConfig springI18nConfig);




}
