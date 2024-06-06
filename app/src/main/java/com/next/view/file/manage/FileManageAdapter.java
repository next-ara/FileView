package com.next.view.file.manage;

import android.annotation.SuppressLint;
import android.content.ClipData;
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
import com.next.module.filehelper.info.FileInfo;
import com.next.view.file.FileTool;
import com.next.view.file.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ClassName:文件管理适配器类
 *
 * @author Afton
 * @time 2023/7/8
 * @auditor
 */
public class FileManageAdapter extends RecyclerView.Adapter<FileManageAdapter.ViewHolder> implements Filterable {

    public final class SelectMode {
        //关闭选中模式
        public static final int SELECT_CLOSE = -1;
        //支持所有文件选中
        public static final int SELECT_ALL = 0;
        //仅文件夹支持选中
        public static final int SELECT_FOLDER = 1;
        //仅文件支持选中
        public static final int SELECT_FILE = 2;
    }

    //文件点击监听接口
    public interface FileClickListener {

        /**
         * 文件点击事件
         *
         * @param view     控件
         * @param fileInfo 文件信息对象
         */
        void onClick(View view, FileInfo fileInfo);

        /**
         * 文件长按事件
         *
         * @param view     控件
         * @param fileInfo 文件信息对象
         */
        void onLongClick(View view, FileInfo fileInfo);
    }

    //搜索完成监听接口
    public interface OnSearchListener {

        /**
         * 搜索完成事件
         */
        void onComplete();
    }

    //文件信息对象列表
    private ArrayList<FileInfo> fileInfoList = new ArrayList<>();

    //过滤文件信息对象列表
    private ArrayList<FileInfo> filterFileInfoList = new ArrayList<>();

    //选中的文件对象Map
    private HashMap<String, FileInfo> selectFileInfoList = new HashMap<>();

    //文件点击监听接口
    private FileClickListener fileClickListener;

    //搜索完成监听接口
    private OnSearchListener onSearchListener;

    //文件图标资源IdMap
    private HashMap<String, Integer> fileIconResIdList = new HashMap<>();

    //上下文
    private Context context;

    //选中模式
    private int selectMode = SelectMode.SELECT_FILE;

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
        this.registerIcon();
        this.factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //判断当前下标是否超出文件信息对象列表长度
        if (this.filterFileInfoList.size() <= position) {
            return;
        }

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
     * 新增选中文件
     *
     * @param fileInfo 文件信息对象
     */
    public void addSelectFile(FileInfo fileInfo) {
        this.selectFileInfoList.put(fileInfo.getFilePath(), fileInfo);
        this.notifyItemChanged(this.getFileInfoObjIndex(fileInfo));
    }

    /**
     * 检测文件是否被选中
     *
     * @param fileInfo 文件信息对象
     * @return
     */
    public boolean fileIsSelect(FileInfo fileInfo) {
        return this.selectFileInfoList.containsKey(fileInfo.getFilePath());
    }

    /**
     * 移除选中文件
     *
     * @param fileInfo 文件信息对象
     */
    public void deleteSelectFile(FileInfo fileInfo) {
        if (this.fileIsSelect(fileInfo)) {
            this.selectFileInfoList.remove(fileInfo.getFilePath());
            this.notifyItemChanged(this.getFileInfoObjIndex(fileInfo));
        }
    }

    /**
     * 设置文件基础信息
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileBaseInfo(ViewHolder holder, FileInfo fileInfo) {
        //设置文件最后修改时间
        holder.fileTimeView.setText(this.getFileLastChangeTime(fileInfo));
        //设置文件名称
        holder.fileNameView.setText(fileInfo.getFileName());
        //设置文件图标
        if (fileInfo.getFileType().equals(FileInfo.FileType.TYPE_IMAGE) || fileInfo.getFileType().equals(FileInfo.FileType.TYPE_VIDEO)) {
            Glide.with(this.context)
                    .load(new File(fileInfo.getFilePath()))
                    .transition(DrawableTransitionOptions.withCrossFade(this.factory))
                    .placeholder(this.getFileIconResId(fileInfo))
                    .error(this.getFileIconResId(fileInfo))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(148, 148)
                    .into(holder.fileIconView);
        } else {
            Glide.with(this.context)
                    .load(this.getFileIconResId(fileInfo))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(148, 148)
                    .into(holder.fileIconView);
        }
    }

    /**
     * 获取文件对象下标
     *
     * @param fileInfo 文件信息对象
     * @return 下标
     */
    private int getFileInfoObjIndex(FileInfo fileInfo) {
        for (int i = 0; i < this.filterFileInfoList.size(); i++) {
            FileInfo fileInfo1 = this.filterFileInfoList.get(i);
            if (fileInfo1.equals(fileInfo)) {
                return i;
            }
        }

        return 0;
    }

