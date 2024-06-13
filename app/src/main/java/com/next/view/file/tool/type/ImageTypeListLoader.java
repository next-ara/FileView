package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:图片类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class ImageTypeListLoader extends TypeListLoader {

    //图片类型
    public static final String[] IMAGE_MIME_TYPE = {
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(IMAGE_MIME_TYPE);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_IMAGE.equals(fileType);
    }
}