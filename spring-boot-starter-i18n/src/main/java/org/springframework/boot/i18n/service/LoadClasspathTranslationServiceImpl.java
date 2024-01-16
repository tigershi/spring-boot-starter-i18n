package org.springframework.boot.i18n.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoadClasspathTranslationServiceImpl extends LoadBaseTranslationServiceImpl {
    private static final Log logger = LogFactory.getLog(LoadClasspathTranslationServiceImpl.class);

    @Override
    public Map<String, TranslationMsg> loadLocaleTranslations(SpringI18nConfig config) {
        return getClassPathTranslations(config.getTranslationBaseDir(), config.getLanguage());
    }

    private Map<String, TranslationMsg> getClassPathTranslations(String baseDirFile, String localeStr) {
        Map<String, TranslationMsg> result = new HashMap<>();
        String baseDirPre = null;
        if (baseDirFile.endsWith("/")) {
            baseDirPre = baseDirFile + "**";
        } else {
            baseDirPre = baseDirFile + "/**";
        }
        try {
            Resource[] resources = resources = new PathMatchingResourcePatternResolver()
                    .getResources(baseDirPre);
            for (Resource res : resources) {
                String fileName = res.getFilename();

                if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_JSON) && fileName.contains(localeStr)) {
                    String compName = getComponentName(res, baseDirFile);
                    TranslationMsg tr = mapper.readValue(res.getContentAsByteArray(), TranslationMsg.class);
                    logger.debug("component Name: " + compName);
                    logger.debug("key size" + tr.getMessages().size());

                    result.put(compName, tr);

                } else if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_PROPERTIES) && fileName.contains(localeStr)) {
                    String compName = getComponentName(res, baseDirFile);
                    Properties properties = new Properties();
                    properties.load(res.getInputStream());
                    TranslationMsg tr = new TranslationMsg();
                    tr.setLocale(localeStr);
                    tr.setComponent(compName);
                    Map<String, Object> map = new HashMap<>();
                    for (Map.Entry<Object, Object> entMsg : properties.entrySet()) {
                        map.put((String) entMsg.getKey(), entMsg.getValue());
                    }
                    tr.setMessages(map);

                    result.put(compName, tr);

                } else if ((fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_YAML)
                        || fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_YML)) && fileName.contains(localeStr)) {
                    String compName = getComponentName(res, baseDirFile);
                    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                    yaml.setResources(new Resource[]{res});
                    Properties yamProp = yaml.getObject();
                    TranslationMsg trYml = new TranslationMsg();
                    trYml.setLocale(localeStr);
                    trYml.setComponent(compName);
                    Map<String, Object> map = new HashMap<>();
                    for (Map.Entry<Object, Object> entYmlMsg : yamProp.entrySet()) {
                        map.put((String) entYmlMsg.getKey(), entYmlMsg.getValue());
                    }
                    trYml.setMessages(map);

                    result.put(compName, trYml);

                }

            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

}
