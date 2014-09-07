package com.kris.massey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ApiVersion {
    final float ALL_VERSION_SUPPORTED = 0;

    boolean headerRequired() default true;
    float minVersion() default ALL_VERSION_SUPPORTED;
    float maxVersion() default ALL_VERSION_SUPPORTED;
}
