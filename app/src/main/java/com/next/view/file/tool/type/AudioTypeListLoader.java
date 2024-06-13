package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:音频类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class AudioTypeListLoader extends TypeListLoader {

    //音频类型
    public static final String[] AUDIO_MIME_TYPE = {
            "audio/mpeg",
            "audio/x-wav",
            "audio/amr-wb",
            "audio/amr",
            "audio/aac",
            "application/ogg",
            "audio/ogg",
            "audio/x-flac",
            "audio/midi",
            "audio/x-midi",
            "audio/x-aiff",
            "audio/aiff",
            "audio/basic"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(AUDIO_MIME_TYPE);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_AUDIO.equals(fileType);
    }
}