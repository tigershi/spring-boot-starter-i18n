package org.springframework.boot.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.request.BreakpointRequest;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.i18n.annotation.I18nComponent;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;
import org.springframework.boot.i18n.service.CollectSourceService;
import org.springframework.boot.i18n.service.CollectSourceServiceImpl;
import org.springframework.boot.i18n.service.MapI18nTranslationService;
import org.springframework.boot.i18n.service.MapI18nTranslationServiceImpl;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class InitI18nComponentListener implements ApplicationListener<ContextRefreshedEvent> {

   private CollectSourceService collectSourceService = new CollectSourceServiceImpl();
   private MapI18nTranslationService mapI18nTranslationService = new MapI18nTranslationServiceImpl();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String baseDir = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_DIR,SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_DEFAULT_DIR);
        String localeStr = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_LOCALE,SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_DEFAULT_LOCALE);
        String collectEnable = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_ENABLE, "false");
        String collectDir = event.getApplicationContext().getEnvironment()
                .getProperty(SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_DIR, SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_DEFAULT_DIR);


        Map<String, Object> map =  event.getApplicationContext().getBeansWithAnnotation(I18nComponent.class);
        if (collectEnable.equals("true")){
            collectSourceService.collectSourceFromI18nObj(map, collectDir);
        }

        mapI18nTranslationService.mapTranslationToI18nObj(map, baseDir, localeStr);
    }


}
