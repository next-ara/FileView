package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:类型列表工厂类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class TypeListFactory {

    //类型列表
    public static class Type {
        //图片
        public static final String TYPE_IMAGE = "image";
        //视频
        public static final String TYPE_VIDEO = "video";
        //音频
        public static final String TYPE_AUDIO = "audio";
        //文档
        public static final String TYPE_DOCUMENT = "document";
        //安装包
        public static final String TYPE_INSTALL = "install";
        //压缩包
        public static final String TYPE_ZIP = "zip";
    }

    //类型列表加载器列表
    private ArrayList<TypeListLoader> typeListLoaders;

    public TypeListFactory() {
        //初始化类型列表加载器列表
        this.initTypeListLoaders();
    }

    /**
     * 获取类型列表
     *
     * @param fileType 文件类型
     * @return 类型列表
     */
    public ArrayList<FileInfo> getTypeList(String fileType) {
        for (TypeListLoader typeListLoader : this.typeListLoaders) {
            if (typeListLoader.isExecute(fileType)) {
                return typeListLoader.getTypeList();
            }
        }

        return null;
    }

    /**
     * 初始化类型列表加载器列表
     */
    private void initTypeListLoaders() {
        this.typeListLoaders = new ArrayList<>();
        //图片类型列表加载器注册
        this.typeListLoaders.add(new ImageTypeListLoader());
        //音频类型列表加载器注册
        this.typeListLoaders.add(new AudioTypeListLoader());
        //视频类型列表加载器注册
        this.typeListLoaders.add(new VideoTypeListLoader());
    }
}