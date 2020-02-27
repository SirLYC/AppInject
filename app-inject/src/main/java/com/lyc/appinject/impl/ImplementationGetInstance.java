package com.lyc.appinject.impl;

import com.lyc.appinject.ModuleApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Liu Yuchuan on 2020/1/17.
 */
public class ImplementationGetInstance extends Implementation {

    private static final String TAG = "ImplementationGetInstance";

    public ImplementationGetInstance(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Object createInstance() {
        try {
            Method method = clazz.getMethod("getInstance");
            return method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            ModuleApi.getLogger().e(TAG, null, e);
        }
        return null;
    }

    @Override
    public String toString() {
        return "ImplementationGetInstance{" +
                "clazz=" + clazz +
                '}';
    }
}
