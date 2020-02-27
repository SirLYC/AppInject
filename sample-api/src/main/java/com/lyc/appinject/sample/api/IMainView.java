package com.lyc.appinject.sample.api;

import android.content.Context;
import android.view.View;

import com.lyc.appinject.annotations.InjectApi;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
@InjectApi
public interface IMainView {
    View createContentView(Context context);
}
