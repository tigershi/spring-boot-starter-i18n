package org.springframework.boot.i18n.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.i18n.annotation.I18nVal;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CollectSourceServiceImpl implements CollectSourceService {

    private static final Log logger = LogFactory.getLog(CollectSourceServiceImpl.class);

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Map<String, Object>> result = new HashMap<>();

    @Override
    public void collectSourceFromI18nObj(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String compName = entry.getKey();
            Map<String, Object> msgs = result.get(compName);
            if (msgs == null) {
                result.put(compName, new HashMap<>());
                msgs = result.get(compName);
            }
            Object object = entry.getValue();
            Method[] methods = object.getClass().getDeclaredMethods();

            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                // System.out.println("method:" + method.getName());
                if (method.getName().startsWith("get")) {
                    String key = method.getName().replace("get", "");
                    char firstChar = Character.toLowerCase(key.charAt(0));
                    String newKey = firstChar + key.substring(1);
                    try {
                        msgs.put(newKey, method.invoke(object));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }


    }

    @Override
    public void collectSourceFromI18nVal(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getDeclaredAnnotation(I18nVal.class) != null) {
                    I18nVal i18nVal = field.getDeclaredAnnotation(I18nVal.class);
                    String component = i18nVal.component();
                    String key = i18nVal.key();
                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        Map<String, Object> msgs = result.get(component);
                        if (msgs == null) {
                            result.put(component, new HashMap<>());
                            msgs = result.get(component);
                        }
                        msgs.put(key, value);

                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }


    }

    @Override
    public void writeCollectedSourceToFile(SpringI18nConfig config) {
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            String comp = entry.getKey();
            Map<String, Object> msgsMap = entry.getValue();
            TranslationMsg translationMsg = new TranslationMsg();
            translationMsg.setLocale("latest");
            translationMsg.setComponent(comp);
            translationMsg.setMessages(msgsMap);
            File file = createSourceFile(comp, config.getCollectFileDir());
            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationMsg));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            logger.info("write source i18n file to disk: " + file.getAbsolutePath());
        }

    }

    private File createSourceFile(String component, String baseCollectDir) {
        String basePath = baseCollectDir.replace(SpringI18nConfigConstant.FILE_PATH_PREFIX, "");
        if (basePath.endsWith(File.separator)) {
            basePath = basePath + component + File.separator;
        } else {
            basePath = basePath + File.separator + component + File.separator;
        }
        String fileName = basePath + SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_FILE_NAME;
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        return file;
    }


}
