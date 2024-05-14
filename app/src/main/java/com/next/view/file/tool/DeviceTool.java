package com.next.view.file.tool;

import android.content.Context;
import android.content.res.Configuration;

/**
 * ClassName:设备信息获取工具类
 *
 * @author Afton
 * @time 2023/10/27
 * @auditor
 */
public class DeviceTool {

    /**
     * 获取屏幕宽度
     *
     * @param context 上下文
     * @return 宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕长度
     *
     * @param context 上下文
     * @return 长度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 当前是否是横屏
     *
     * @param context 上下文
     * @return true/false
     */
    public static boolean isLandscapeScreen(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 是否是短屏（-300px < 长度-宽度 < 300px）
     *
     * @param context 上下文
     * @return true/false
     */
    public static boolean isShortScreen(Context context) {
        int width = getScreenWidth(context);
        int height = getScreenHeight(context);
        int differ = width - height;
        if (differ < 0) {
            differ = -differ;
        }

        return differ < 300;
    }
}