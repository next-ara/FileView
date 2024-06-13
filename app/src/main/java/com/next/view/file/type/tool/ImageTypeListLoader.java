package com.next.view.file.type.tool;

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

    //图片后缀
    public static final String[] IMAGE_EXTENSION = {
            ".jpg",
            ".jpeg",
            ".png",
            ".gif",
            ".webp",
            ".bmp"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(IMAGE_EXTENSION);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_IMAGE.equals(fileType);
    }
}