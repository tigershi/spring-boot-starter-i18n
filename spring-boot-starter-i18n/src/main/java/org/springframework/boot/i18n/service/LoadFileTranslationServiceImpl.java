package org.springframework.boot.i18n.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoadFileTranslationServiceImpl extends LoadBaseTranslationServiceImpl {
    private static final Log logger = LogFactory.getLog(LoadFileTranslationServiceImpl.class);
    @Override
    public Map<String, TranslationMsg> loadLocaleTranslations(SpringI18nConfig config) {
        return getFilePathTranslations(new File(config.getTranslationBaseDir()), config.getLanguage());
    }

    private Map<String, TranslationMsg> getFilePathTranslations(File baseDirFile, String localeStr) {
        Map<String, File> localeMapFile = getFilePathTranslationsFiles(baseDirFile, localeStr);
        Map<String, TranslationMsg> result = new HashMap<>();

        for (Map.Entry<String, File> entry : localeMapFile.entrySet()) {

            String fileName = entry.getValue().getName();
            try {
                if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_JSON)) {
                    TranslationMsg tr = mapper.readValue(entry.getValue(), TranslationMsg.class);
                    result.put(entry.getKey(), tr);

                } else if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_PROPERTIES)) {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(entry.getValue()));
                    TranslationMsg tr = new TranslationMsg();
                    tr.setLocale(localeStr);
                    tr.setComponent(entry.getKey());
                    Map<String, Object> map = new HashMap<>();
                    for (Map.Entry<Object, Object> entMsg : properties.entrySet()) {
                        map.put((String) entMsg.getKey(), entMsg.getValue());
                    }
                    tr.setMessages(map);

                    result.put(entry.getKey(), tr);

                } else if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_YAML)
                        || fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_YML)) {
                    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                    yaml.setResources(new Resource[]{new FileSystemResource(entry.getValue())});
                    Properties yamProp = yaml.getObject();
                    TranslationMsg trYml = new TranslationMsg();
                    trYml.setLocale(localeStr);
                    trYml.setComponent(entry.getKey());
                    Map<String, Object> map = new HashMap<>();
                    for (Map.Entry<Object, Object> entYmlMsg : yamProp.entrySet()) {
                        map.put((String) entYmlMsg.getKey(), entYmlMsg.getValue());
                    }
                    trYml.setMessages(map);

                    result.put(entry.getKey(), trYml);

                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return result;
    }
}
