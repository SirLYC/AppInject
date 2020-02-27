package com.lyc.appinject.sample.main;

import android.os.Bundle;

import com.lyc.appinject.ModuleApi;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ISingleApi singleApi = ModuleApi.getInstance().getSingleApi(ISingleApi.class);
        singleApi.logMsg();
        for (IOneToManyApi impl : ModuleApi.getInstance().getMultiImpls(IOneToManyApi.class)) {
            impl.logMsg();
        }
    }

    @Override
    protected void onResume() {
        IGetInstanceApi getInstanceApi = ModuleApi.getInstance().getSingleApi(IGetInstanceApi.class);
        getInstanceApi.logMsg();
        super.onResume();
    }
}
