package org.springframework.boot.i18n.service;

import com.vmware.i18n.utils.timezone.TimeZoneName;
import org.springframework.boot.i18n.model.L10nException;

import java.util.List;
import java.util.Map;

public interface L10nPatternService {
    /**
     * Get i18n pattern data according to the locale and categoryList value
     * @param locale
     * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
     * @param scopeFilter a String for filtering out the pattern data, separated by commas and underline.
     * @return
     */
    public Map<String, Object> getPatternFromFile(String locale, List<String> categoryList, String scopeFilter) throws L10nException;

    /**
     * Get i18n pattern data according to the language, region and categoryList value
     * @param language
     * @param region
     * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
     * @param scopeFilter a String for filtering out the pattern data, separated by commas and underline.
     * @return
     */
    public Map<String, Object> getPatternWithLanguageAndRegionFromFile(String language, String region, List<String> categoryList, String scopeFilter) throws L10nException;

    /**
     * @param locale
     * @param defaultTerritory value representing default territory
     * @return timezone name of the locale
     */
    public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory);

}
