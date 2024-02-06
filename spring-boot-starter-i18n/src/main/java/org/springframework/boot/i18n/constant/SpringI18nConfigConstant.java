package org.springframework.boot.i18n.constant;

public interface SpringI18nConfigConstant {
    public final static String SPRING_I18N_CONFIG_TRANSLATION_DIR = "spring.i18n.config.translation.dir";
    public final static String SPRING_I18N_CONFIG_TRANSLATION_DEFAULT_DIR = "classpath:i18n";

    public final static String SPRING_I18N_CONFIG_TRANSLATION_LOCALE = "spring.i18n.config.translation.locale";
    public final static String SPRING_I18N_CONFIG_TRANSLATION_DEFAULT_LOCALE = "en";

    public final static String SPRING_I18N_CONFIG_TRANSLATION_COLLECT_ENABLE="spring.i18n.config.translation.collection.enable";

    public final static String SPRING_I18N_CONFIG_TRANSLATION_COLLECT_DIR="spring.i18n.config.translation.collection.dir";
    public final static String SPRING_I18N_CONFIG_TRANSLATION_COLLECT_DEFAULT_DIR = "file:i18n";
    public final static String SPRING_I18N_CONFIG_TRANSLATION_COLLECT_FILE_NAME = "messages_latest.json";
    public final static String SPRING_I18N_CONFIG_TRANSLATION_EXCLUDE_PREFIX = "org.springframework.";


    //l10n
    public final static String SPRING_I18N_CONFIG_L10N_ENABLE = "spring.i18n.config.l10n.enable";
    public final static String SPRING_I18N_CONFIG_L10N_REGION = "spring.i18n.config.l10n.region";



    public static final String FILE_TYPE_PROPERTIES = ".properties";
    public static final String FILE_TYPE_YML = ".yml";
    public static final String FILE_TYPE_YAML = ".yaml";
    public static final String FILE_TYPE_JSON = ".json";
    public static final String FILE_TYPE_SVG = ".svg";
    public static final String FILE_TYPE_SINGLETON_PREFIX="messages_";

    public static final String FILE_PATH_PREFIX="file:";
    public static final String CLASS_PATH_PREFIX="classpath:";
    public static final String CLASS_PATH_STR="classpath";


    public static final String DEFAULT = "default";
    public static final String TRUE_STR = "true";


}
