package com.next.view.file.manage.tool;

import android.content.ContentResolver;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import com.next.module.file2.Contracts;
import com.next.module.file2.File2;
import com.next.module.file2.File2Creator;
import com.next.module.file2.FileConfig;
import com.next.module.file2.TreeDocumentFile;
import com.next.module.file2.tool.FilePathTool;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:DocumentFile加载器类
 *
 * @author Afton
 * @time 2024/6/8
 * @auditor
 */
public class DocumentFileListLoader extends FileListLoader {

    @Override
    public FileListFactory.FileListInfo getFileList(String path) throws FileLoadException {
        //检查访问权限
        if (!this.checkAccessPermission(path)) {
            throw new FileLoadException(FileLoadException.ErrorCode.ERROR_CODE_NO_PERMISSION);
        }

        TreeDocumentFile treeDocumentFile = File2Creator.fromUri(FilePathTool.dataPathToUri(path));
        //检查文件是否存在且是文件夹
        if (!treeDocumentFile.exists() || !treeDocumentFile.isDirectory()) {
            throw new FileLoadException(FileLoadException.ErrorCode.ERROR_CODE_FILE_NOT_EXIST);
        }

        return new FileListFactory.FileListInfo(treeDocumentFile, this.getFileInfoList(treeDocumentFile));
    }

    @Override
    public File2 getFile2(String path) throws FileLoadException {
        //检查访问权限
        if (!this.checkAccessPermission(path)) {
            throw new FileLoadException(FileLoadException.ErrorCode.ERROR_CODE_NO_PERMISSION);
        }

        return File2Creator.fromUri(FilePathTool.dataPathToUri(path));
    }

    @Override
    public boolean isExecute(String path) {
        if (FilePathTool.isAppDataPath(path)) {
            return false;
        }

        return FilePathTool.isDataPath(path) || FilePathTool.isObbPath(path) || FilePathTool.isUnderDataPath(path) || FilePathTool.isUnderObbPath(path);
    }

    /**
     * 检查访问权限
     *
     * @param path 路径
     * @return 是否有访问权限
     */
    private boolean checkAccessPermission(String path) {
        List<UriPermission> uriPermissions = FileConfig.getApplication().getContentResolver().getPersistedUriPermissions();
        String uriPath = FilePathTool.dataPathToUri(path).getPath();
        for (UriPermission uriPermission : uriPermissions) {
            String itemPath = uriPermission.getUri().getPath();
            if (uriPath != null && itemPath != null && (uriPath + "/").contains(itemPath + "/")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取文件信息列表
     *
     * @param treeDocumentFile 文件对象
     * @return 文件信息列表
     */
    private ArrayList<FileInfo> getFileInfoList(TreeDocumentFile treeDocumentFile) {
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();

        Uri self = treeDocumentFile.getUri();
        final ContentResolver resolver = FileConfig.getApplication().getContentResolver();
        final Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(self, DocumentsContract.getDocumentId(self));

        Cursor c = null;
        String[] projection = new String[]{
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
        };

        try {
            c = resolver.query(childrenUri, projection, null, null, null);
            if (null != c) {
                while (c.moveToNext()) {
                    final String documentId = c.getString(0);
                    final Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(self, documentId);
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
                    fileInfo.setFile2(new TreeDocumentFile(treeDocumentFile, documentUri));
                    fileInfoList.add(fileInfo);
                }
            }
        } catch (Exception e) {
        } finally {
            Contracts.closeQuietly(c);
        }

        return fileInfoList;
    }
}