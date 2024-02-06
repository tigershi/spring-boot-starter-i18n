package org.springframework.boot.i18n.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;
import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.i18n.utils.timezone.TimeZoneName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.i18n.constant.L10nCategoriesConstant;
import org.springframework.boot.i18n.constant.MsgConstant;
import org.springframework.boot.i18n.model.FormattedDateResult;
import org.springframework.boot.i18n.model.FormattedNumberResult;
import org.springframework.boot.i18n.model.L10nException;
import org.springframework.boot.i18n.model.TerritoryResult;
import org.springframework.boot.i18n.utils.CommonUtility;
import org.springframework.util.StringUtils;

import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class L10nLocaleServiceImpl implements L10nLocaleService {
    private static Logger logger = LoggerFactory.getLogger(L10nLocaleServiceImpl.class);

    private L10nPatternService l10nPatternService = new L10nPatternServiceImpl();

    private L10nImageService l10nImageService = new L10nImageServiceImpl();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<TerritoryResult> gitRegionList(String languageList, boolean displayCity, String regions) throws L10nException {
        List<TerritoryResult> territoryList = new ArrayList<TerritoryResult>();
        String[] langArr = languageList.split(",");
        for (String lang : langArr) {
            String locale = lang.replace("_", "-");
            lang = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap).toLowerCase();
            logger.info("get Territory data from file");
            TerritoryResult territoryRegions = null;
            try {
                territoryRegions = getRegionsByLanguage(lang);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                throw new L10nException(e.getMessage(), e);
            }

            if (displayCity) {
                logger.info("cache cities is null, get data cites from file");
                TerritoryResult territoryCities = null;
                try {
                    territoryCities = getCitiesByLanguage(lang);
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                    throw new L10nException(e.getMessage(), e);
                }
                TerritoryResult territory = territoryRegions.shallowCopy();
                if (!StringUtils.isEmpty(regions) && !StringUtils.isEmpty(territoryCities.getCities())) {
                    Map<String, Object> cityMap = new HashMap<>();
                    Map<String, Object> originCityMap = territoryCities.getCities();
                    Arrays.stream(regions.split(",")).forEach(regionName -> {
                        regionName = regionName.toUpperCase();
                        if (originCityMap.containsKey(regionName)) {
                            cityMap.put(regionName, originCityMap.get(regionName));
                        }
                    });
                    territory.setCities(cityMap);
                } else {
                    territory.setCities(territoryCities.getCities());

                }
                territoryList.add(territory);
            } else {
                territoryList.add(territoryRegions);
            }

        }

        return territoryList;
    }


    private TerritoryResult getRegionsByLanguage(String language) throws JsonProcessingException {
        TerritoryResult dto = new TerritoryResult();
        dto.setLanguage(language);
        String regionJson = PatternUtil.getRegionFromLib(language.replace("_", "-"));
        if (StringUtils.isEmpty(regionJson)) {
            dto.setTerritories(null);
            dto.setDefaultRegionCode("");
            return dto;
        }
        Map<String, Object> map = objectMapper.readValue(regionJson, HashMap.class);
        Map<String, String> terrMap = (Map<String, String>) map.get(L10nCategoriesConstant.TERRITORIES);

        Object defaultRegionCode = map.get(L10nCategoriesConstant.DEFAULT_REGION_CODE);
        dto.setTerritories(terrMap);
        dto.setDefaultRegionCode(defaultRegionCode.toString());
        dto.setCities(null);
        return dto;
    }

    @SuppressWarnings("unchecked")
    private TerritoryResult getCitiesByLanguage(String language) throws JsonProcessingException {
        TerritoryResult dto = new TerritoryResult();
        dto.setLanguage(language);
        String citiesJson = PatternUtil.getCitiesFromLib(language.replace("_", "-"));
        if (!StringUtils.isEmpty(citiesJson)) {
            Map<String, Object> citiesMap = (Map<String, Object>) objectMapper.readValue(citiesJson, HashMap.class)
                    .get(L10nCategoriesConstant.CITIES);
            dto.setCities(citiesMap);
        }
        return dto;
    }


    @Override
    public FileChannel getCountryFlag(String region, int scale, String type) throws L10nException {
        return l10nImageService.getCountryFlagChannel(region, scale, type);
    }

    @Override
    public Map<String, Object> getFormattingPatterns(String language, String region, String scope, String scopeFilter) throws L10nException {
        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);

        try {
            return l10nPatternService.getPatternWithLanguageAndRegionFromFile(language, region, categories, scopeFilter);
        } catch (L10nException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> getFormattingPatterns(String locale, String scope, String scopeFilter) throws L10nException {
        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);
        return l10nPatternService.getPatternFromFile(locale, categories, scopeFilter);
    }



    @Override
    public TimeZoneName getTimezoneNameList(String locale, boolean defaultTerritory) throws L10nException {

        String newLocale = locale.replace("_", "-");
        newLocale = CommonUtil.getCLDRLocale(newLocale, localePathMap, localeAliasesMap);
        if (CommonUtil.isEmpty(newLocale)) {
            logger.info("Invalid locale!");
            throw new L10nException(String.format(MsgConstant.LOCALENAME_NOT_SUPPORTED, locale));
        }

        TimeZoneName timeZoneName = l10nPatternService.getTimeZoneName(newLocale, defaultTerritory);
        if (StringUtils.isEmpty(timeZoneName)) {
            logger.info("file data don't exist");
            return null;
        }

        return timeZoneName;
    }

    @Override
    public FormattedDateResult getLocalizedDate(String locale, long longDate, String pattern) throws L10nException {
        try {
            ULocale uLocale = new ULocale(locale);
            Date d = new Date(longDate);
            SimpleDateFormat format = new SimpleDateFormat(pattern, uLocale.toLocale());

            FormattedDateResult dateDTO = new FormattedDateResult();

            dateDTO.setLongDate(String.valueOf(longDate));
            dateDTO.setFormattedDate(format.format(d));;
            dateDTO.setLocale(locale);
            dateDTO.setPattern(pattern);
            return dateDTO;
        } catch (Exception e) {
            throw new L10nException(e.getMessage());
        }
    }

    @Override
    public FormattedNumberResult getLocalizedNumber(String locale, String number, Integer scale) {
        FormattedNumberResult numberDTO = new FormattedNumberResult();

        if (scale == null){
            numberDTO.setFormattedNumber(formatNumber(locale, number));
        }else {
            numberDTO.setFormattedNumber(formatNumber(locale, number, scale));
        }
        numberDTO.setLocale(locale);
        numberDTO.setScale(scale.toString());
        numberDTO.setNumber(number);

        return numberDTO;
    }


    /**
     * Format a number to localized number
     * @param locale A string representing a specific locale in [lang]_[country (region)] format. e.g., ja_JP, zh_CN
     * @param number The digits.
     * @return Localized number
     */
    public String formatNumber(String locale, String number) {
        Number num = this.parseNumber(number);
        ULocale uLocale = new ULocale(locale);
        return NumberFormat.getNumberInstance(uLocale).format(num);
    }

    /**
     * Format a number to localized number by scale
     * @param locale
     * @param number
     * @param scale
     * @return Localized number
     */
    public String formatNumber(String locale, String number, int scale) {
        Number num = this.parseNumber(number);
        ULocale uLocale = new ULocale(locale);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(uLocale);
        numberFormat.setMaximumFractionDigits(scale);
        numberFormat.setMinimumFractionDigits(scale);
        numberFormat.setRoundingMode(BigDecimal.ROUND_HALF_UP);
        return numberFormat.format(num);
    }

    /**
     * Parse a number string to number
     * @param numberStr
     * @return number
     */
    private Number parseNumber(String numberStr){
        try {
            return NumberFormat.getNumberInstance().parse(numberStr);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }



}
