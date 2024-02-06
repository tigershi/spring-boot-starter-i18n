package org.springframework.boot.i18n.model;


import org.springframework.boot.i18n.constant.L10nConstant;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class TimezoneResult implements Serializable {


    private static final long serialVersionUID = 9060484294234938026L;

    /**
     * @param gmtZeroFormat
     * @param gmtFormat
     * @param hourFormat
     * @param regionFormat
     * @param regionFormatTypeDaylight
     * @param regionFormatTypeStandard
     * @param fallbackFormat
     * @param metaZones */
    public TimezoneResult(String language, String gmtZeroFormat, String gmtFormat, String hourFormat,
                          String regionFormat, String regionFormatTypeDaylight, String regionFormatTypeStandard,
                          String fallbackFormat, List<LinkedHashMap<String,Object>> metaZones) {
        this.language = language;
        this.timeZoneNames = new LinkedHashMap<String,Object>();
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_HOUR_FORMAT, hourFormat);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_GMT_FORMAT, gmtFormat);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_GMT_ZERO_FORMAT,gmtZeroFormat);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_REGION_FORMAT,regionFormat);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_REGION_FORMAT_TYPE_DAYLIGHT,regionFormatTypeDaylight);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_REGION_FORMAT_TYPE_STANDARD,regionFormatTypeStandard);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_FALLBACK_FORMAT,fallbackFormat);
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_METAZONES,metaZones);
    }
    public TimezoneResult() {}

    private String language;
    private LinkedHashMap <String, Object> timeZoneNames;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LinkedHashMap<String, Object> getTimeZoneNames() {
        return timeZoneNames;
    }

    public void setTimeZoneNames(LinkedHashMap<String, Object> timeZoneNames) {
        this.timeZoneNames = timeZoneNames;
    }

    @SuppressWarnings("unchecked")
    public  List<LinkedHashMap<String,Object>> queryMetaZones() {
        return (List<LinkedHashMap<String,Object>>) this.timeZoneNames.get(L10nConstant.TIMEZONENAME_METAZONES);
    }

    public void resetMetaZones(List<LinkedHashMap<String,Object>> metaZones) {
        this.timeZoneNames.put(L10nConstant.TIMEZONENAME_METAZONES,metaZones);
    }
}
