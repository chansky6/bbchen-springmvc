package com.bbedu.bbspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 用于标识 Service 对象，并注入 Spring 对象
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
