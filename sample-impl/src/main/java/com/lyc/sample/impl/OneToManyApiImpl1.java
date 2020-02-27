package com.lyc.sample.impl;

import com.lyc.appinject.annotations.InjectApiImpl;
import com.lyc.appinject.sample.api.IOneToManyApi;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = IOneToManyApi.class)
public class OneToManyApiImpl1 implements IOneToManyApi {
    @Override
    public String logMsg() {
        return "I'm OneToManyApiImpl1!";
    }
}
