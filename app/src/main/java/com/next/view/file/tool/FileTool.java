package com.next.view.file.tool;

import java.text.DecimalFormat;

/**
 * ClassName:文件工具类
 *
 * @author Afton
 * @time 2024/6/10
 * @auditor
 */
public class FileTool {

    /**
     * 文件大小转换
     *
     * @param fileS 文件大小
     * @return 文件大小文本
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";

        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }

        return fileSizeString;
    }
}