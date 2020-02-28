package com.lyc.appinject.sample.api;

import com.lyc.appinject.annotations.InjectApi;

/**
 * Created by Liu Yuchuan on 2020/2/28.
 */
@InjectApi
public abstract class AbstractApi {
    public abstract String logMsg();
}
