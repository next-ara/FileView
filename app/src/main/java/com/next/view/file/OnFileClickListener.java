package com.next.view.file;

import com.next.view.file.info.FileInfo;

/**
 * ClassName:文件点击监听接口
 *
 * @author Afton
 * @time 2024/6/14
 * @auditor
 */
public interface OnFileClickListener {

    /**
     * 点击事件
     *
     * @param fileInfo 文件信息对象
     */
    void onClick(FileInfo fileInfo);

    /**
     * 长按事件
     *
     * @param fileInfo 文件信息对象
     */
    void onLongClick(FileInfo fileInfo);
}