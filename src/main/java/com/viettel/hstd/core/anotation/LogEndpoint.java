package com.viettel.hstd.core.anotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogEndpoint {
    String name() default "";
    String code() default "";
}
