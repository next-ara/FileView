package com.next.view.file.glide.app;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.next.view.file.info.AppInfo;

import java.io.InputStream;

/**
 * ClassName:应用图标模型加载类
 *
 * @author Afton
 * @time 2024/6/6
 * @auditor
 */
public class AppIconModelLoader implements ModelLoader<AppInfo, InputStream> {

    //上下文
    private Context context;

    public AppIconModelLoader(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull AppInfo appInfo, int i, int i1, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(appInfo.getPackageName()), new AppIconFetcher(appInfo, this.context));
    }

    @Override
    public boolean handles(@NonNull AppInfo appInfo) {
        return !TextUtils.isEmpty(appInfo.getPackageName());
    }
}