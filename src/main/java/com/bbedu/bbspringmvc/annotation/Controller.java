package com.bbedu.bbspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 用于标识控制器组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
