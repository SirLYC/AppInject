package com.lyc.appinject.sample.api;

import com.lyc.appinject.annotations.InjectApi;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApi(oneToMany = true)
public interface IOneToManyApi {
    String logMsg();
}

