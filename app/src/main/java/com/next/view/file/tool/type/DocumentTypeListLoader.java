package com.next.view.file.tool.type;

import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:文档类型列表加载器类
 *
 * @author Afton
 * @time 2024/6/13
 * @auditor
 */
public class DocumentTypeListLoader extends TypeListLoader {

    //文档后缀
    public static final String[] DOCUMENT_EXTENSION = {
            ".pdf",
            ".doc",
            ".docx",
            ".xls",
            ".xlsx",
            ".odt",
            ".odp",
            ".ods",
            ".txt",
            ".rtf",
            ".html",
            ".htm",
            ".xml"
    };

    @Override
    public ArrayList<FileInfo> getTypeList() {
        return this.getTypeList(DOCUMENT_EXTENSION);
    }

    @Override
    public boolean isExecute(String fileType) {
        return TypeListFactory.Type.TYPE_DOCUMENT.equals(fileType);
    }
}