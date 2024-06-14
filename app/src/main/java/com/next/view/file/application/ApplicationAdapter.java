package com.next.view.file.application;

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
import com.next.view.file.info.AppInfo;
import com.next.view.file.info.FileInfo;

import java.util.ArrayList;

/**
 * ClassName:应用适配器类
 *
 * @author Afton
 * @time 2023/9/30
 * @auditor
 */
public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> implements Filterable {

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

    public ApplicationAdapter(Context context) {
        this.context = context;
        this.factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
    }

    @Override
    public void onBindViewHolder(ApplicationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
    public ApplicationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.next_item_file, parent, false);
        ApplicationAdapter.ViewHolder holder = new ApplicationAdapter.ViewHolder(view);

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
                    filterResults.values = ApplicationAdapter.this.fileInfoList;
                } else {
                    ArrayList<FileInfo> filteredList = new ArrayList<>();
                    for (FileInfo fileInfo : ApplicationAdapter.this.fileInfoList) {
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
                ApplicationAdapter.this.filterFileInfoList = (ArrayList<FileInfo>) results.values;
                ApplicationAdapter.this.notifyDataSetChanged();

                if (ApplicationAdapter.this.onSearchListener != null) {
                    ApplicationAdapter.this.onSearchListener.onComplete();
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
        this.fileInfoList.clear();
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
     * @param fileClickListener 文件点击监听接口
     */
    public void addFileClickListener(OnFileClickListener fileClickListener) {
        this.onFileClickListeners.add(fileClickListener);
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
    private void setFileBaseInfo(ApplicationAdapter.ViewHolder holder, FileInfo fileInfo) {
        AppInfo appInfo = (AppInfo) fileInfo;
        //设置应用包名
        holder.fileTimeView.setText(appInfo.getPackageName());
        //设置应用名称
        holder.fileNameView.setText(appInfo.getAppName());

        Glide.with(this.context)
                .load(appInfo)
                .transition(DrawableTransitionOptions.withCrossFade(this.factory))
                .placeholder(R.drawable.next_ic_file_apk)
                .error(R.drawable.next_ic_file_apk)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(148, 148)
                .into(holder.fileIconView);
    }

    /**
     * 设置文件选中状态
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileSelectState(ApplicationAdapter.ViewHolder holder, FileInfo fileInfo) {
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
    private void setFileClick(ApplicationAdapter.ViewHolder holder, FileInfo fileInfo) {
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