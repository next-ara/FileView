package com.next.view.file;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.next.module.filehelper.config.FileManageConfig;

import java.io.File;

/**
 * ClassName:文件工具类
 *
 * @author Afton
 * @time 2023/10/12
 * @auditor
 */
public class FileTool {

    /**
     * 文件对象转Uri对象
     *
     * @param file 文件对象
     * @return Uri对象
     */
    public static Uri fileToUri(File file) {
        Context context = FileManageConfig.getInstance().getApplication();
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }
}