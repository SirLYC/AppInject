package com.lyc.appinject;

import android.util.Log;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
class DefaultLogger implements ILogger {
    private DefaultLogger() {

    }

    private static volatile ILogger instance;

    public static ILogger getInstance() {
        if (instance == null) {
            synchronized (DefaultLogger.class) {
                if (instance == null) {
                    instance = new DefaultLogger();
                }
            }
        }

        return instance;
    }

    @Override
    public void d(String tag, String msg, Throwable t) {
        Log.d(tag, msg, t);
    }

    @Override
    public void i(String tag, String msg, Throwable t) {
        Log.i(tag, msg, t);
    }

    @Override
    public void w(String tag, String msg, Throwable t) {
        Log.w(tag, msg, t);
    }

    @Override
    public void e(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
    }
}
