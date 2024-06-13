package com.next.view.file.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.next.view.file.glide.app.AppModelLoaderFactory;
import com.next.view.file.info.AppInfo;

import java.io.InputStream;

/**
 * ClassName:文件Glide模块类
 *
 * @author Afton
 * @time 2024/6/6
 * @auditor
 */
@GlideModule
public class FileGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.prepend(AppInfo.class, InputStream.class, new AppModelLoaderFactory(context));
    }
}