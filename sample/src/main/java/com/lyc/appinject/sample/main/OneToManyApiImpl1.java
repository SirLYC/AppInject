package com.lyc.appinject.sample.main;

import android.util.Log;

import com.lyc.appinject.annotations.InjectApiImpl;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = IOneToManyApi.class)
public class OneToManyApiImpl1 implements IOneToManyApi {
    @Override
    public void logMsg() {
        Log.d("IOneToManyApi", "I'm OneToManyApiImpl1!");
    }
}
