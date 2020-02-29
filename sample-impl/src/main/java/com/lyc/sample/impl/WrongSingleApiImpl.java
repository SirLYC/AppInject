//package com.lyc.sample.impl;
//
//import com.lyc.appinject.annotations.InjectApi;
//import com.lyc.appinject.annotations.InjectApiImpl;
//import com.lyc.appinject.sample.api.ISingleApi;
//
///**
// * Created by Liu Yuchuan on 2020/2/29.
// * This class will lead to build error for
// * {@link ISingleApi} has {@link InjectApi#oneToMany()} == false
// */
//@InjectApiImpl(api = ISingleApi.class)
//public class WrongSingleApiImpl implements ISingleApi {
//    @Override
//    public String logMsg() {
//        throw new RuntimeException(new IllegalAccessException("Never called"));
//    }
//}
