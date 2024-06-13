package com.next.view.file.type.tool;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:安装包类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class InstallTypeListLoader extends TypeListLoader {

    //安装包类型
    public static final String[] INSTALL_EXTENSION = {
            ".apk"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(INSTALL_EXTENSION);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_INSTALL.equals(fileType);
    }
}