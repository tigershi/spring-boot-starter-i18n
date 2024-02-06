/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.springframework.boot.i18n.model;


import org.springframework.boot.i18n.utils.LocaleUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * Dto objects for date encapsulation
 */
public class FormattedDateResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -8305248727237332556L;

    private String pattern;
    private String locale;
    private String longDate;
    private String formattedDate;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = LocaleUtils.normalizeToLanguageTag(locale);
    }

    public String getLongDate() {
        return longDate;
    }

    public void setLongDate(String longDate) {
        this.longDate = longDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
