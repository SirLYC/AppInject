package com.lyc.appinject.annotations;

import com.lyc.appinject.AppInject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For interface to implemented.
 * It's implemented classed should be annotated with {@link InjectApiImpl}.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface InjectApi {
    /**
     * If this interface can be implemented by more than one class.
     * If true, get its implements class by {@link AppInject#getOneToManyApiList(Class)}
     * else {@link AppInject#getSingleApi(Class)}
     */
    boolean oneToMany() default false;
}
