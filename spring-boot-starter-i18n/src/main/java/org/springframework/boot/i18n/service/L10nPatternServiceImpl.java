package org.springframework.boot.i18n.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.i18n.utils.timezone.TimeZoneName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.i18n.constant.CategoriesEnum;
import org.springframework.boot.i18n.constant.CharConstant;
import org.springframework.boot.i18n.constant.L10nCategoriesConstant;
import org.springframework.boot.i18n.model.L10nException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class L10nPatternServiceImpl implements L10nPatternService {
    private static final Log logger = LogFactory.getLog(L10nPatternServiceImpl.class);
    private static List<String> specialCategories = Arrays.asList(L10nCategoriesConstant.PLURALS, L10nCategoriesConstant.DATE_FIELDS, L10nCategoriesConstant.MEASUREMENTS, L10nCategoriesConstant.CURRENCIES);
    private static List<String> otherCategories = Arrays.asList(L10nCategoriesConstant.DATES, L10nCategoriesConstant.NUMBERS);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getPatternFromFile(String locale, List<String> categoryList, String scopeFilter) throws L10nException {
        locale = locale.replace("_", "-");
        String newLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
        if (CommonUtil.isEmpty(newLocale)) {
            logger.info("Invalid locale!");
            return buildPatternMap(locale);
        }
        locale = newLocale;

        supplyDependentCategories(categoryList);


        String patternJson = getPattern(locale, null);
        Map<String, Object> patternMap = null;
        try {
            patternMap = objectMapper.readValue(patternJson, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new L10nException("get pattern local error", e);
        }

        if (StringUtils.isEmpty(patternMap.get(L10nCategoriesConstant.REGION))) {
            String regionJson = PatternUtil.getRegionFromLib(locale.replace("_", "-"));
            if (StringUtils.hasLength(regionJson)) {

                Object region = null;
                try {
                    region = objectMapper.readValue(regionJson, HashMap.class).get(L10nCategoriesConstant.DEFAULT_REGION_CODE);
                } catch (JsonProcessingException e) {
                    throw new L10nException("get pattern region error", e);
                }
                patternMap.put(L10nCategoriesConstant.REGION, region.toString());
            }
        }


        Map<String, Object> categoryPatternMap = getCategories(categoryList, patternMap);
        filterScope(categoryPatternMap, scopeFilter);
        patternMap.put(L10nCategoriesConstant.CATEGORIES, categoryPatternMap);
        return patternMap;
    }

    /**
     * Get i18n pattern with language, region and scope parameter.
     *
     * @param language
     * @param region
     * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
     * @param scopeFilter  a String for filtering out the pattern data, separated by commas and underline.
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getPatternWithLanguageAndRegionFromFile(String language, String region, List<String> categoryList, String scopeFilter) throws L10nException {
        logger.info("Get i18n pattern with language: " + language + ",region: " + region + " and categories: " + categoryList);
        Map<String, Object> patternMap = null;
        String patternJson = "";
        language = language.replace("_", "-");
        LocaleDataDTO resultData = CommonUtil.getLocale(language, region);
        String locale = resultData.getLocale();
        logger.info(locale);

        patternJson = getPattern(locale, null);
        if (StringUtils.isEmpty(patternJson)) {
            logger.info("file data don't exist");
            return buildPatternMap(language, region, patternJson, categoryList, scopeFilter, resultData);
        }

        logger.info("get pattern data from cache");
        patternMap = buildPatternMap(language, region, patternJson, categoryList, scopeFilter, resultData);
        if (!CommonUtil.isEmpty(patternMap) && !CommonUtil.isEmpty(patternMap.get(L10nCategoriesConstant.CATEGORIES))) {
            filterScope((Map<String, Object>) patternMap.get(L10nCategoriesConstant.CATEGORIES), scopeFilter);
        }
        logger.info("The result pattern: "+ patternMap);
        logger.info("Get i18n pattern successful");
        return patternMap;
    }

    /**
     * Build pattern
     *
     * @param patternJson
     * @param categoryList
     * @return
     */
    private Map<String, Object> buildPatternMap(String language, String region, String patternJson, List<String> categoryList, String scopeFilter, LocaleDataDTO localeDataDTO) throws L10nException {
        Map<String, Object> patternMap = new LinkedHashMap<>();
        Map<String, Object> categoriesMap = new LinkedHashMap<>();

        supplyDependentCategories(categoryList);

        if (StringUtils.isEmpty(patternJson)) {
            patternMap.put(L10nCategoriesConstant.LOCALE_ID, "");
            for (String category : categoryList) {
                categoriesMap.put(category, null);
            }
        } else {
            try {
                patternMap = objectMapper.readValue(patternJson, HashMap.class);
            } catch (JsonProcessingException e) {
                throw new L10nException(e.getMessage(), e);
            }
            categoriesMap = getCategories(categoryList, patternMap);
        }

        //when the combination of language and region is invalid, specialCategories(plurals,currencies,dateFields,measurements) data fetching follow language
        if (!localeDataDTO.isDisplayLocaleID() || localeDataDTO.getLocale().isEmpty()) {
            for(String category : categoryList){
                if(specialCategories.contains(category) && !CategoriesEnum.CURRENCIES.getText().equals(category)){
                    handleSpecialCategory(category, language, categoriesMap, scopeFilter);
                }
            }
            patternMap.put(L10nCategoriesConstant.LOCALE_ID, "");
        }

        // fix issue: https://github.com/vmware/singleton/issues/311
        if (!Collections.disjoint(categoryList, specialCategories)) {
            if (Collections.disjoint(categoryList, otherCategories)) {
                patternMap.put(L10nCategoriesConstant.LOCALE_ID, language);
            } else if (!language.equals(localeDataDTO.getLocale())) {
                patternMap.put(L10nCategoriesConstant.LOCALE_ID, "");
            }
        }

        patternMap.put(L10nCategoriesConstant.LANGUAGE, language);
        patternMap.put(L10nCategoriesConstant.REGION, region);
        patternMap.put(L10nCategoriesConstant.CATEGORIES, categoriesMap);
        return patternMap;
    }

    private void handleSpecialCategory(String category, String language, Map<String, Object> categoriesMap, String scopeFilter) throws L10nException {
        categoriesMap.put(category, null);
        Map<String, Object> patternMap = getPatternFromFile(language, Arrays.asList(category), scopeFilter);
        if (null != patternMap.get(L10nCategoriesConstant.CATEGORIES)) {
            Map<String, Object> categoryMap = (Map<String, Object>) patternMap.get(L10nCategoriesConstant.CATEGORIES);
            if (null != categoryMap.get(category)) {
                categoriesMap.put(category, categoryMap.get(category));
            }
        }
    }

    /**
     * Filtering out the pattern data
     *
     * @param patternMap
     * @param scopeFilter
     */
    private void filterScope(Map<String, Object> patternMap, String scopeFilter) {
        if (CommonUtil.isEmpty(scopeFilter) || CommonUtil.isEmpty(patternMap)) {
            return;
        }

        // If scopeFilter starts with ^, it means deleting the data under the node
        if (scopeFilter.startsWith(CharConstant.REVERSE)) {
            scopeFilter = scopeFilter.substring(scopeFilter.indexOf(CharConstant.LEFT_PARENTHESIS) + 1, scopeFilter.indexOf(CharConstant.RIGHT_PARENTHESIS));
            Arrays.asList(scopeFilter.split(CharConstant.COMMA)).stream().forEach(scopeNode -> {
                List<String> scopeFilters = Arrays.asList(scopeNode.split(CharConstant.UNDERLINE));
                removeData(patternMap, scopeFilters, 0);
            });
        } else {
            Map<String, Object> newPatternMap = new HashMap<>();
            for (String scopeNode : Arrays.asList(scopeFilter.split(CharConstant.COMMA))) {
                List<String> scopeFilters = Arrays.asList(scopeNode.split(CharConstant.UNDERLINE));
                Map<String, Object> tempPatternMap = getData(patternMap, scopeFilters, 0);
                newPatternMap = mergePatternMap(newPatternMap, tempPatternMap, scopeFilters, 0);
            }
            patternMap.putAll(newPatternMap);
        }
    }


    private void removeData(Map<String, Object> patternMap, List<String> scopeFilters, Integer index) {
        String scopeFilter = scopeFilters.get(index);
        if (index == scopeFilters.size() - 1) {
            patternMap.remove(scopeFilter);
        } else if (!CommonUtil.isEmpty(patternMap.get(scopeFilter))) {
            patternMap = (Map<String, Object>) patternMap.get(scopeFilter);
            removeData(patternMap, scopeFilters, index + 1);
        }
    }

    /**
     * Obtain the data of the corresponding node in the pattern according to scopeFilters
     *
     * @param originPatternMap
     * @param scopeFilters
     * @param index
     * @return
     */
    private Map<String, Object> getData(Map<String, Object> originPatternMap, List<String> scopeFilters, Integer index) {
        String scopeFilter = scopeFilters.get(index);
        Map<String, Object> patternMap = new HashMap<>();

        if ((index == scopeFilters.size() - 1) || (CommonUtil.isEmpty(originPatternMap.get(scopeFilter)))) {
            patternMap.put(scopeFilter, originPatternMap.get(scopeFilter));
        } else {
            originPatternMap = (Map<String, Object>) originPatternMap.get(scopeFilter);
            patternMap.put(scopeFilter, getData(originPatternMap, scopeFilters, index + 1));
        }
        return patternMap;
    }

    /**
     * Merging pattern data
     *
     * @param originPatternMap
     * @param newPatternMap
     * @param scopeFilters
     * @param index
     * @return
     */
    private Map<String, Object> mergePatternMap(Map<String, Object> originPatternMap, Map<String, Object> newPatternMap,
                                                List<String> scopeFilters, Integer index) {
        Map<String, Object> patternMap = new HashMap<>(originPatternMap);
        String scopeFilter = scopeFilters.get(index);

        if (!CommonUtil.isEmpty(originPatternMap.get(scopeFilter))) {
            originPatternMap = (Map<String, Object>) originPatternMap.get(scopeFilter);
            newPatternMap = (Map<String, Object>) newPatternMap.get(scopeFilter);
            patternMap.put(scopeFilter, mergePatternMap(originPatternMap, newPatternMap, scopeFilters, index + 1));
        } else {
            patternMap.putAll(newPatternMap);
        }

        return patternMap;
    }


    /**
     * Getting categories according to pattern
     *
     * @param categoryList
     * @param patternMap
     * @return
     */
    private Map<String, Object> getCategories(List<String> categoryList, Map<String, Object> patternMap) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> categoriesMap = (Map<String, Object>) patternMap.get(L10nCategoriesConstant.CATEGORIES);
        Map<String, Object> supplementMap = (Map<String, Object>) categoriesMap.get(L10nCategoriesConstant.SUPPLEMENT);
        Map<String, Object> suppMap = new HashMap<>();
        for (String cat : categoryList) {
            if (!CommonUtil.isEmpty(categoriesMap.get(cat))) {
                resultMap.put(cat, categoriesMap.get(cat));
            }
            if (!CommonUtil.isEmpty(supplementMap.get(cat))) {
                suppMap.put(cat, supplementMap.get(cat));
            }
        }
        //add the supplement data
        resultMap.put(L10nCategoriesConstant.SUPPLEMENT, suppMap);

        return resultMap;
    }

    private void supplyDependentCategories(List<String> categoryList) throws L10nException {
        try {
            if (CollectionUtils.containsAny(categoryList, specialCategories) && !categoryList.contains(L10nCategoriesConstant.PLURALS)) {
                categoryList.add(L10nCategoriesConstant.PLURALS);
            }
            if (categoryList.contains(L10nCategoriesConstant.PLURALS) && !categoryList.contains(L10nCategoriesConstant.NUMBERS)) {
                categoryList.add(L10nCategoriesConstant.NUMBERS);
            }
        } catch (
                UnsupportedOperationException e) {//catch the exception when refetch plural and dateFields by language for getPatternWithLanguageAndRegion API
            throw new L10nException(e.getMessage(), e);
        }
    }


    private Map<String, Object> buildPatternMap(String locale) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(L10nCategoriesConstant.LOCALE_ID, locale);
        map.put(L10nCategoriesConstant.CATEGORIES, null);
        return map;
    }

    private String getPattern(String locale, String categories) {
        locale = locale.replace("_", "-");
        return PatternUtil.getPatternFromLib(locale, categories);
    }


    /**
     * @param locale
     * @param defaultTerritory value representing default territory
     * @return timezone name of the locale
     */
    public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) {
        locale = locale.replace("_", "-");
        return PatternUtil.getTimeZoneName(locale, defaultTerritory);
    }


}
