package com.lyc.sample.impl;

import com.lyc.appinject.annotations.InjectApiImpl;
import com.lyc.appinject.sample.api.ISingleApi;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = ISingleApi.class)
public class SingleApiImpl implements ISingleApi {
    @Override
    public String logMsg() {
        return "I'm SingleApiImpl!";
    }
}
