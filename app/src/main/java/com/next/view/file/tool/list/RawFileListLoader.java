package com.next.view.file.tool.list;

import android.os.Build;

import com.next.module.file2.File2;
import com.next.module.file2.File2Creator;
import com.next.module.file2.RawFile;
import com.next.module.file2.tool.FilePathTool;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileTool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * ClassName:RawFile加载器类
 *
 * @author Afton
 * @time 2024/6/8
 * @auditor
 */
public class RawFileListLoader extends FileListLoader {

    @Override
    public FileListFactory.FileListInfo getFileList(String path) throws FileLoadException {
        //检查访问权限
        if (!this.checkAccessPermission(path)) {
            throw new FileLoadException(FileLoadException.ErrorCode.ERROR_CODE_NO_PERMISSION);
        }

        RawFile rawFile = File2Creator.fromFile(new File(path));
        //检查文件是否存在且是文件夹
        if (!rawFile.exists() || !rawFile.isDirectory()) {
            throw new FileLoadException(FileLoadException.ErrorCode.ERROR_CODE_FILE_NOT_EXIST);
        }

        return new FileListFactory.FileListInfo(rawFile, this.file2ListToFileInfoList(rawFile.listFiles()));
    }

    @Override
    public boolean isExecute(String path) {
        return true;
    }

    /**
     * 检查访问权限
     *
     * @param path 路径
     * @return 是否有访问权限
     */
    private boolean checkAccessPermission(String path) {
        if (FilePathTool.isAppDataPath(path)) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return !FilePathTool.isDataPath(path) && !FilePathTool.isObbPath(path) && !FilePathTool.isUnderDataPath(path) && !FilePathTool.isUnderObbPath(path);
        }

        return true;
    }

    /**
     * 文件2数组转文件信息对象列表
     *
     * @param file2List 文件2对象列表
     * @return 文件信息对象列表
     */
    private ArrayList<FileInfo> file2ListToFileInfoList(File2[] file2List) {
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();

        for (File2 file2 : file2List) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(file2.getName());
            fileInfo.setFileSize(FileTool.formetFileSize(file2.length()));
            fileInfo.setDirectory(file2.isDirectory());
            fileInfo.setLastModified(file2.lastModified());
            fileInfo.setLastModifiedText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(file2.lastModified()));
            fileInfo.setFileType(file2.getType());
            fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_NONE);
            fileInfo.setFile2(file2);
            fileInfoList.add(fileInfo);
        }

        return fileInfoList;
    }
}