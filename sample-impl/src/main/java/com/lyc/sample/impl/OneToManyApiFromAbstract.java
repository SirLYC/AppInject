package com.lyc.sample.impl;

import com.lyc.appinject.annotations.InjectApiImpl;
import com.lyc.appinject.sample.api.IOneToManyApi;

/**
 * Created by Liu Yuchuan on 2020/2/28.
 */
@InjectApiImpl(api = IOneToManyApi.class)
public class OneToManyApiFromAbstract extends AbstractOneToManyApi {
    @Override
    public String logMsg() {
        return "OneToManyApiFromAbstract";
    }
}
