package com.orion.messagingService.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventDuplicateSubmit {
    /**
     * 超时时间默认为5秒
     */
    long timeout() default 5000;

    boolean autoCleanup() default false;
}
