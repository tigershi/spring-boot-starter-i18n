package org.springframework.boot.i18n.service;

import com.vmware.i18n.utils.timezone.TimeZoneName;
import org.springframework.boot.i18n.model.*;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

public interface L10nLocaleService {
    /**
     * Get the region list from CLDR by the language
     *
     * @param supportedLanguageList the supported language list, separated by commas. e.g. 'en, zh, ja'
     * @param displayCity a flag for displaying all the cities, e.g. true, false
     * @param regions a string which represents regions, separated by commas. e.g. US, PT, CN
     */
    public List<TerritoryResult> gitRegionList(String supportedLanguageList, boolean displayCity, String regions) throws L10nException, L10nException;

    /**
     *
     * Get the country flag svg image with region and scale
     *
     * @param region a string which represents region, e.g. US, PT, CN
     * @param scale The country flag scale：1 is 1x1, 2 is 3x2,default is 1x1
     * @param type a string for image response type, default is json (only support 'svg', 'json' now)
     */
    public FileChannel getCountryFlag(String region, int scale, String type) throws L10nException;

    /**
     * Get pattern from CLDR with language, region and scope
     * @param language a string which represents language, e.g. en, en-US, pt, pt-BR, zh-Hans
     * @param region a string which represents region, e.g. US, PT, CN
     * @param scope pattern category string, separated by commas. e.g. 'dates, numbers, currencies, plurals, measurements, dateFields'
     * @param scopeFilter a String for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dayPeriodsFormat'
     */
    public Map<String, Object> getFormattingPatterns(String language, String region, String scope, String scopeFilter) throws L10nException;

    /**
     * Get pattern from CLDR by locale and scope
     *
     * @param locale locale String. e.g. 'en-US'
     * @param scope pattern category string, separated by commas. e.g. 'dates, numbers, currencies, plurals, measurements, dateFields'
     * @param scopeFilter a String for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dayPeriodsFormat'
     */
    public  Map<String, Object> getFormattingPatterns(String locale, String scope, String scopeFilter) throws L10nException;

    /**
     *Get localized number by locale and scale
     *
     * @param locale  locale String. e.g. 'en-US'
     * @param number the number
     * @param scale decimal digits
     */
    public FormattedNumberResult getLocalizedNumber(String locale, String number, Integer scale);

    /** Get timezone Name list by display language and defaultTerritory
     *
     * @param displayLanguage the display language. e.g. 'en'
     * @param defaultTerritory a boolean value to get the default territory timezone name or not, default is true
     */
    public TimeZoneName getTimezoneNameList(String displayLanguage, boolean defaultTerritory) throws L10nException;

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
    public FormattedDateResult getLocalizedDate(String locale, long longDate, String pattern) throws L10nException;

}
