package com.lyc.appinject.sample.main;

import android.os.Bundle;
import android.view.View;

import com.lyc.appinject.ModuleApi;
import com.lyc.appinject.sample.api.IMainView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = ModuleApi.getInstance().getSingleApi(IMainView.class).createContentView(this);
        setContentView(view);
    }
}
