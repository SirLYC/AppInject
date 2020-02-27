package com.lyc.appinject.annotations;

import com.lyc.appinject.CreateMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For classes who implement interface annotated with {@link InjectApi}
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface InjectApiImpl {

    /**
     * Interface annotated with {@link InjectApi}.
     * This class must be one of the interfaces this class implements,
     * or build will fail.
     */
    Class<?> api();

    /**
     * How to create instance from class.
     */
    CreateMethod createMethod() default CreateMethod.NEW;
}
