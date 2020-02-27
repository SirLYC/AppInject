package com.lyc.appinject;

import com.lyc.appinject.impl.Implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
public class ModuleApi {
    private static final Lock INSTANCE_LOCK = new ReentrantLock();
    private volatile static ModuleApi instance;
    private ReadWriteLock singleApiReadWriteLock = new ReentrantReadWriteLock();
    private Map<Class<?>, Object> singleApiCache = new HashMap<>();

    private ReadWriteLock oneToManyApiReadWriteLock = new ReentrantReadWriteLock();
    private Map<Class<?>, List<?>> oneToManyApiCache = new HashMap<>();

    private Lock userLoggerLock = new ReentrantLock();
    private ILogger userLogger;

    private static final String TAG = "ModuleApi";

    private ModuleApi() {

    }

    public void setUserLogger(ILogger userLogger) {
        try {
            userLoggerLock.lock();
            this.userLogger = userLogger;
        } finally {
            userLoggerLock.unlock();
        }
    }

    public static ILogger getLogger() {
        ILogger userLogger;
        Lock userLoggerLock = instance.userLoggerLock;
        try {
            userLoggerLock.lock();
            userLogger = instance.userLogger;
        } finally {
            userLoggerLock.unlock();
        }
        return userLogger == null ? DefaultLogger.getInstance() : userLogger;
    }

    public static ModuleApi getInstance() {
        if (instance == null) {
            try {
                INSTANCE_LOCK.lock();
                if (instance == null) {
                    instance = new ModuleApi();
                }
            } finally {
                INSTANCE_LOCK.unlock();
            }
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSingleApi(Class<T> serviceClass) {
        Object serviceImpl;
        Lock readLock = singleApiReadWriteLock.readLock();
        try {
            readLock.lock();
            serviceImpl = singleApiCache.get(serviceClass);
        } finally {
            readLock.unlock();
        }

        if (serviceImpl == null) {
            Lock writeLock = singleApiReadWriteLock.writeLock();
            try {
                writeLock.lock();
                serviceImpl = singleApiCache.get(serviceClass);
                if (serviceImpl == null) {
                    Implementation impl = ModuleApiHolders.getInstance().getSingleImpl(serviceClass);
                    if (impl != null) {
                        serviceImpl = impl.createInstance();
                        if (serviceImpl != null) {
                            getLogger().i(TAG, "[getSingleApi] create a new instance, impl=" + impl +
                                    ", serviceImpl=" + serviceImpl + ", serviceImplClass=" + serviceImpl.getClass().getName(), null);
                        }
                    }

                    if (serviceImpl == null) {
                        getLogger().i(TAG, "[getSingleApi] cannot a new instance, impl=" + impl, null);
                    }
                }
                if (serviceImpl != null) {
                    singleApiCache.put(serviceClass, serviceImpl);
                }
            } finally {
                writeLock.unlock();
            }
        }

        return (T) serviceImpl;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getMultiImpls(Class<T> extensionClass) {
        List<T> result = new ArrayList<>();
        boolean hasCache;
        Lock readLock = oneToManyApiReadWriteLock.readLock();
        try {
            readLock.lock();
            List<?> extensionList = oneToManyApiCache.get(extensionClass);
            hasCache = extensionList != null;
            if (hasCache) {
                for (Object o : extensionList) {
                    result.add((T) o);
                }
            }
        } finally {
            readLock.unlock();
        }

        if (!hasCache) {
            Lock writeLock = oneToManyApiReadWriteLock.writeLock();
            try {
                writeLock.lock();
                List<?> extensionList = oneToManyApiCache.get(extensionClass);
                hasCache = extensionList != null;
                if (hasCache) {
                    for (Object o : extensionList) {
                        result.add((T) o);
                    }
                } else {
                    List<Implementation> impls = ModuleApiHolders.getInstance().getOneToManyImplList(extensionClass);
                    if (impls != null) {
                        for (Implementation impl : impls) {
                            Object instance = impl.createInstance();
                            if (instance != null) {
                                getLogger().i(TAG, "[getMultiImpls] create a new instance, impl=" + impl +
                                        ", extensionImpl=" + instance + ", extensionImplClass=" + instance.getClass().getName(), null);
                                result.add((T) instance);
                            } else {
                                getLogger().i(TAG, "[getMultiImpls] cannot a new instance, impl=" + impl, null);
                            }
                        }
                        oneToManyApiCache.put(extensionClass, Collections.unmodifiableList(result));
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }

        return result;
    }

}
