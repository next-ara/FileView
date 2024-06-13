package com.next.view.file.tool;

import com.next.view.file.R;
import com.next.view.file.info.FileInfo;

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
        //应用程序
        public static final int FILE_TYPE_APPLICATION = 5;
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

        String mimeType = fileInfo.getFileType();
        return switch (mimeType) {
            case "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp" ->
                    FileType.FILE_TYPE_IMAGE;
            case "video/mp4", "video/3gpp", "video/x-msvideo", "video/quicktime", "video/mpeg", "video/webm", "video/x-matroska", "video/x-flv" ->
                    FileType.FILE_TYPE_VIDEO;
            case "audio/mpeg", "audio/x-wav", "audio/amr-wb", "audio/amr", "audio/aac", "application/ogg", "audio/ogg", "audio/x-flac", "audio/midi", "audio/x-midi", "audio/x-aiff", "audio/aiff", "audio/basic" ->
                    FileType.FILE_TYPE_AUDIO;
            case "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.presentation", "application/vnd.oasis.opendocument.spreadsheet", "text/plain", "application/rtf", "text/html", "application/xml", "text/xml" ->
                    FileType.FILE_TYPE_DOCUMENT;
            case "application/vnd.android.package-archive" -> FileType.FILE_TYPE_APPLICATION;
            case "application/x-rar-compressed", "application/zip", "multipart/x-zip", "application/octet-stream", "application/x-gzip", "application/gzip", "application/x-bzip2" ->
                    FileType.FILE_TYPE_ZIP;
            default -> FileType.FILE_TYPE_OTHER;
        };
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
            case FileType.FILE_TYPE_APPLICATION -> R.drawable.next_ic_file_apk;
            case FileType.FILE_TYPE_OTHER -> R.drawable.next_ic_file_zip;
            default -> R.drawable.next_ic_file_other;
        };
    }
}