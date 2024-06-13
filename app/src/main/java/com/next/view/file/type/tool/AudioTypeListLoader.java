package com.next.view.file.type.tool;

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

    //音频后缀
    public static final String[] AUDIO_EXTENSION = {
            ".mp3",
            ".wav",
            ".amr",
            ".aac",
            ".ogg",
            ".flac",
            ".mid",
            ".midi",
            ".aiff",
            ".aif",
            ".au",
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(AUDIO_EXTENSION);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_AUDIO.equals(fileType);
    }
}