package com.bbedu.bbspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 标注在目标方法的参数上，表示对应 http 请求的参数
 * @author BBChen
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
