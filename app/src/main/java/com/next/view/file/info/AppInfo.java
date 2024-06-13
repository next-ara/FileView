package com.next.view.file.info;

/**
 * ClassName:应用信息类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class AppInfo extends FileInfo {

    //应用包名
    private String packageName;

    //应用名称
    private String appName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}