package com.lyc.sample.impl;

import com.lyc.appinject.CreateMethod;
import com.lyc.appinject.annotations.InjectApiImpl;
import com.lyc.appinject.sample.api.IGetInstanceApi;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = IGetInstanceApi.class, createMethod = CreateMethod.GET_INSTANCE)
public class GetInstanceApiImpl implements IGetInstanceApi {
    private static GetInstanceApiImpl instance = new GetInstanceApiImpl();

    private GetInstanceApiImpl() {
    }

    public static IGetInstanceApi getInstance() {
        return instance;
    }

    @Override
    public String logMsg() {
        return "I'm GetInstanceApiImpl, created by getInstance()!";
    }
}
