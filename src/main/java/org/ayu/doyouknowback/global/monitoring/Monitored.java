package org.ayu.doyouknowback.global.monitoring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {

    // DB_READ
    // DB_WRITE
    // PUSH_NOTIFICATION
    // TOTAL
    String value();
}
