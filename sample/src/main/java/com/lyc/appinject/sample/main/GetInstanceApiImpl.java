package com.lyc.appinject.sample.main;

import android.util.Log;

import com.lyc.appinject.CreateMethod;
import com.lyc.appinject.annotations.InjectApiImpl;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = IGetInstanceApi.class, createMethod = CreateMethod.GET_INSTANCE)
public class GetInstanceApiImpl implements IGetInstanceApi {
    public static IGetInstanceApi getInstance() {
        Log.d("IGetInstanceApi", "Get instance called!");
        return new GetInstanceApiImpl();
    }

    @Override
    public void logMsg() {
        Log.d("IGetInstanceApi", "I'm GetInstanceApiImpl!");
    }
}
