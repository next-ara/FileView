package com.next.view.file.tool;

import com.next.view.file.R;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.type.AudioTypeListLoader;
import com.next.view.file.tool.type.DocumentTypeListLoader;
import com.next.view.file.tool.type.ImageTypeListLoader;
import com.next.view.file.tool.type.InstallTypeListLoader;
import com.next.view.file.tool.type.VideoTypeListLoader;
import com.next.view.file.tool.type.ZipTypeListLoader;

import java.util.Arrays;

/**
 * ClassName:文件类型工具类
 *
 * @author Afton
 * @time 2024/6/10
 * @auditor
 */
public class FileTypeTool {

    //文件类型
    public static class FileType {
        //文件夹
        public static final int FILE_TYPE_FOLDER = 0;
        //图片
        public static final int FILE_TYPE_IMAGE = 1;
        //视频
        public static final int FILE_TYPE_VIDEO = 2;
        //音频
        public static final int FILE_TYPE_AUDIO = 3;
        //文档
        public static final int FILE_TYPE_DOCUMENT = 4;
        //安装包
        public static final int FILE_TYPE_INSTALL = 5;
        //压缩包
        public static final int FILE_TYPE_ZIP = 6;
        //其他
        public static final int FILE_TYPE_OTHER = 7;
    }

    /**
     * 获取文件类型
     *
     * @param fileInfo 文件信息对象
     * @return 文件类型
     */
    public static int getFileType(FileInfo fileInfo) {
        if (fileInfo.isDirectory()) {
            return FileType.FILE_TYPE_FOLDER;
        }

        String fileExtension = getFileExtension(fileInfo.getFileName());
        if (Arrays.binarySearch(AudioTypeListLoader.AUDIO_EXTENSION, fileExtension) >= 0) {
            return FileType.FILE_TYPE_AUDIO;
        }

        if (Arrays.binarySearch(ImageTypeListLoader.IMAGE_EXTENSION, fileExtension) >= 0) {
            return FileType.FILE_TYPE_IMAGE;
        }

        if (Arrays.binarySearch(VideoTypeListLoader.VIDEO_EXTENSION, fileExtension) >= 0) {
            return FileType.FILE_TYPE_VIDEO;
        }

        if (Arrays.binarySearch(DocumentTypeListLoader.DOCUMENT_EXTENSION, fileExtension) >= 0) {
            return FileType.FILE_TYPE_DOCUMENT;
        }

        if (Arrays.binarySearch(ZipTypeListLoader.ZIP_EXTENSION, fileExtension) >= 0) {
            return FileType.FILE_TYPE_ZIP;
        }

        if (Arrays.binarySearch(InstallTypeListLoader.INSTALL_EXTENSION, fileExtension) >= 0) {
            return FileType.FILE_TYPE_INSTALL;
        }

        return FileType.FILE_TYPE_OTHER;
    }

    /**
     * 根据文件类型获取图标
     *
     * @param fileType 文件类型
     * @return 图标
     */
    public static int getFileIcon(int fileType) {
        return switch (fileType) {
            case FileType.FILE_TYPE_FOLDER -> R.drawable.next_ic_file_folder;
            case FileType.FILE_TYPE_IMAGE -> R.drawable.next_ic_file_image;
            case FileType.FILE_TYPE_VIDEO -> R.drawable.next_ic_file_video;
            case FileType.FILE_TYPE_AUDIO -> R.drawable.next_ic_file_audio;
            case FileType.FILE_TYPE_DOCUMENT -> R.drawable.next_ic_file_document;
            case FileType.FILE_TYPE_INSTALL -> R.drawable.next_ic_file_apk;
            case FileType.FILE_TYPE_ZIP -> R.drawable.next_ic_file_zip;
            default -> R.drawable.next_ic_file_other;
        };
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName 文件名
     * @return 文件后缀名
     */
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }

        return fileName.substring(dotIndex);
    }
}