package com.lyc.appinject.sample.main;

import android.util.Log;

import com.lyc.appinject.annotations.InjectApiImpl;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApiImpl(api = ISingleApi.class)
public class SingleApiImpl implements ISingleApi {
    @Override
    public void logMsg() {
        Log.d("ISingleApi", "I'm SingleApiImpl!");
    }
}
