package org.springframework.boot.i18n.model;

import java.io.Serializable;
import java.util.Map;

public class TranslationMsg implements Serializable {
    private String component = "default";
    private String locale = "en";
    private Map<String, Object> messages;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Map<String, Object> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Object> messages) {
        this.messages = messages;
    }


}
