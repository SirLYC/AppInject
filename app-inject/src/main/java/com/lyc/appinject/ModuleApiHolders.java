package com.lyc.appinject;

import com.lyc.appinject.impl.Implementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
class ModuleApiHolders {

    private static final Lock INSTANCE_LOCK = new ReentrantLock();
    private static volatile ModuleApiHolders instance;
    private Map<Class<?>, Implementation> singleApiClassMap = new HashMap<>();
    private Map<Class<?>, List<Implementation>> oneToManyApiClassMap = new HashMap<>();

    private ModuleApiHolders() {
        initSingleApiMap();
        initOneToManyApiMap();
    }

    static ModuleApiHolders getInstance() {
        if (instance == null) {
            try {
                INSTANCE_LOCK.lock();
                if (instance == null) {
                    instance = new ModuleApiHolders();
                }
            } finally {
                INSTANCE_LOCK.unlock();
            }
        }

        return instance;
    }

    // 插桩方法
    private void initSingleApiMap() {
    }

    // 插桩方法
    private void initOneToManyApiMap() {
        List<Implementation> list;
    }

    Implementation getSingleImpl(Class<?> serviceClazz) {
        return singleApiClassMap.get(serviceClazz);
    }

    List<Implementation> getOneToManyImplList(Class<?> extensionClazz) {
        return oneToManyApiClassMap.get(extensionClazz);
    }
}
