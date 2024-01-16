package org.springframework.boot.i18n.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.i18n.annotation.I18nVal;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MapI18nTranslationServiceImpl implements MapI18nTranslationService {
    private static final Log logger = LogFactory.getLog(MapI18nTranslationServiceImpl.class);

    @Override
    public void mapTranslationToI18nObj(Map<String, Object> map, SpringI18nConfig springI18nConfig) {
        if (map == null || map.isEmpty()) {
            return;
        }
        Map<String, TranslationMsg> i18nMsg = getLocaleTranslations(springI18nConfig);
        logger.debug("get the component translation size " + i18nMsg.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String compName = entry.getKey();
            TranslationMsg translationMsg = i18nMsg.get(compName);
            if (translationMsg != null) {
                Object object = entry.getValue();
                Method[] methods = object.getClass().getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    logger.debug("method:" + method.getName());
                    if (method.getName().startsWith("set")) {
                        try {
                            String key = method.getName().replace("set", "");
                            char firstChar = Character.toLowerCase(key.charAt(0));
                            String newKey = firstChar + key.substring(1);
                            Object value = translationMsg.getMessages().get(newKey);
                            if (value == null) {
                                value = translationMsg.getMessages().get(key);
                            }
                            if (value != null) {
                                method.invoke(object, value);
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mapTranslationToI18nVal(Map<String, Object> map, SpringI18nConfig springI18nConfig) {

        if (map == null || map.isEmpty()) {
            return;
        }
        Map<String, TranslationMsg> i18nMsg = getLocaleTranslations(springI18nConfig);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getDeclaredAnnotation(I18nVal.class) != null) {
                    I18nVal i18nVal = field.getDeclaredAnnotation(I18nVal.class);
                    String component = i18nVal.component();
                    String key = i18nVal.key();
                    Object val = i18nMsg.get(component).getMessages().get(key);
                    field.setAccessible(true);
                    try {
                        field.set(obj, val);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }


    }

    private Map<String, TranslationMsg> getLocaleTranslations(SpringI18nConfig springI18nConfig) {

        if (springI18nConfig.getTranslationBaseDir().startsWith(SpringI18nConfigConstant.FILE_PATH_PREFIX)) {
            return new LoadFileTranslationServiceImpl().loadLocaleTranslations(springI18nConfig);
        } else {
            return new LoadClasspathTranslationServiceImpl().loadLocaleTranslations(springI18nConfig);
        }

    }
}
