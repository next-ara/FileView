package com.next.view.file.application.tool;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.next.module.file2.File2Creator;
import com.next.module.file2.FileConfig;
import com.next.module.file2.RawFile;
import com.next.view.file.info.AppInfo;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileTool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:获取应用列表工具类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class GetAppListTool {

    //选择模式
    public final class SelectMode {
        //关闭选中模式
        public static final int SELECT_CLOSE = -1;
        //仅文件支持选中
        public static final int SELECT_FILE = 2;
    }

    //显示模式
    public final class ShowMode {
        //显示所有应用
        public static final int SHOW_ALL = 0;
        //显示用户应用
        public static final int SHOW_USER = 1;
        //显示系统应用
        public static final int SHOW_SYSTEM = 2;
    }

    /**
     * 获取文件信息对象列表
     *
     * @param selectMode 选择模式
     * @param showMode   显示模式
     * @return 文件信息对象列表
     */
    public ArrayList<FileInfo> getFileInfoList(int selectMode, int showMode) {
        ArrayList<FileInfo> list = new ArrayList<>();

        PackageManager pm = FileConfig.getApplication().getPackageManager();
        List<PackageInfo> appList = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);

        for (PackageInfo packageInfo : appList) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户应用
                switch (showMode) {
                    case ShowMode.SHOW_ALL:
                    case ShowMode.SHOW_USER:
                        list.add(this.creatAppInfo(pm, packageInfo));
                        break;
                }
            } else {
                //系统应用
                switch (showMode) {
                    case ShowMode.SHOW_ALL:
                    case ShowMode.SHOW_SYSTEM:
                        list.add(this.creatAppInfo(pm, packageInfo));
                        break;
                }
            }
        }

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
            case GetAppListTool.SelectMode.SELECT_CLOSE ->
                    fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
            case GetAppListTool.SelectMode.SELECT_FILE ->
                    fileInfo.setSelectType(fileInfo.isDirectory() ? FileInfo.SelectType.SELECT_TYPE_NONE : FileInfo.SelectType.SELECT_TYPE_UNSELECT);
        }
    }

    /**
     * 创建应用信息对象
     *
     * @param packageManager 包管理器对象
     * @param packageInfo    包信息对象
     * @return 应用信息对象
     */
    private AppInfo creatAppInfo(PackageManager packageManager, PackageInfo packageInfo) {
        AppInfo appInfo = new AppInfo();
        RawFile rawFile = File2Creator.fromFile(new File(packageInfo.applicationInfo.sourceDir));

        appInfo.setFileName(rawFile.getName());
        appInfo.setFileSize(FileTool.formetFileSize(rawFile.length()));
        appInfo.setDirectory(rawFile.isDirectory());
        appInfo.setLastModified(rawFile.lastModified());
        appInfo.setLastModifiedText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(appInfo.getLastModified()));
        appInfo.setFileType(rawFile.getType());
        appInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
        appInfo.setFile2(rawFile);
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());

        return appInfo;
    }
}