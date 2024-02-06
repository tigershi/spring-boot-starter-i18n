/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.springframework.boot.i18n.model;


import org.springframework.boot.i18n.utils.LocaleUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * Dto objects for locale data encapsulation
 */
public class LocaleResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 4879418823224316458L;
    private String displayName;

    private String locale;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = LocaleUtils.normalizeToLanguageTag(locale);
    }

}
