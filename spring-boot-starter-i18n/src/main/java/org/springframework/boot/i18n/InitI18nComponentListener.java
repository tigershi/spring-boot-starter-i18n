package org.springframework.boot.i18n;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.i18n.annotation.I18nComponent;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.service.CollectSourceService;
import org.springframework.boot.i18n.service.CollectSourceServiceImpl;
import org.springframework.boot.i18n.service.MapI18nTranslationService;
import org.springframework.boot.i18n.service.MapI18nTranslationServiceImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class InitI18nComponentListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Log logger = LogFactory.getLog(InitI18nComponentListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String baseDir = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_DIR, SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_DEFAULT_DIR);
        String localeStr = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_LOCALE, SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_DEFAULT_LOCALE);
        String collectEnable = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_ENABLE, "false");
        String collectDir = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_DIR, SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_DEFAULT_DIR);

        SpringI18nConfig springI18nConfig = new SpringI18nConfig();
        springI18nConfig.setTranslationBaseDir(baseDir);
        springI18nConfig.setLanguage(localeStr);
        springI18nConfig.setCollectEnable(Boolean.valueOf(collectEnable));
        springI18nConfig.setCollectFileDir(collectDir);


        Map<String, Object> I18nComponentMap = event.getApplicationContext().getBeansWithAnnotation(I18nComponent.class);

        Map<String, Object> mapComps = event.getApplicationContext().getBeansWithAnnotation(Component.class);
        Map<String, Object> i18ValMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : mapComps.entrySet()) {
            String key = entry.getKey();
            if (!I18nComponentMap.containsKey(key) && !key.startsWith(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_EXCLUDE_PREFIX)) {
                i18ValMap.put(key, entry.getValue());
            }
        }
        logger.debug("the contain i18n value java bean object size: " + i18ValMap.size());

        if (springI18nConfig.isCollectEnable()) {
            CollectSourceService collectSourceService = new CollectSourceServiceImpl();
            logger.info("begin collect i18n key value pair from i18nComponent object");
            collectSourceService.collectSourceFromI18nObj(I18nComponentMap);
            logger.info("begin collect i18n key value pair from i18n value java bean");
            collectSourceService.collectSourceFromI18nVal(i18ValMap);
            collectSourceService.writeCollectedSourceToFile(springI18nConfig);
            logger.info("collect i18n key value pair end");
        }


        MapI18nTranslationService mapI18nTranslationService = new MapI18nTranslationServiceImpl();
        logger.info("map locale translation from Dir: " + baseDir + " , locale: " + localeStr + " to i18n component object");
        mapI18nTranslationService.mapTranslationToI18nObj(I18nComponentMap, springI18nConfig);

        logger.info("map locale translation from Dir: " + baseDir + " , locale: " + localeStr + " to i18n value java bean");
        mapI18nTranslationService.mapTranslationToI18nVal(i18ValMap, springI18nConfig);
        logger.info("map locale translation end");

    }


}
