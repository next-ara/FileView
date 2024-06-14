package com.next.view.file.manage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.next.view.file.OnFileClickListener;
import com.next.view.file.OnSearchListener;
import com.next.view.file.R;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.FileTypeTool;

import java.util.ArrayList;

/**
 * ClassName:文件管理适配器类
 *
 * @author Afton
 * @time 2023/7/8
 * @auditor
 */
public class FileManageAdapter extends RecyclerView.Adapter<FileManageAdapter.ViewHolder> implements Filterable {

    //文件信息对象列表
    private ArrayList<FileInfo> fileInfoList = new ArrayList<>();

    //过滤文件信息对象列表
    private ArrayList<FileInfo> filterFileInfoList = new ArrayList<>();

    //文件点击监听接口
    private ArrayList<OnFileClickListener> onFileClickListeners = new ArrayList<>();

    //搜索完成监听接口
    private OnSearchListener onSearchListener;

    //上下文
    private Context context;

    //图片交叉淡入过渡动画工厂对象
    private DrawableCrossFadeFactory factory;

    static class ViewHolder extends RecyclerView.ViewHolder {

        //文件图标图片控件
        private ImageView fileIconView;
        //文本名称文本控件
        private TextView fileNameView;
        //文本时间文本控件
        private TextView fileTimeView;
        //文件选中控件
        private CheckBox fileCheckBox;

        public ViewHolder(View view) {
            super(view);
            this.fileIconView = view.findViewById(R.id.image_file);
            this.fileNameView = view.findViewById(R.id.tv_file_name);
            this.fileTimeView = view.findViewById(R.id.tv_file_time);
            this.fileCheckBox = view.findViewById(R.id.check_box_file);
            this.fileCheckBox.setClickable(false);
        }
    }

    public FileManageAdapter(Context context) {
        this.context = context;
        this.factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //获取当前下标的文件信息对象
        FileInfo fileInfo = this.filterFileInfoList.get(position);

        this.setFileBaseInfo(holder, fileInfo);
        this.setFileSelectState(holder, fileInfo);
        this.setFileClick(holder, fileInfo);
    }

    @Override
    public int getItemCount() {
        return this.filterFileInfoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.next_item_file, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                String filter = constraint.toString();
                if (TextUtils.isEmpty(filter)) {
                    //没有过滤的内容，则使用源数据
                    filterResults.values = FileManageAdapter.this.fileInfoList;
                } else {
                    ArrayList<FileInfo> filteredList = new ArrayList<>();
                    for (FileInfo fileInfo : FileManageAdapter.this.fileInfoList) {
                        //这里根据需求，添加匹配规则
                        if (fileInfo.getFileName().toLowerCase().contains(filter.toLowerCase())) {
                            filteredList.add(fileInfo);
                        }
                    }
                    filterResults.values = filteredList;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                FileManageAdapter.this.filterFileInfoList = (ArrayList<FileInfo>) results.values;
                FileManageAdapter.this.notifyDataSetChanged();

                if (FileManageAdapter.this.onSearchListener != null) {
                    FileManageAdapter.this.onSearchListener.onComplete();
                }
            }
        };
    }

    /**
     * 通知数据更新
     *
     * @param fileInfo 文件信息对象
     */
    public void notifyItemChanged(FileInfo fileInfo) {
        int index = this.filterFileInfoList.indexOf(fileInfo);
        if (index >= 0) {
            this.notifyItemChanged(index);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        this.filterFileInfoList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 设置文件点击监听
     *
     * @param onFileClickListeners 文件点击监听接口
     */
    public void setOnFileClickListeners(OnFileClickListener onFileClickListeners) {
        this.onFileClickListeners.clear();
        this.onFileClickListeners.add(onFileClickListeners);
    }

    /**
     * 添加文件点击监听
     *
     * @param onFileClickListener 文件点击监听接口
     */
    public void addFileClickListener(OnFileClickListener onFileClickListener) {
        this.onFileClickListeners.add(onFileClickListener);
    }

    /**
     * 获取选中的文件信息对象列表
     *
     * @return 选中的文件信息对象列表
     */
    public ArrayList<FileInfo> getSelectFileInfoList() {
        ArrayList<FileInfo> selectFileInfoList = new ArrayList<>();

        for (FileInfo fileInfo : this.filterFileInfoList) {
            if (fileInfo.getSelectType() == FileInfo.SelectType.SELECT_TYPE_SELECT) {
                selectFileInfoList.add(fileInfo);
            }
        }

        return selectFileInfoList;
    }

    /**
     * 设置文件基础信息
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileBaseInfo(ViewHolder holder, FileInfo fileInfo) {
        //设置文件最后修改时间
        holder.fileTimeView.setText(fileInfo.getLastModifiedText());
        //设置文件名称
        holder.fileNameView.setText(fileInfo.getFileName());
        //获取文件类型
        int fileType = FileTypeTool.getFileType(fileInfo);

        //设置文件图标
        if (FileTypeTool.FileType.FILE_TYPE_IMAGE == fileType || FileTypeTool.FileType.FILE_TYPE_VIDEO == fileType) {
            Glide.with(this.context)
                    .load(fileInfo.getFile2().getUri())
                    .transition(DrawableTransitionOptions.withCrossFade(this.factory))
                    .placeholder(FileTypeTool.getFileIcon(fileType))
                    .error(FileTypeTool.getFileIcon(fileType))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(148, 148)
                    .into(holder.fileIconView);
        } else {
            Glide.with(this.context)
                    .load(FileTypeTool.getFileIcon(fileType))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(148, 148)
                    .into(holder.fileIconView);
        }
    }

    /**
     * 设置文件选中状态
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileSelectState(ViewHolder holder, FileInfo fileInfo) {
        switch (fileInfo.getSelectType()) {
            case FileInfo.SelectType.SELECT_TYPE_NONE:
                holder.fileCheckBox.setVisibility(View.GONE);
                holder.fileCheckBox.setChecked(false);
                break;
            case FileInfo.SelectType.SELECT_TYPE_SELECT:
                holder.fileCheckBox.setVisibility(View.VISIBLE);
                holder.fileCheckBox.setChecked(true);
                break;
            case FileInfo.SelectType.SELECT_TYPE_UNSELECT:
                holder.fileCheckBox.setVisibility(View.VISIBLE);
                holder.fileCheckBox.setChecked(false);
                break;
        }
    }

    /**
     * 设置文件点击事件
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileClick(ViewHolder holder, FileInfo fileInfo) {
        if (this.onFileClickListeners != null) {
            holder.itemView.setOnClickListener(view -> {
                for (OnFileClickListener onFileClickListener : this.onFileClickListeners) {
                    onFileClickListener.onClick(fileInfo);
                }
            });

            holder.itemView.setOnLongClickListener(view -> {
                for (OnFileClickListener onFileClickListener : this.onFileClickListeners) {
                    onFileClickListener.onLongClick(fileInfo);
                }
                return true;
            });
        }
    }

    public ArrayList<FileInfo> getFileInfoList() {
        return filterFileInfoList;
    }

    public void setFileInfoList(ArrayList<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
        this.filterFileInfoList.clear();
        this.filterFileInfoList.addAll(fileInfoList);
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }
}