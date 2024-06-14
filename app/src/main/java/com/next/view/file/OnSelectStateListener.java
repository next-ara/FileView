package com.next.view.file;

/**
 * ClassName:选择状态监听接口
 *
 * @author Afton
 * @time 2024/6/14
 * @auditor
 */
public interface OnSelectStateListener {

    /**
     * 选择状态改变
     *
     * @param isSelected 选择状态
     */
    void onSelectStateChanged(boolean isSelected);
}