package com.next.view.file.path;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.next.module.file2.tool.FilePathTool;
import com.next.view.file.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * ClassName:文件路径列表适配器类
 *
 * @author Afton
 * @time 2023/7/18
 * @auditor
 */
public class FilePathListAdapter extends RecyclerView.Adapter<FilePathListAdapter.ViewHolder> {

    //路径按钮点击监听接口
    public interface FilePathClickListener {

        /**
         * 路径按钮点击事件
         *
         * @param path 路径
         */
        void onClick(String path);

        /**
         * 路径按钮长按事件
         *
         * @param path 路径
         */
        void onLongClick(String path);
    }

    //文件路径列表
    private ArrayList<String> filePathList = new ArrayList<>();

    //当前文件路径
    private String nowPath = FilePathTool.ROOT_PATH;

    //文件路径点击监听接口
    private FilePathClickListener filePathClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        //路径文本控件
        private TextView pathView;

        public ViewHolder(View view) {
            super(view);
            this.pathView = view.findViewById(R.id.item_path);
        }
    }

    /**
     * 设置当前路径
     *
     * @param nowPath 文件路径
     */
    public void setNowPath(String nowPath) {
        this.nowPath = nowPath;
        this.getPathList(nowPath);
        this.notifyDataSetChanged();
    }

    /**
     * 获取当前长度
     *
     * @return 长度
     */
    public int size() {
        return this.filePathList.size();
    }

    /**
     * 解析文件路径列表
     *
     * @param path 文件路径
     */
    private void getPathList(String path) {
        this.filePathList.clear();

        for (String nowPath = path; !nowPath.equals(FilePathTool.ROOT_PATH); nowPath = FilePathTool.getParentPath(nowPath)) {
            this.filePathList.add(new File(nowPath).getName());
        }

        this.filePathList.add("根目录");
        Collections.reverse(this.filePathList);
    }

    /**
     * 获取文件路径
     *
     * @param index 下标
     * @return 路径
     */
    private String getPath(int index) {
        File file = new File(FilePathTool.ROOT_PATH);

        for (int i = 1; i <= index && index < this.filePathList.size(); i++) {
            if (!file.exists()) {
                file = file.getParentFile();
                break;
            }

            String item = this.filePathList.get(i);
            file = new File(file.getPath(), item);
        }

        return file.getPath();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String pathItem = filePathList.get(position);

        holder.pathView.setText(pathItem);

        if (this.filePathClickListener != null) {
            holder.itemView.setOnClickListener(view -> this.filePathClickListener.onClick(this.getPath(position)));

            holder.itemView.setOnLongClickListener(view -> {
                this.filePathClickListener.onLongClick(this.getPath(position));
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.filePathList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.next_item_path, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    public String getNowPath() {
        return nowPath;
    }

    public FilePathClickListener getFilePathClickListener() {
        return filePathClickListener;
    }

    public void setFilePathClickListener(FilePathClickListener filePathClickListener) {
        this.filePathClickListener = filePathClickListener;
    }
}