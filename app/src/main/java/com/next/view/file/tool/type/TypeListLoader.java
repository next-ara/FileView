package com.next.view.file.tool.type;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.next.module.file2.FileConfig;
import com.next.module.file2.MediaFile;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileTool;

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
     * @param mimeTypeList MIME类型列表
     * @return 类型列表
     */
    protected ArrayList<FileInfo> getTypeList(String[] mimeTypeList) {
        ArrayList<FileInfo> typeList = new ArrayList<>();
        ContentResolver contentResolver = FileConfig.getApplication().getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = new String[]{
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATE_MODIFIED
        };
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        Cursor c = contentResolver.query(uri, projection, selection, mimeTypeList, MediaStore.Files.FileColumns.DATE_TAKEN + " DESC");
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    long imageId = c.getLong(0);
                    Uri imageUri = ContentUris.withAppendedId(uri, imageId);
                    final String name = c.getString(1);
                    final long size = c.getLong(2);
                    final String mimeType = c.getString(3);
                    final long lastModified = c.getLong(4);

                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(name);
                    fileInfo.setFileSize(FileTool.formetFileSize(size));
                    fileInfo.setDirectory(DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType));
                    fileInfo.setLastModified(lastModified);
                    fileInfo.setLastModifiedText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(fileInfo.getLastModified()));
                    fileInfo.setFileType(DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType) ? null : mimeType);
                    fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
                    fileInfo.setFile2(new MediaFile(imageUri));
                    typeList.add(fileInfo);
                }
            } finally {
                c.close();
            }
        }

        return typeList;
    }
}