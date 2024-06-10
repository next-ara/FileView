package com.next.view.file.info;

import com.next.module.file2.File2;

/**
 * ClassName:文件信息类
 *
 * @author Afton
 * @time 2024/6/10
 * @auditor
 */
public class FileInfo {

    //选中类型
    public static class SelectType {
        //默认
        public static final int SELECT_TYPE_NONE = 0;
        //选中
        public static final int SELECT_TYPE_SELECT = 1;
        //未选中
        public static final int SELECT_TYPE_UNSELECT = 2;
    }

    //文件名
    private String fileName;

    //最后修改时间
    private long lastModified;

    //最后修改时间文本
    private String lastModifiedText;

    //文件大小
    private String fileSize;

    //文件类型
    private String fileType;

    //是否是文件夹
    private boolean isDirectory;

    //选中类型
    private int selectType;

    //文件对象
    private File2 file2;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedText() {
        return lastModifiedText;
    }

    public void setLastModifiedText(String lastModifiedText) {
        this.lastModifiedText = lastModifiedText;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public int getSelectType() {
        return selectType;
    }

    public void setSelectType(int selectType) {
        this.selectType = selectType;
    }

    public File2 getFile2() {
        return file2;
    }

    public void setFile2(File2 file2) {
        this.file2 = file2;
    }
}