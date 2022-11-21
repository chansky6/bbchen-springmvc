package com.bbedu.bbspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author BBChen
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWired {
    String value() default "";
}