    /**
     * 设置文件选中状态
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileSelectState(ViewHolder holder, FileInfo fileInfo) {
        this.initSelectState(holder);

        if (this.selectMode == SelectMode.SELECT_CLOSE) {
            holder.fileCheckBox.setVisibility(View.GONE);
        } else {
            if (this.selectMode == SelectMode.SELECT_FILE && fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                return;
            }

            if (this.selectMode == SelectMode.SELECT_FOLDER && !fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                return;
            }

            holder.fileCheckBox.setVisibility(View.VISIBLE);
            holder.fileCheckBox.setChecked(this.isSelect(fileInfo.getFilePath()));
        }
    }

    /**
     * 初始化选中状态
     *
     * @param holder
     */
    private void initSelectState(ViewHolder holder) {
        holder.fileCheckBox.setVisibility(View.GONE);
        holder.fileCheckBox.setChecked(false);
    }

    /**
     * 设置文件点击事件
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setFileClick(ViewHolder holder, FileInfo fileInfo) {
        if (this.fileClickListener != null) {
            holder.itemView.setOnClickListener(view -> this.fileClickListener.onClick(view, fileInfo));

            holder.itemView.setOnLongClickListener(view -> {
                this.fileClickListener.onLongClick(view, fileInfo);
                return true;
            });
        }

        //设置图片拖拽
        //this.setImageDragAndDrop(holder, fileInfo);
    }

    /**
     * 设置图片拖拽
     *
     * @param holder
     * @param fileInfo 文件信息对象
     */
    private void setImageDragAndDrop(ViewHolder holder, FileInfo fileInfo) {
        ImageView imageView = holder.fileIconView;

        if (this.selectMode != SelectMode.SELECT_CLOSE) {
            imageView.setOnLongClickListener(null);
            return;
        }

        imageView.setOnLongClickListener(v -> {
            if (!fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                File file = new File(fileInfo.getFilePath());
                if (file.exists()) {
                    ClipData clipData = ClipData.newRawUri("label", FileTool.fileToUri(file));
                    imageView.startDragAndDrop(clipData, new View.DragShadowBuilder(imageView), null, View.DRAG_FLAG_GLOBAL | View.DRAG_FLAG_GLOBAL_URI_READ);
                }
            }

            return true;
        });
    }

    /**
     * 验证是否被选中
     *
     * @param path 文件路径
     * @return true/false
     */
    private boolean isSelect(String path) {
        return this.selectFileInfoList.containsKey(path);
    }

    /**
     * 注册文件图标
     */
    private void registerIcon() {
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_FOLDER, R.drawable.next_ic_file_folder);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_IMAGE, R.drawable.next_ic_file_image);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_AUDIO, R.drawable.next_ic_file_audio);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_VIDEO, R.drawable.next_ic_file_video);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_DOCUMENT, R.drawable.next_ic_file_document);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_PACKAGE, R.drawable.next_ic_file_apk);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_ZIP, R.drawable.next_ic_file_zip);
        this.fileIconResIdList.put(FileInfo.FileType.TYPE_OTHER, R.drawable.next_ic_file_other);
    }

    /**
     * 获取文件最后修改时间
     *
     * @param fileInfo 文件信息对象
     * @return 最后修改时间
     */
    private String getFileLastChangeTime(FileInfo fileInfo) {
        String time = "";

        if (fileInfo.getFileLastChangeTime() > 0) {
            time = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(fileInfo.getFileLastChangeTime());
        }

        return time;
    }

    /**
     * 获取文件图标资源Id
     *
     * @param fileInfo 文件信息对象
     * @return 文件图标资源Id
     */
    private int getFileIconResId(FileInfo fileInfo) {
        if (this.fileIconResIdList.containsKey(fileInfo.getFileType())) {
            //获取文件图标
            return this.fileIconResIdList.get(fileInfo.getFileType());
        }

        return this.fileIconResIdList.get(FileInfo.FileType.TYPE_OTHER);
    }

    public void setFileClickListener(FileClickListener fileClickListener) {
        this.fileClickListener = fileClickListener;
    }

    public ArrayList<FileInfo> getFileInfoList() {
        return filterFileInfoList;
    }

    public void setFileInfoList(ArrayList<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
        this.filterFileInfoList.clear();
        this.filterFileInfoList.addAll(fileInfoList);
    }

    public HashMap<String, FileInfo> getSelectFileInfoList() {
        return selectFileInfoList;
    }

    public void setSelectFileInfoList(HashMap<String, FileInfo> selectFileInfoList) {
        this.selectFileInfoList = selectFileInfoList;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
        this.selectFileInfoList.clear();

        for (int i = 0; i < this.filterFileInfoList.size(); i++) {
            this.notifyItemChanged(i);
        }
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }
}