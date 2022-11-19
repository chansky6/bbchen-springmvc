package com.bbedu.bbspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 用于指定 控制器-方法 的映射路径
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
