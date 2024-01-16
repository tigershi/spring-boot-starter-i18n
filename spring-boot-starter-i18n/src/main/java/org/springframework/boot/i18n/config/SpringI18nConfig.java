package org.springframework.boot.i18n.config;

public class SpringI18nConfig {
    private boolean collectEnable = false;

    //publish the collected source's base directory, the direct should be a file directory
    private String collectFileDir = "file:i18n/";

    // translationBaseDir the i18n translation file's base dir
    private String translationBaseDir = "classpath:i18n/";

    //  the language locale string
    private String language = "en";

    public boolean isCollectEnable() {
        return collectEnable;
    }

    public void setCollectEnable(boolean collectEnable) {
        this.collectEnable = collectEnable;
    }

    public String getCollectFileDir() {
        return collectFileDir;
    }

    public void setCollectFileDir(String collectFileDir) {
        this.collectFileDir = collectFileDir;
    }

    public String getTranslationBaseDir() {
        return translationBaseDir;
    }

    public void setTranslationBaseDir(String translationBaseDir) {
        this.translationBaseDir = translationBaseDir;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
