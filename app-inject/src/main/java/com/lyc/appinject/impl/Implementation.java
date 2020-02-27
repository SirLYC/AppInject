package com.lyc.appinject.impl;

/**
 * Created by Liu Yuchuan on 2020/1/17.
 */
public abstract class Implementation {
    final Class<?> clazz;

    public Implementation(Class<?> clazz) {
        this.clazz = clazz;
    }

    public abstract Object createInstance();

    @Override
    public String toString() {
        return "Implementation{" +
                "clazz=" + clazz +
                '}';
    }
}
