package org.springframework.boot.i18n.model;

import java.io.Serial;
import java.io.Serializable;

public class FormattedNumberResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 5060424851621197331L;
    private String number;
    private String formattedNumber;
    private String scale;
    private String locale;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFormattedNumber() {
        return formattedNumber;
    }

    public void setFormattedNumber(String formattedNumber) {
        this.formattedNumber = formattedNumber;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


}
