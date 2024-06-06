package com.next.view.file.glide.apk;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.next.module.filehelper.info.ApkInfo;

import java.io.InputStream;

/**
 * ClassName:Apk模型加载工厂类
 *
 * @author Afton
 * @time 2024/6/6
 * @auditor
 */
public class ApkModelLoaderFactory implements ModelLoaderFactory<ApkInfo, InputStream> {

    //上下文
    private Context context;

    public ApkModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<ApkInfo, InputStream> build(@NonNull MultiModelLoaderFactory multiModelLoaderFactory) {
        return new ApkIconModelLoader(this.context);
    }

    @Override
    public void teardown() {

    }
}