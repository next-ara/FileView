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

    //视频类型
    public static final String[] VIDEO_MIME_TYPE = {
            "video/mp4",
            "video/3gpp",
            "video/x-msvideo",
            "video/quicktime",
            "video/mpeg",
            "video/webm",
            "video/x-matroska",
            "video/x-flv"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(VIDEO_MIME_TYPE);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_VIDEO.equals(fileType);
    }
}