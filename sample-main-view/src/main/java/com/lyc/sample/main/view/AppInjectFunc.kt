package com.lyc.sample.main.view

import com.lyc.appinject.AppInject

/**
 * Created by Liu Yuchuan on 2020/2/27.
 * An example for kotlin
 */
inline fun <reified T> getSingleApi(): T? {
    return AppInject.getInstance().getSingleApi(T::class.java)
}

inline fun <reified T> getOneToManyApiList(): List<T> {
    return AppInject.getInstance().getOneToManyApiList(T::class.java)
}
