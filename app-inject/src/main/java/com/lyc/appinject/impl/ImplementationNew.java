package com.lyc.appinject.impl;

import com.lyc.appinject.AppInject;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Liu Yuchuan on 2020/1/17.
 */
public class ImplementationNew extends Implementation {

    private static final String TAG = "ImplementationNew";

    public ImplementationNew(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Object createInstance() {
        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            AppInject.getLogger().e(TAG, null, e);
        } catch (NoSuchMethodException e) {
            AppInject.getLogger().e(TAG, null, e);
        } catch (InvocationTargetException e) {
            AppInject.getLogger().e(TAG, null, e);
        } catch (IllegalAccessException e) {
            AppInject.getLogger().e(TAG, null, e);
        }

        return null;
    }

    @Override
    public String toString() {
        return "ImplementationNew{" +
                "clazz=" + clazz +
                '}';
    }
}
