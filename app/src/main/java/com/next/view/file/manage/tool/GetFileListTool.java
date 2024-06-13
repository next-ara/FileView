package com.next.view.file.manage.tool;

import com.next.module.file2.File2;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileSortTool;

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

    //选择模式
    public final class SelectMode {
        //关闭选中模式
        public static final int SELECT_CLOSE = -1;
        //仅文件支持选中
        public static final int SELECT_FILE = 2;
    }

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

    public GetFileListTool() {
        this.factory = new FileListFactory();
    }

    /**
     * 获取文件信息对象列表
     *
     * @param path           路径
     * @param isShowHideFile 是否显示隐藏文件
     * @param sortMode       排序模式
     * @param showMode       显示模式
     * @param selectMode     选择模式
     * @return 文件信息对象列表
     * @throws FileLoadException 文件加载异常
     */
    public ArrayList<FileInfo> getFileInfoList(String path, boolean isShowHideFile, int sortMode, int showMode, int selectMode) throws FileLoadException {
        ArrayList<FileInfo> list = new ArrayList<>();
        //初始化数据
        this.initData();
        this.nowPath = path;

        FileListFactory.FileListInfo fileListInfo = this.factory.getFileList(path);
        this.parentFile = fileListInfo.getParentFile();
        list.addAll(fileListInfo.getChildFileList());

        if (!list.isEmpty()) {
            //筛选隐藏文件和显示模式
            list = (ArrayList<FileInfo>) list.stream().filter(fileInfo -> this.isFilter(fileInfo, isShowHideFile, showMode)).collect(Collectors.toList());
            //排序
            FileSortTool.sort(list, sortMode);
            //设置选择模式
            this.setItemSelectMode(list, selectMode);
        }

        return list;
    }

    /**
     * 设置选择模式
     *
     * @param list       文件信息对象列表
     * @param selectMode 选择模式
     */
    public void setItemSelectMode(ArrayList<FileInfo> list, int selectMode) {
        for (FileInfo fileInfo : list) {
            //设置选择模式
            this.setSelectMode(fileInfo, selectMode);
        }
    }

    /**
     * 设置选择模式
     *
     * @param fileInfo   文件信息对象
     * @param selectMode 选择模式
     */
    private void setSelectMode(FileInfo fileInfo, int selectMode) {
        switch (selectMode) {
            case SelectMode.SELECT_CLOSE ->
                    fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
            case SelectMode.SELECT_FILE ->
                    fileInfo.setSelectType(fileInfo.isDirectory() ? FileInfo.SelectType.SELECT_TYPE_NONE : FileInfo.SelectType.SELECT_TYPE_UNSELECT);
        }
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
        this.parentFile = null;
        this.nowPath = null;
    }

    public File2 getParentFile() {
        return parentFile;
    }

    public String getNowPath() {
        return nowPath;
    }
}