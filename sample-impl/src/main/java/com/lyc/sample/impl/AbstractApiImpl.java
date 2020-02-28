package com.lyc.sample.impl;

import com.lyc.appinject.annotations.InjectApiImpl;
import com.lyc.appinject.sample.api.AbstractApi;

/**
 * Created by Liu Yuchuan on 2020/2/28.
 */
@InjectApiImpl(api = AbstractApi.class)
public class AbstractApiImpl extends AbstractApi {
    @Override
    public String logMsg() {
        return "I'm AbstractApiImpl";
    }
}
