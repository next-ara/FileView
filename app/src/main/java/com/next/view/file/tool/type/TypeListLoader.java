package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
abstract public class TypeListLoader {

    /**
     * 获取类型列表
     *
     * @return 类型列表
     */
    abstract public ArrayList<FileInfo> getTypeList();

    /**
     * 判断是否执行
     *
     * @param fileType 文件类型
     * @return 是否执行
     */
    abstract public boolean isExecute(String fileType);
}