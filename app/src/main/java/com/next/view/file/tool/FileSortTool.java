package com.next.view.file.tool;

import com.next.view.file.info.FileInfo;
import com.next.view.file.manage.GetFileListTool;

import java.util.ArrayList;

/**
 * ClassName:文件排序工具类
 *
 * @author Afton
 * @time 2024/6/11
 * @auditor
 */
public class FileSortTool {

    /**
     * 文件排序
     *
     * @param fileInfoList 文件信息列表
     * @param sortType     排序类型
     */
    public static void sort(ArrayList<FileInfo> fileInfoList, int sortType) {
        switch (sortType) {
            case GetFileListTool.SortMode.TIME_FORWARD:
                timeForwardSort(fileInfoList);
                break;
            case GetFileListTool.SortMode.TIME_REVERSE:
                timeReverseSort(fileInfoList);
                break;
            case GetFileListTool.SortMode.NAME_FORWARD:
                nameForwardSort(fileInfoList);
                break;
            case GetFileListTool.SortMode.NAME_REVERSE:
                nameReverseSort(fileInfoList);
                break;
        }
    }

    /**
     * 时间正序排序
     *
     * @param fileInfoList 文件信息列表
     */
    private static void timeForwardSort(ArrayList<FileInfo> fileInfoList) {
        fileInfoList.sort((o1, o2) -> {
            long diff = o1.getLastModified() - o2.getLastModified();
            if (diff > 0) return 1;
            else if (diff == 0) return 0;
            else return -1;
        });
    }

    /**
     * 时间倒序排序
     *
     * @param fileInfoList 文件信息列表
     */
    private static void timeReverseSort(ArrayList<FileInfo> fileInfoList) {
        fileInfoList.sort((o1, o2) -> {
            long diff = o2.getLastModified() - o1.getLastModified();
            if (diff > 0) return 1;
            else if (diff == 0) return 0;
            else return -1;
        });
    }

    /**
     * 名称正序排序
     *
     * @param fileInfoList 文件信息列表
     */
    private static void nameForwardSort(ArrayList<FileInfo> fileInfoList) {
        fileInfoList.sort((o1, o2) -> {
            if (o1.isDirectory() && !o2.isDirectory()) {
                return -1;
            } else if (!o1.isDirectory() && o2.isDirectory()) {
                return 1;
            } else {
                return o1.getFileName().compareTo(o2.getFileName());
            }
        });
    }

    /**
     * 名称倒序排序
     *
     * @param fileInfoList 文件信息列表
     */
    private static void nameReverseSort(ArrayList<FileInfo> fileInfoList) {
        fileInfoList.sort((o1, o2) -> {
            if (o1.isDirectory() && !o2.isDirectory()) {
                return 1;
            } else if (!o1.isDirectory() && o2.isDirectory()) {
                return -1;
            } else {
                return o2.getFileName().compareTo(o1.getFileName());
            }
        });
    }
}