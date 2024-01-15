package org.springframework.boot.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.request.BreakpointRequest;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.i18n.annotation.I18nComponent;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;
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

   private ObjectMapper mapper = new ObjectMapper();

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
            collectSourceTranslation(map, collectDir);
        }

        mapI18nTranslation(map, baseDir, localeStr);
    }


    private  void mapI18nTranslation(Map<String, Object> map, String baseDir, String localeStr){
        Map<String, TranslationMsg> i18nMsg = getLocaleTranslations(baseDir, localeStr);
        System.out.println("get the component translation size " + i18nMsg.size());
        for (Map.Entry<String, Object> entry : map.entrySet()){
            String compName = entry.getKey();
            TranslationMsg translationMsg = i18nMsg.get(compName);

            if (translationMsg != null){
                Object object = entry.getValue();
                Method[] methods = object.getClass().getDeclaredMethods();
                for (int i =0; i<methods.length; i++){
                    Method method = methods[i];
                    System.out.println("method:"+ method.getName());
                    if (method.getName().startsWith("set")){
                        try {
                            String key = method.getName().replace("set", "");
                            char firstChar = Character.toLowerCase(key.charAt(0));
                            String newKey = firstChar + key.substring(1);
                            Object value = translationMsg.getMessages().get(newKey);
                            if(value == null){
                                value = translationMsg.getMessages().get(key);
                            }
                            if (value != null){
                                System.out.println(value);
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

    private void collectSourceTranslation(Map<String, Object> map, String baseDir) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String compName = entry.getKey();
            Map<String,Object> msgs = result.get(compName);
            if (msgs == null){
                result.put(compName, new HashMap<>());
                msgs = result.get(compName);
            }
            Object object = entry.getValue();
            Method[] methods = object.getClass().getDeclaredMethods();

            for (int i =0; i<methods.length; i++){
                Method method = methods[i];
                System.out.println("method:"+ method.getName());
                if (method.getName().startsWith("get")){
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

        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()){
            String comp = entry.getKey();
            Map<String, Object> msgs = entry.getValue();
            TranslationMsg translationMsg = new TranslationMsg();
            translationMsg.setLocale("latest");
            translationMsg.setComponent(comp);
            translationMsg.setMessages(msgs);
            File file = createSourceFile(comp, baseDir);
            try(FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)){
                fileWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(translationMsg));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("write source i18n file to disk: "+ file.getAbsolutePath());
        }


    }

    private File createSourceFile(String component, String baseCollectDir){
       String basePath =  baseCollectDir.replace(SpringI18nConfigConstant.FILE_PATH_PREFIX, "");
       if (basePath.endsWith(File.separator)){
           basePath = basePath+component+File.separator;
       }else {
           basePath = basePath+File.separator+ component+File.separator;
       }
       String fileName = basePath+SpringI18nConfigConstant.SPRING_I18N_CONFIG_TRANSLATION_COLLECT_FILE_NAME;
       File file = new File(fileName);
       if (!file.getParentFile().exists()){
           file.getParentFile().mkdirs();
       }
       if (file.exists()){
           file.delete();
       }
        return file;
    }
    private Map<String, TranslationMsg> getLocaleTranslations(String basePathStr, String localeStr){

        if (basePathStr.startsWith(SpringI18nConfigConstant.FILE_PATH_PREFIX)){
            return getFilePathTranslations(new File(basePathStr), localeStr);
        }else {
            return getClassPathTranslations(basePathStr, localeStr);
        }

    }

    private Map<String, TranslationMsg> getClassPathTranslations(String baseDirFile, String localeStr){
        Map<String, TranslationMsg> result = new HashMap<>();
        String baseDirPre = null;
        if (baseDirFile.endsWith("/")){
           baseDirPre =  baseDirFile+"**";
        }else {
            baseDirPre = baseDirFile+"/**";
        }
        try {
            Resource[] resources = resources = new PathMatchingResourcePatternResolver()
                    .getResources(baseDirPre);
            for (Resource res : resources) {
                String fileName = res.getFilename();

                if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_JSON) && fileName.contains(localeStr)) {
                    String compName = getComponentName(res, baseDirFile);
                    TranslationMsg tr = mapper.readValue(res.getContentAsByteArray(), TranslationMsg.class);
                    System.out.println("component Name: "+ compName);
                    System.out.println("key size"+ tr.getMessages().size());

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

        }catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
        }

        return result;
    }

    private String getComponentName(Resource resource, String baseDir) throws IOException {

        String transClassPath = resource.getURL().getPath();
        if (resource.isFile()){
            String path1 = transClassPath.substring(0, transClassPath.lastIndexOf(File.separator));
            String comp = path1.substring(path1.lastIndexOf(File.separator)+1);
            String baseDirStr = formatPathDir(removeClassPathPrefix(baseDir));
            if (baseDirStr.endsWith(comp)){
                return  SpringI18nConfigConstant.DEFAULT;
            }else {
                return comp;
            }
        }
        System.out.println("baseDirFile:"+ baseDir);
        int idx = transClassPath.lastIndexOf("!/");
        String path = transClassPath.substring(idx);
        System.out.println("subpath:"+ path);

        int lastIdx = path.lastIndexOf("/");
        if(path.substring(0, lastIdx).equals(formatPathDir(baseDir))){
            return SpringI18nConfigConstant.DEFAULT;
        }
        return path.replace(formatPathDir(baseDir), "").substring(1, lastIdx);

    }
    private String removeClassPathPrefix(String path){
        String result = path.replace(SpringI18nConfigConstant.CLASS_PATH_STR, "")
                .replaceFirst("\\*", "").replaceFirst(":", "").replace("/", "");
        return result;
    }
    private String formatPathDir(String path){
        if (path.endsWith("/") || path.endsWith(File.separator)){
            return path.substring(0, path.length()-1);
        }
        return path;
    }
    private Map<String, TranslationMsg> getFilePathTranslations(File baseDirFile, String localeStr){
        Map<String, File> localeMapFile = getFilePathTranslationsFiles(baseDirFile, localeStr);
        Map<String, TranslationMsg> result = new HashMap<>();

        for (Map.Entry<String, File> entry : localeMapFile.entrySet()){

            String fileName = entry.getValue().getName();
            try {
            if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_JSON)){
                TranslationMsg tr = mapper.readValue(entry.getValue(), TranslationMsg.class);
                result.put(entry.getKey(), tr);

            }else if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_PROPERTIES)){
                Properties properties = new Properties();
                properties.load(new FileInputStream(entry.getValue()));
                TranslationMsg tr = new TranslationMsg();
                tr.setLocale(localeStr);
                tr.setComponent(entry.getKey());
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<Object, Object> entMsg: properties.entrySet()){
                    map.put((String) entMsg.getKey(), entMsg.getValue());
                }
                tr.setMessages(map);

                result.put(entry.getKey(), tr);

            } else if (fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_YAML)
                    || fileName.endsWith(SpringI18nConfigConstant.FILE_TYPE_YML)){
                YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                yaml.setResources(new Resource[]{new FileSystemResource(entry.getValue())});
                Properties yamProp = yaml.getObject();
                TranslationMsg trYml = new TranslationMsg();
                trYml.setLocale(localeStr);
                trYml.setComponent(entry.getKey());
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<Object, Object> entYmlMsg: yamProp.entrySet()){
                    map.put((String) entYmlMsg.getKey(), entYmlMsg.getValue());
                }
                trYml.setMessages(map);

                result.put(entry.getKey(), trYml);

            }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }
    private Map<String, File> getFilePathTranslationsFiles(File baseDirFile, String localeStr){
        Map<String, File> compDirMap = getComponentFileMap(baseDirFile);
        Map<String, File> compLocaleFileMap = new HashMap<>();
        for(Map.Entry<String,File> entry : compDirMap.entrySet()){
            File result = findLocalTranslationFile(entry.getValue(), localeStr);
            if (result != null){
                compLocaleFileMap.put(entry.getKey(), result);
            }
        }
        return compLocaleFileMap;
    }

    private static Map<String, File> getComponentFileMap(File baseDirFile) {
        File[] componentArr = baseDirFile.listFiles(pathname -> {
            if(pathname.isDirectory()){
                return true;
            }
            return false;
        });
        Map<String, File> compDirMap = new HashMap<>();
        if (componentArr == null || componentArr.length <1){
            compDirMap.put("default", baseDirFile);
        }else {
            for (File file : Arrays.asList(componentArr)){
                compDirMap.put(file.getName(), file);
            }
        }
        return compDirMap;
    }


    private File findLocalTranslationFile(File compDir, String localStr){
      if(compDir.exists()){
          final String includeStr = localStr + ".";
          File[] localeFiles = compDir.listFiles(new FilenameFilter() {
              @Override
              public boolean accept(File dir, String name) {
                 if (name.contains(includeStr)){
                     return true;
                 }
                 return false;
              }
          });

          if (localeFiles.length > 0){
              return localeFiles[0];
          }
          
      }
      return null;
    }

}
