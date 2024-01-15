package org.springframework.boot.i18n.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface I18nComponent {
    @AliasFor(annotation = Component.class)
    String value() default "default";

    String prefix() default  "";
}
