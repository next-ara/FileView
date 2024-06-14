package com.next.view.file;

import com.next.view.file.manage.tool.FileLoadException;

/**
 * ClassName:应用加载监听接口
 *
 * @author Afton
 * @time 2024/6/14
 * @auditor
 */
public interface OnFileLoadListener {

    /**
     * 文件加载完成
     */
    void onLoadComplete();

    /**
     * 文件加载失败
     *
     * @param e 错误信息
     */
    void onLoadError(FileLoadException e);
}