package com.next.view.file.manage;

import com.next.module.file2.File2;
import com.next.module.file2.tool.FileListFactory;
import com.next.module.file2.tool.FileLoadException;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileSortTool;
import com.next.view.file.tool.FileTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * ClassName:获取文件列表工具类
 *
 * @author Afton
 * @time 2024/6/10
 * @auditor
 */
public class GetFileListTool {

    //排序模式
    public static final class SortMode {
        //按名称正序
        public static final int NAME_FORWARD = 0;
        //按名称倒序
        public static final int NAME_REVERSE = 1;
        //按时间正序
        public static final int TIME_FORWARD = 2;
        //按时间倒序
        public static final int TIME_REVERSE = 3;
    }

    //显示模式
    public static class ShowMode {
        //显示所有
        public static final int SHOW_ALL = 0;
        //显示文件夹
        public static final int SHOW_FOLDER = 1;
        //显示文件
        public static final int SHOW_FILE = 2;
    }

    //文件列表工厂对象
    private FileListFactory factory;

    //父文件对象
    private File2 parentFile;

    //当前路径
    private String nowPath;

    //文件信息对象列表
    private ArrayList<FileInfo> fileInfoList;

    public GetFileListTool() {
        this.factory = new FileListFactory();
        this.fileInfoList = new ArrayList<>();
    }

    /**
     * 获取文件信息对象列表
     *
     * @param path           路径
     * @param isShowHideFile 是否显示隐藏文件
     * @param sortMode       排序模式
     * @param showMode       显示模式
     * @return 文件信息对象列表
     * @throws FileLoadException 文件加载异常
     */
    public ArrayList<FileInfo> getFileInfoList(String path, boolean isShowHideFile, int sortMode, int showMode) throws FileLoadException {
        ArrayList<FileInfo> list = new ArrayList<>();
        if (path.equals(this.nowPath) && !this.fileInfoList.isEmpty()) {
            list.addAll(this.fileInfoList);
        } else {
            //初始化数据
            this.initData();
            this.nowPath = path;

            FileListFactory.FileListInfo fileListInfo = this.factory.getFileList(path);
            this.parentFile = fileListInfo.getParentFile();
            File2[] file2s = fileListInfo.getChildFileList();
            this.fileInfoList = this.file2ListToFileInfoList(file2s);
            list.addAll(this.fileInfoList);
        }

        if (!list.isEmpty()) {
            //筛选隐藏文件和显示模式
            list = (ArrayList<FileInfo>) list.stream().filter(fileInfo -> this.isFilter(fileInfo, isShowHideFile, showMode)).collect(Collectors.toList());
            //排序
            FileSortTool.sort(list, sortMode);
        }

        return list;
    }

    /**
     * 文件2数组转文件信息对象列表
     *
     * @param file2List 文件2对象列表
     * @return 文件信息对象列表
     */
    private ArrayList<FileInfo> file2ListToFileInfoList(File2[] file2List) {
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();

        for (File2 file2 : file2List) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(file2.getName());
            fileInfo.setFileSize(FileTool.formetFileSize(file2.length()));
            fileInfo.setDirectory(file2.isDirectory());
            fileInfo.setLastModified(file2.lastModified());
            fileInfo.setLastModifiedText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(file2.lastModified()));
            fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
            fileInfo.setFileType(file2.getType());
            fileInfo.setFile2(file2);
        }

        return fileInfoList;
    }

    /**
     * 筛选文件信息对象
     *
     * @param fileInfo       文件信息对象
     * @param isShowHideFile 是否显示隐藏文件
     * @param showMode       显示模式
     * @return 是否筛选
     */
    private boolean isFilter(FileInfo fileInfo, boolean isShowHideFile, int showMode) {
        if (!isShowHideFile && fileInfo.getFileName().startsWith(".")) {
            return false;
        }

        if (showMode == ShowMode.SHOW_FILE && fileInfo.isDirectory()) {
            return false;
        }

        if (showMode == ShowMode.SHOW_FOLDER && !fileInfo.isDirectory()) {
            return false;
        }

        return true;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        this.fileInfoList.clear();
        this.parentFile = null;
        this.nowPath = null;
    }
}