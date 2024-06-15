package com.next.view.file.type.tool;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.next.module.file2.File2;
import com.next.module.file2.File2Creator;
import com.next.module.file2.FileConfig;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileTool;

import java.io.File;
import java.text.SimpleDateFormat;
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

    /**
     * 获取类型列表
     *
     * @param fileExtensionList 文件后缀列表
     * @return 类型列表
     */
    protected ArrayList<FileInfo> getTypeList(String[] fileExtensionList) {
        ArrayList<FileInfo> typeList = new ArrayList<>();
        ContentResolver contentResolver = FileConfig.getApplication().getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA,
        };

        StringBuilder selection = new StringBuilder(MediaStore.Files.FileColumns.DATA);
        selection.append(" LIKE '%");
        selection.append(fileExtensionList[0]);
        selection.append("'");

        if (fileExtensionList.length > 1) {
            for (String suffix : fileExtensionList) {
                selection.append(" or ");
                selection.append(MediaStore.Files.FileColumns.DATA);
                selection.append(" LIKE '%");
                selection.append(suffix);
                selection.append("'");
            }
        }

        Cursor c = contentResolver.query(uri, projection, selection.toString(), null, MediaStore.Files.FileColumns.DATE_TAKEN + " DESC");
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    String filePath = c.getString(0);
                    File2 file2 = File2Creator.fromFile(new File(filePath));

                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(file2.getName());
                    fileInfo.setFileSize(FileTool.formetFileSize(file2.length()));
                    fileInfo.setDirectory(file2.isDirectory());
                    fileInfo.setLastModified(file2.lastModified());
                    fileInfo.setLastModifiedText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(fileInfo.getLastModified()));
                    fileInfo.setFileType(file2.getType());
                    fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
                    fileInfo.setFile2(file2);
                    typeList.add(fileInfo);
                }
            } finally {
                c.close();
            }
        }

        return typeList;
    }
}