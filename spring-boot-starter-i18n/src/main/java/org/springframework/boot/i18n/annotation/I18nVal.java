package org.springframework.boot.i18n.annotation;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18nVal {
    /**
     * The actual value expression such as <code>${my.app.i18n.key}</code>.
     */
    String value();
}
