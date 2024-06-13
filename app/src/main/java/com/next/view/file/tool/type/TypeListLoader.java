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
     * @param fileExtensionList 文件后缀列表
     * @return 类型列表
     */
    protected ArrayList<FileInfo> getTypeList(String[] fileExtensionList) {
        ArrayList<FileInfo> typeList = new ArrayList<>();
        ContentResolver contentResolver = FileConfig.getApplication().getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATE_MODIFIED
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
                    long imageId = c.getLong(0);
                    Uri mediaUri = ContentUris.withAppendedId(uri, imageId);
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
                    fileInfo.setFile2(new MediaFile(mediaUri));
                    typeList.add(fileInfo);
                }
            } finally {
                c.close();
            }
        }

        return typeList;
    }
}