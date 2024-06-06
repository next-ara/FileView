package com.next.view.file.glide.apk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.next.module.filehelper.info.ApkInfo;

import java.io.InputStream;

/**
 * ClassName:Apk图标模型加载类
 *
 * @author Afton
 * @time 2024/6/6
 * @auditor
 */
public class ApkIconModelLoader implements ModelLoader<ApkInfo, InputStream> {

    //上下文
    private Context context;

    public ApkIconModelLoader(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull ApkInfo apkInfo, int i, int i1, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(apkInfo.getFilePath()), new ApkIconFetcher(apkInfo, this.context));
    }

    @Override
    public boolean handles(@NonNull ApkInfo apkInfo) {
        return true;
    }
}