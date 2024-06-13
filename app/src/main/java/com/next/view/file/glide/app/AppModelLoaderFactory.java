package com.next.view.file.glide.app;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.next.view.file.info.AppInfo;

import java.io.InputStream;

/**
 * ClassName:应用模型加载工厂类
 *
 * @author Afton
 * @time 2024/6/6
 * @auditor
 */
public class AppModelLoaderFactory implements ModelLoaderFactory<AppInfo, InputStream> {

    //上下文
    private Context context;

    public AppModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<AppInfo, InputStream> build(@NonNull MultiModelLoaderFactory multiModelLoaderFactory) {
        return new AppIconModelLoader(this.context);
    }

    @Override
    public void teardown() {

    }
}