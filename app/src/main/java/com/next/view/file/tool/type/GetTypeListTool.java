package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:获取类型列表工具类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class GetTypeListTool {

    //选择模式
    public final class SelectMode {
        //关闭选中模式
        public static final int SELECT_CLOSE = -1;
        //仅文件支持选中
        public static final int SELECT_FILE = 2;
    }

    //类型列表工厂对象
    private TypeListFactory factory;

    //当前文件类型
    private String nowFileType;

    public GetTypeListTool() {
        this.factory = new TypeListFactory();
    }

    /**
     * 获取文件信息对象列表
     *
     * @param fileType   文件类型
     * @param selectMode 选择模式
     * @return 文件信息对象列表
     */
    public ArrayList<FileInfo> getFileInfoList(String fileType, int selectMode) {
        this.nowFileType = fileType;
        ArrayList<FileInfo> list = this.factory.getTypeList(fileType);

        if (!list.isEmpty()) {
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

    public String getNowFileType() {
        return nowFileType;
    }
}