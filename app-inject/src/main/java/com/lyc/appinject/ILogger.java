package com.lyc.appinject;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
public interface ILogger {
    void d(String tag, String msg, Throwable t);

    void i(String tag, String msg, Throwable t);

    void w(String tag, String msg, Throwable t);

    void e(String tag, String msg, Throwable t);
}
