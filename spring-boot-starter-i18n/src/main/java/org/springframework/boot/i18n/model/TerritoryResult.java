/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.springframework.boot.i18n.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class TerritoryResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -4445840261590213760L;


    private String language;

    private String defaultRegionCode;

    private Map<String, String> territories;

    private Map<String, Object> cities;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefaultRegionCode() {
        return defaultRegionCode;
    }

    public void setDefaultRegionCode(String defaultRegionCode) {
        this.defaultRegionCode = defaultRegionCode;
    }

    public Map<String, String> getTerritories() {
        return territories;
    }

    public void setTerritories(Map<String, String> territories) {
        this.territories = territories;
    }

    public Map<String, Object> getCities() {
        return cities;
    }

    public void setCities(Map<String, Object> cities) {
        this.cities = cities;
    }

    public TerritoryResult shallowCopy() {
        TerritoryResult newDTO = new TerritoryResult();
        newDTO.language = language;
        newDTO.defaultRegionCode = defaultRegionCode;
        newDTO.territories = territories;
        newDTO.cities = cities;
        return newDTO;
    }
}
