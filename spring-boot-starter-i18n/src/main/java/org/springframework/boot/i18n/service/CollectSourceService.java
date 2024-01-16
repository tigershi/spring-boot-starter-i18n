package org.springframework.boot.i18n.service;

import java.util.Map;

public interface CollectSourceService {

    /**
     * collect I18n key value pair from object that has Java annotation org.springframework.boot.i18n.annotation.I18nComponent
     * @param map java bean map that the key is component name the value is java bean object
     * @param collectDir publish the collected source's base directory, the direct should be a file directory
     */
 public void collectSourceFromI18nObj( Map<String, Object> map, String collectDir);

}
