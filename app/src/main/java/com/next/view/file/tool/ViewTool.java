package com.next.view.file.tool;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * ClassName:视图工具类
 *
 * @author Afton
 * @time 2024/8/16
 * @auditor
 */
public class ViewTool {

    /**
     * 设置视图在屏幕中心
     *
     * @param targetView 目标视图
     */
    public static void setViewOnScreenCenter(View targetView) {
        ViewGroup parentView = (ViewGroup) targetView.getParent();
        if (parentView == null) {
            return;
        }

        ViewGroup.LayoutParams params = targetView.getLayoutParams();
        if (!(params instanceof RelativeLayout.LayoutParams)) {
            return;
        }

        WindowManager wm = (WindowManager) parentView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        //获取屏幕中心的Y坐标
        int screenCenterY = metrics.heightPixels / 2;
        //获取目标视图的高度
        int targetViewHeight = targetView.getHeight();
        //获取父控件中心点的Y坐标
        int parentCenterY = parentView.getHeight() / 2;
        //获取父控件在屏幕上的Y坐标
        int location[] = new int[2];
        parentView.getLocationOnScreen(location);
        int parentLoadingViewY = location[1];

        //计算目标视图基于父控件中处于屏幕中心的Y坐标
        int targetViewOnScreenY = screenCenterY - targetViewHeight / 2 - parentLoadingViewY;

        //如果目标视图基于父控件中处于屏幕中心的Y坐标小于0，则将目标视图Y坐标设置为父控件中心点的Y坐标
        if (targetViewOnScreenY < 0) {
            targetViewOnScreenY = parentCenterY - targetViewHeight / 2;
        }

        //设置目标视图外间距
        ((RelativeLayout.LayoutParams) params).setMargins(0, targetViewOnScreenY, 0, 0);
        targetView.setLayoutParams(params);
    }
}