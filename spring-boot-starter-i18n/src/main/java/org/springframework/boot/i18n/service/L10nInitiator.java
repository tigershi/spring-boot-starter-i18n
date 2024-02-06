package org.springframework.boot.i18n.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.i18n.constant.CharConstant;
import org.springframework.boot.i18n.constant.L10nConstant;
import org.springframework.boot.i18n.constant.SpringI18nConfigConstant;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class L10nInitiator {
    private static Logger logger = LoggerFactory.getLogger(L10nInitiator.class);

    private static boolean flag = true;
    public static synchronized void initZipCountryFlagPattern() {
        if (flag) {

            logger.info("begin to init country flag content.");
            String sourcePath = "flag/country-flag-icons-**.zip";
            try {
                Resource[] resources = resources = new PathMatchingResourcePatternResolver()
                        .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + sourcePath);
                if (resources.length == 1) {
                    try (ZipInputStream zin = new ZipInputStream(resources[0].getInputStream(), Charset.forName("UTF-8"))) {
                        ZipEntry ze = null;
                        while ((ze = zin.getNextEntry()) != null) {
                            if (ze.getName().startsWith("country-flag-icons-")) {
                                String zipName = ze.getName().substring(ze.getName().indexOf(CharConstant.BACKSLASH));
                                if ((zipName.startsWith("/flags/3x2/") || zipName.startsWith("/flags/1x1/")) && zipName.endsWith(SpringI18nConfigConstant.FILE_TYPE_SVG)) {
                                    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                                        byte[] buffer = new byte[1024];
                                        int len;
                                        while ((len = zin.read(buffer)) > 0) {
                                            bos.write(buffer, 0, len);
                                        }
                                        String content = bos.toString();
                                        writeCountryFlagResult(zipName, content, SpringI18nConfigConstant.FILE_TYPE_SVG);
                                    }
                                }
                            }
                            zin.closeEntry();
                        }
                    }
                    logger.info("Init zip country flag content successfully!");

                } else {
                    logger.error("only allows one country flag source zip file");
                }
            } catch (IOException e) {
                logger.error("Init zip country flag content failure!", e);
            }
        }
        flag = false;
    }

    private static  void writeCountryFlagResult(String sourcePathStr, String fileContent, String newFileNameSuffix) throws IOException {

        String pathStr = L10nConstant.IMAGE + sourcePathStr;
        pathStr = pathStr.replaceAll(CharConstant.BACKSLASH, Matcher.quoteReplacement(File.separator));
        pathStr = pathStr.replaceAll(SpringI18nConfigConstant.FILE_TYPE_SVG, newFileNameSuffix);
        File file = new File(pathStr);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.deleteOnExit();
        file.createNewFile();
        logger.info(file.getAbsolutePath());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.write(ByteBuffer.wrap(fileContent.getBytes(StandardCharsets.UTF_8)));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }


    }



    private static void writeCountryFlagJsonResult(String sourcePathStr, String flagContent) throws IOException {
        int regionIdx = sourcePathStr.lastIndexOf(CharConstant.BACKSLASH)+1;
        String region = sourcePathStr.substring(regionIdx).replace(SpringI18nConfigConstant.FILE_TYPE_SVG, "");

        Map<String, String> respData = new HashMap<>();
        respData.put("type", "svg");
        respData.put("image", flagContent);
        respData.put("region", region);

        try {
            String content = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(respData);
            writeCountryFlagResult(sourcePathStr, content, SpringI18nConfigConstant.FILE_TYPE_JSON);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }
}
