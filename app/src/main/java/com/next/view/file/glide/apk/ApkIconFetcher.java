package com.next.view.file.glide.apk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.next.module.filehelper.info.ApkInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * ClassName:Apk图标提取器类
 *
 * @author Afton
 * @time 2024/6/6
 * @auditor
 */
public class ApkIconFetcher implements DataFetcher<InputStream> {

    //Apk信息对象
    private ApkInfo apkInfo;

    //包管理器对象
    private final PackageManager packageManager;

    public ApkIconFetcher(ApkInfo apkInfo, Context context) {
        this.apkInfo = apkInfo;
        this.packageManager = context.getPackageManager();
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> dataCallback) {
        try {
            ApplicationInfo applicationInfo = this.packageManager.getApplicationInfo(this.apkInfo.getPackageName(), 0);

            Drawable iconDrawable = this.packageManager.getApplicationIcon(applicationInfo);
            Bitmap iconBitmap;

            if (iconDrawable instanceof BitmapDrawable bitmapDrawable) {
                iconBitmap = bitmapDrawable.getBitmap();
            } else {
                iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(),
                        iconDrawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(iconBitmap);
                iconDrawable.setBounds(0, 0, iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight());
                iconDrawable.draw(canvas);
            }

            InputStream inputStream = this.bitmapToInputStream(iconBitmap);
            dataCallback.onDataReady(inputStream);
        } catch (Exception e) {
            dataCallback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

    /**
     * 位图转输入流
     *
     * @param bitmap 位图
     * @return 输入流
     */
    private InputStream bitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}