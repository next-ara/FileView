package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:视频类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class VideoTypeListLoader extends TypeListLoader {

    //视频后缀
    public static final String[] VIDEO_EXTENSION = {
            ".mp4",
            ".3gp",
            ".avi",
            ".mov",
            ".mpeg",
            ".mpg",
            ".m1v",
            ".m2v",
            ".webm",
            ".mkv",
            ".flv"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(VIDEO_EXTENSION);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_VIDEO.equals(fileType);
    }
}