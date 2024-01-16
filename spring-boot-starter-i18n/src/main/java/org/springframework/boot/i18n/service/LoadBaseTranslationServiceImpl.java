package org.springframework.boot.i18n.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.boot.i18n.model.TranslationMsg;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class LoadBaseTranslationServiceImpl implements LoadTranslationService{
    private static final Log logger = LogFactory.getLog(LoadBaseTranslationServiceImpl.class);

    protected ObjectMapper mapper = new ObjectMapper();

    @Override
    public abstract Map<String, TranslationMsg> loadLocaleTranslations(SpringI18nConfig config);

    protected String getComponentName(Resource resource, String baseDir) throws IOException {

        String transClassPath = resource.getURL().getPath();
        if (resource.isFile()) {
            String path1 = transClassPath.substring(0, transClassPath.lastIndexOf(File.separator));
            String comp = path1.substring(path1.lastIndexOf(File.separator) + 1);
            String baseDirStr = formatPathDir(removeClassPathPrefix(baseDir));
            if (baseDirStr.endsWith(comp)) {
                return SpringI18nConfigConstant.DEFAULT;
            } else {
                return comp;
            }
        }
        logger.debug("baseDirFile:" + baseDir);
        int idx = transClassPath.lastIndexOf("!/");
        String path = transClassPath.substring(idx);
        logger.debug("subpath:" + path);

        int lastIdx = path.lastIndexOf("/");
        if (path.substring(0, lastIdx).equals(formatPathDir(baseDir))) {
            return SpringI18nConfigConstant.DEFAULT;
        }
        return path.replace(formatPathDir(baseDir), "").substring(1, lastIdx);

    }

    private String removeClassPathPrefix(String path) {
        String result = path.replace(SpringI18nConfigConstant.CLASS_PATH_STR, "")
                .replaceFirst("\\*", "").replaceFirst(":", "").replace("/", "");
        return result;
    }

    private String formatPathDir(String path) {
        if (path.endsWith("/") || path.endsWith(File.separator)) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }


    protected Map<String, File> getFilePathTranslationsFiles(File baseDirFile, String localeStr) {
        Map<String, File> compDirMap = getComponentFileMap(baseDirFile);
        Map<String, File> compLocaleFileMap = new HashMap<>();
        for (Map.Entry<String, File> entry : compDirMap.entrySet()) {
            File result = findLocalTranslationFile(entry.getValue(), localeStr);
            if (result != null) {
                compLocaleFileMap.put(entry.getKey(), result);
            }
        }
        return compLocaleFileMap;
    }

    private File findLocalTranslationFile(File compDir, String localStr) {
        if (compDir.exists()) {
            final String includeStr = localStr + ".";
            File[] localeFiles = compDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contains(includeStr)) {
                        return true;
                    }
                    return false;
                }
            });

            if (localeFiles.length > 0) {
                return localeFiles[0];
            }

        }
        return null;
    }

    private Map<String, File> getComponentFileMap(File baseDirFile) {
        File[] componentArr = baseDirFile.listFiles(pathname -> {
            if (pathname.isDirectory()) {
                return true;
            }
            return false;
        });
        Map<String, File> compDirMap = new HashMap<>();
        if (componentArr == null || componentArr.length < 1) {
            compDirMap.put("default", baseDirFile);
        } else {
            for (File file : Arrays.asList(componentArr)) {
                compDirMap.put(file.getName(), file);
            }
        }
        return compDirMap;
    }

}
