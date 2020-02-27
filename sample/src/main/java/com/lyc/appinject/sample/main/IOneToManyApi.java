package com.lyc.appinject.sample.main;

import com.lyc.appinject.annotations.InjectApi;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApi(oneToMany = true)
public interface IOneToManyApi {
    void logMsg();
}

