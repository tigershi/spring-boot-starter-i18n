package org.springframework.boot.i18n.service;

import com.vmware.i18n.utils.timezone.TimeZoneName;
import org.springframework.boot.i18n.config.SpringI18nConfig;
import org.springframework.boot.i18n.model.FormattedDateResult;
import org.springframework.boot.i18n.model.FormattedNumberResult;
import org.springframework.boot.i18n.model.L10nException;
import org.springframework.boot.i18n.model.TerritoryResult;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

public class L10nLocaleFactory {
    private static boolean initFlag = false;

    private static SpringI18nConfig springI18nConfig = null;
    private static class L10nLocaleServiceInstance{
        private static final L10nLocaleService l10nLocaleService = new L10nLocaleServiceImpl();
    }
    public static L10nLocaleService getL10nLocaleService(){
        return L10nLocaleServiceInstance.l10nLocaleService;
    }

    public  static synchronized void initFactory(SpringI18nConfig springI18nConfig){
        if (!initFlag){
            springI18nConfig = springI18nConfig;
        }
        initFlag = true;
    }

    /**
     * Get the region list from CLDR by the language
     *
     * @param displayCity a flag for displaying all the cities, e.g. true, false

     */
    public static List<TerritoryResult> gitRegionList(boolean displayCity) throws  L10nException{
        if (initFlag){
            return getL10nLocaleService().gitRegionList(springI18nConfig.getLanguage(),displayCity, springI18nConfig.getDefaultRegion() );
        }
        return null;
    }

    /**
     *
     * Get the country flag svg image with region and scale
     *
     * @param scale The country flag scale：1 is 1x1, 2 is 3x2,default is 1x1
     * @param type a string for image response type, default is json (only support 'svg', 'json' now)
     */
    public static FileChannel getCountryFlag( int scale, String type) throws L10nException{
        if (initFlag){
            return getL10nLocaleService().getCountryFlag(springI18nConfig.getDefaultRegion(), scale, type);
        }

        return null;
    }

    /**
     * Get pattern from CLDR with language, region and scope
     * @param language a string which represents language, e.g. en, en-US, pt, pt-BR, zh-Hans
     * @param region a string which represents region, e.g. US, PT, CN
     * @param scope pattern category string, separated by commas. e.g. 'dates, numbers, currencies, plurals, measurements, dateFields'
     * @param scopeFilter a String for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dayPeriodsFormat'
     */
    public static Map<String, Object> getFormattingPatterns(String language, String region, String scope, String scopeFilter) throws L10nException{
        if (initFlag){
            return getL10nLocaleService().getFormattingPatterns(springI18nConfig.getLanguage(), springI18nConfig.getDefaultRegion(), scope, scopeFilter);
        }
        return null;
    }

    /**
     * Get pattern from CLDR by locale and scope
     *
     * @param scope pattern category string, separated by commas. e.g. 'dates, numbers, currencies, plurals, measurements, dateFields'
     * @param scopeFilter a String for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dayPeriodsFormat'
     */
    public  static Map<String, Object> getFormattingPatterns(String scope, String scopeFilter) throws L10nException{
        if (initFlag){
            return getL10nLocaleService().getFormattingPatterns(springI18nConfig.getLanguage()+"-"+ springI18nConfig.getDefaultRegion(), scope, scopeFilter);
        }
        return null;
    }

    /**
     *Get localized number by locale and scale
     *
     * @param number the number
     * @param scale decimal digits
     */
    public static FormattedNumberResult getLocalizedNumber( String number, Integer scale){
        if (initFlag){
            return getL10nLocaleService().getLocalizedNumber(springI18nConfig.getLanguage()+"-"+ springI18nConfig.getDefaultRegion(), number, scale);
        }
        return null;
    }

    /** Get timezone Name list by display language and defaultTerritory
     *
     * @param displayLanguage the display language. e.g. 'en'
     * @param defaultTerritory a boolean value to get the default territory timezone name or not, default is true
     */
    public static TimeZoneName getTimezoneNameList(String displayLanguage, boolean defaultTerritory) throws L10nException{
        if (initFlag){
            return getL10nLocaleService().getTimezoneNameList(springI18nConfig.getLanguage(), defaultTerritory);
        }
        return null;
    }

    /**
     * Get localized date by locale and pattern
     *
     * @param locale locale String
     * @param longDate long value of the date
     * @param pattern pattern used to format the long date
     * (the value could be one of this: YEAR = "y",QUARTER = "QQQQ",ABBR_QUARTER = "QQQ",QUARTER_YEAR = "QQQQy",QUARTER_ABBR_YEAR = "QQQy",MONTH = "MMMM",
     * ABBR_MONTH = "MMM",NUM_MONTH = "M",MONTH_YEAR = "MMMMy",MONTH_ABBR_YEAR = "MMMy",MONTH_NUM_YEAR = "My",DAY = "d",MONTH_DAY_YEAR = "MMMMdy",
     * ABBR_MONTH_DAY_YEAR = "MMMdy",NUM_MONTH_DAY_YEAR = "Mdy",WEEKDAY = "EEEE",ABBR_WEEKDAY = "E",WEEKDAY_MONTH_DAY_YEAR = "EEEEMMMMdy",
     * ABBR_WEEKDAY_MONTH_DAY_YEAR = "EMMMdy",NUM_WEEKDAY_MONTH_DAY_YEAR = "EMdy",MONTH_DAY = "MMMMd",ABBR_MONTH_DAY = "MMMd",NUM_MONTH_DAY = "Md",
     * WEEKDAY_MONTH_DAY = "EEEEMMMMd",ABBR_WEEKDAY_MONTH_DAY = "EMMMd",NUM_WEEKDAY_MONTH_DAY = "EMd"
     */
    public static FormattedDateResult getLocalizedDate(String locale, long longDate, String pattern) throws L10nException{
        if (initFlag){
            return getL10nLocaleService().getLocalizedDate(springI18nConfig.getLanguage()+"-"+ springI18nConfig.getDefaultRegion(), longDate, pattern);
        }
        return null;
    }




}
