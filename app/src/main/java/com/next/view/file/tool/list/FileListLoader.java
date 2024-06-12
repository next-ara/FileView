package com.next.view.file.tool.list;

/**
 * ClassName:文件列表加载器类
 *
 * @author Afton
 * @time 2024/6/7
 * @auditor
 */
abstract public class FileListLoader {

    /**
     * 获取文件列表
     *
     * @param path 路径
     * @return 文件列表信息对象
     * @throws FileLoadException 文件加载异常
     */
    abstract public FileListFactory.FileListInfo getFileList(String path) throws FileLoadException;

    /**
     * 判断是否执行
     *
     * @param path 路径
     * @return 是否执行
     */
    abstract public boolean isExecute(String path);
}