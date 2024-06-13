package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:压缩包类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class ZipTypeListLoader extends TypeListLoader {

    //压缩包类型
    public static final String[] ZIP_EXTENSION = {
            ".rar",
            ".zip",
            ".gz",
            ".bz",
            ".bz2",
            ".7z",
            ".xz"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(ZIP_EXTENSION);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_ZIP.equals(fileType);
    }
}