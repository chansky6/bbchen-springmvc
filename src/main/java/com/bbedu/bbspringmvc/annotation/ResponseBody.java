package com.bbedu.bbspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author BBChen
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
}
