package org.springframework.boot.i18n.service;

import org.springframework.boot.i18n.config.SpringI18nConfig;

import java.util.Map;

public interface CollectSourceService {

    /**
     * collect I18n key value pair from object that has Java annotation org.springframework.boot.i18n.annotation.I18nComponent
     * @param map java bean map that the key is component name the value is java bean object
     * @param
     */
 public void collectSourceFromI18nObj( Map<String, Object> map);

 public void collectSourceFromI18nVal( Map<String, Object> map);

 public void writeCollectedSourceToFile(SpringI18nConfig config);

}
