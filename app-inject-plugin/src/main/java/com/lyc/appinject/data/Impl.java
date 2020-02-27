package com.lyc.appinject.data;

import java.util.Objects;

/**
 * Created by Liu Yuchuan on 2020/1/17.
 */
public class Impl {
    public final String className;
    public final String createMethod;

    public Impl(String className, String createMethod) {
        this.className = className;
        if (createMethod == null) {
            this.createMethod = "NEW";
        } else {
            this.createMethod = createMethod;
        }
        if (className == null) {
            throw new NullPointerException("className is null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Impl impl = (Impl) o;

        if (!Objects.equals(className, impl.className))
            return false;
        return Objects.equals(createMethod, impl.createMethod);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + createMethod.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Impl{" +
                "className='" + className + '\'' +
                ", createMethod='" + createMethod + '\'' +
                '}';
    }
}
