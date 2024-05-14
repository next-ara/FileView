package com.next.view.file.type;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.next.module.filehelper.TypeQueryTool;
import com.next.module.filehelper.config.FileManageConfig;
import com.next.module.filehelper.info.FileInfo;
import com.next.view.file.R;
import com.next.view.file.manage.FileManageAdapter;
import com.next.view.file.tool.DeviceTool;
import com.next.view.loading.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * ClassName:文件类型控件类
 *
 * @author Afton
 * @time 2023/8/22
 * @auditor
 */
public class FileTypeView extends LinearLayout implements FileManageAdapter.FileClickListener {

    //文件类型
    public static class FileType {
        //自定义类型
        public static final int FILE_TYPE_CUSTOM = -1;
        //图片类型
        public static final int FILE_TYPE_IMAGE = 0;
        //音频类型
        public static final int FILE_TYPE_AUDIO = 1;
        //视频类型
        public static final int FILE_TYPE_VIDEO = 2;
    }

    //文件加载监听接口
    public interface OnFileLoadListener {

        /**
         * 文件加载完成
         *
         * @param list 文件信息对象列表
         */
        void onFileLoadComplete(ArrayList<FileInfo> list);
    }

    //文件打开监听接口
    public interface OnFileOpenListener {

        /**
         * 文件打开
         *
         * @param fileInfo 文件信息对象
         */
        void onFileOpen(FileInfo fileInfo);
    }

    //文件管理列表控件
    private RecyclerView fileTypeView;

    //没有文件提示控件
    private LinearLayout noFileTipsView;

    //没有文件文本控件
    private TextView noFileTextView;

    //加载控件
    private LoadingView loadingView;

    //文件点击监听接口
    private FileManageAdapter.FileClickListener fileClickListenerObj;

    //文件管理适配对象
    private FileManageAdapter adapterObj;

    //文件排列顺序
    private int fileSortOrder = FileManageConfig.FileSort.SORT_NO;

    //Activity对象
    private Activity activity;

    //文件加载监听接口
    private OnFileLoadListener onFileLoadListener;

    //文件打开监听接口
    private OnFileOpenListener onFileOpenListener;

    //文件信息对象列表
    private ArrayList<FileInfo> fileInfoObjList = new ArrayList<>();

    public FileTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    /**
     * 初始化
     *
     * @param activity Activity对象
     * @param fileType 文件类型
     */
    public void init(Activity activity, int fileType) {
        this.activity = activity;

        //初始化列表控件
        this.initListView();

        new Thread(() -> {
            switch (fileType) {
                case FileType.FILE_TYPE_IMAGE:
                    this.fileInfoObjList = TypeQueryTool.queryAllImageTypeFile();
                    break;
                case FileType.FILE_TYPE_AUDIO:
                    this.fileInfoObjList = TypeQueryTool.queryAllAudioTypeFile();
                    break;
                default:
                    this.fileInfoObjList = TypeQueryTool.queryAllVideoTypeFile();
                    break;
            }

            this.activity.runOnUiThread(() -> {
                this.adapterObj.setFileInfoList(this.fileInfoObjList);
                //刷新适配器视图
                this.notifyDataSetChanged();

                //设置没有文件提示
                this.setNoFileTips();
                //发送文件加载监听
                this.sendFileLoadListener();
            });
        }).start();
    }

    /**
     * 初始化
     *
     * @param activity       Activity对象
     * @param fileSuffixList 文件后缀队列
     */
    public void init(Activity activity, ArrayList<String> fileSuffixList) {
        this.activity = activity;

        //初始化列表控件
        this.initListView();

        new Thread(() -> {
            this.fileInfoObjList = TypeQueryTool.queryAllDesignatedTypeFile(fileSuffixList);

            this.activity.runOnUiThread(() -> {
                this.adapterObj.setFileInfoList(this.fileInfoObjList);
                //刷新适配器视图
                this.notifyDataSetChanged();

                //设置没有文件提示
                this.setNoFileTips();
                //发送文件加载监听
                this.sendFileLoadListener();
            });
        }).start();
    }

    /**
     * 搜索
     *
     * @param charSequence 文本
     */
    public void search(CharSequence charSequence) {
        if (this.adapterObj != null) {
            this.adapterObj.getFilter().filter(charSequence);
        }
    }

    /**
     * 获取文件列表长度
     *
     * @return 长度
     */
    public int size() {
        return this.fileInfoObjList.size();
    }

    /**
     * 获取当前选中模式
     *
     * @return 选中模式
     */
    public int getSelectMode() {
        return this.adapterObj.getSelectMode();
    }

    /**
     * 设置选中模式
     *
     * @param selectMode 选中模式
     */
    public void setSelectMode(int selectMode) {
        this.adapterObj.setSelectMode(selectMode);
    }

    /**
     * 获取选中的文件信息对象Map
     *
     * @return 文件信息对象Map
     */
    public HashMap<String, FileInfo> getSelectFileInfoList() {
        return this.adapterObj.getSelectFileInfoList();
    }

    /**
     * 全选文件
     */
    public void selectAll() {
        int selectMode = this.getSelectMode();

        if (selectMode == FileManageAdapter.SelectMode.SELECT_CLOSE) {
            return;
        }

        for (int i = 0; i < fileInfoObjList.size(); i++) {
            FileInfo fileInfo = fileInfoObjList.get(i);

            if (fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                if (selectMode == FileManageAdapter.SelectMode.SELECT_ALL || selectMode == FileManageAdapter.SelectMode.SELECT_FOLDER) {
                    this.adapterObj.addSelectFile(fileInfo);
                }
            } else {
                if (selectMode == FileManageAdapter.SelectMode.SELECT_ALL || selectMode == FileManageAdapter.SelectMode.SELECT_FILE) {
                    this.adapterObj.addSelectFile(fileInfo);
                }
            }
        }
    }

    /**
     * 取消选择
     */
    public void cancelSelect() {
        if (this.getSelectMode() == FileManageAdapter.SelectMode.SELECT_CLOSE) {
            return;
        }

        this.adapterObj.getSelectFileInfoList().clear();
        this.adapterObj.notifyDataSetChanged();
    }

    /**
     * 设置文件选中
     *
     * @param fileInfo 文件信息对象
     */
    public void setFileSelect(FileInfo fileInfo) {
        int selectMode = this.getSelectMode();

        if (selectMode == FileManageAdapter.SelectMode.SELECT_CLOSE) {
            return;
        }

        if (selectMode == FileManageAdapter.SelectMode.SELECT_ALL || selectMode == FileManageAdapter.SelectMode.SELECT_FILE) {
            this.adapterObj.addSelectFile(fileInfo);
        }
    }

    /**
     * 释放
     */
    public void recycle() {
        this.fileClickListenerObj = null;
        this.onFileLoadListener = null;
        this.onFileOpenListener = null;
    }

    /**
     * 初始化列表控件
     */
    private void initListView() {
        this.initData();

        LinearLayoutManager layoutManager;

        //是否需要屏幕适配
        if (this.isNeedScreenAdaptation()) {
            layoutManager = new GridLayoutManager(this.getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }

        this.fileTypeView.setLayoutManager(layoutManager);
        this.fileTypeView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, int itemPosition, @NonNull RecyclerView parent) {
                super.getItemOffsets(outRect, itemPosition, parent);
                //是否需要屏幕适配
                if (isNeedScreenAdaptation()) {
                    if ((itemPosition + 1) % 2 == 0) {
                        outRect.left = (int) getContext().getResources().getDimension(R.dimen.dp_4);
                    } else {
                        outRect.right = (int) getContext().getResources().getDimension(R.dimen.dp_4);
                    }
                }

                outRect.bottom = (int) getContext().getResources().getDimension(R.dimen.dp_8);
            }
        });

        this.fileTypeView.setAdapter(adapterObj);
        this.setOnSearchListener();
        ((SimpleItemAnimator) Objects.requireNonNull(this.fileTypeView.getItemAnimator())).setSupportsChangeAnimations(false);
        this.showLoading();
    }

    /**
     * 设置搜索监听
     */
    private void setOnSearchListener() {
        this.adapterObj.setOnSearchListener(() -> {
            //设置没有文件提示
            this.setNoFileTips();
            //发送文件加载监听
            this.sendFileLoadListener();
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.next_view_file_manage, this);
        this.fileTypeView = this.findViewById(R.id.rv_file_manage);
        this.noFileTipsView = this.findViewById(R.id.layout_no_file_tips);
        this.noFileTextView = this.findViewById(R.id.tv_no_file_tips);
        this.loadingView = this.findViewById(R.id.loadingView);
    }

    /**
     * 加载数据
     */
    private void initData() {
        this.fileInfoObjList.clear();

        if (this.adapterObj == null) {
            this.adapterObj = new FileManageAdapter(this.getContext());
        }

        this.adapterObj.setFileClickListener(this);
    }

    /**
     * 点击文件处理
     *
     * @param view     控件
     * @param fileInfo 文件信息对象
     */
    private void clickFileHandle(View view, FileInfo fileInfo) {
        if (this.getSelectMode() == FileManageAdapter.SelectMode.SELECT_CLOSE) {
            this.selectModeCloseHandle(fileInfo);
        } else {
            this.selectModeOpenHandle(fileInfo);
        }

        if (this.fileClickListenerObj != null) {
            this.fileClickListenerObj.onClick(view, fileInfo);
        }
    }

    /**
     * 开启选中模式点击处理
     *
     * @param fileInfo 文件信息对象
     */
    private void selectModeOpenHandle(FileInfo fileInfo) {
        int selectMode = this.getSelectMode();
        if (selectMode == FileManageAdapter.SelectMode.SELECT_ALL) {
            this.changeSelectState(fileInfo);
        } else if (selectMode == FileManageAdapter.SelectMode.SELECT_FILE) {
            if (!fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                this.changeSelectState(fileInfo);
            }
        } else {
            if (fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                this.changeSelectState(fileInfo);
            }
        }
    }

    /**
     * 修改选中状态
     *
     * @param fileInfo 文件信息对象
     */
    private void changeSelectState(FileInfo fileInfo) {
        if (this.adapterObj.fileIsSelect(fileInfo)) {
            this.adapterObj.deleteSelectFile(fileInfo);
        } else {
            this.adapterObj.addSelectFile(fileInfo);
        }
    }

    /**
     * 关闭选中模式点击处理
     *
     * @param fileInfo 文件信息对象
     */
    private void selectModeCloseHandle(FileInfo fileInfo) {
        if (this.onFileOpenListener != null) {
            this.onFileOpenListener.onFileOpen(fileInfo);
        }
    }

    /**
     * 长按文件处理
     *
     * @param view     控件
     * @param fileInfo 文件信息对象
     */
    private void longClickFileHandle(View view, FileInfo fileInfo) {
        if (this.fileClickListenerObj != null) {
            this.fileClickListenerObj.onLongClick(view, fileInfo);
        }
    }

    /**
     * 显示加载中视图
     */
    private void showLoading() {
        this.activity.runOnUiThread(() -> {
            this.loadingView.setVisibility(VISIBLE);
            this.noFileTipsView.setVisibility(GONE);
            this.fileTypeView.setVisibility(GONE);
        });
    }

    /**
     * 显示没有文件提示视图
     */
    private void showNoTips() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(VISIBLE);
        this.fileTypeView.setVisibility(GONE);

        this.noFileTextView.setText(activity.getString(R.string.file_manage_no_file_tips));
        this.noFileTextView.setVisibility(this.isNeedScreenAdaptation() ? GONE : VISIBLE);
    }

    /**
     * 显示没有文件提示视图
     *
     * @param tips 提示
     */
    private void showNoTips(String tips) {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(VISIBLE);
        this.fileTypeView.setVisibility(GONE);

        this.noFileTextView.setText(tips);
        this.noFileTextView.setVisibility(this.isNeedScreenAdaptation() ? GONE : VISIBLE);
    }

    /**
     * 显示文件管理视图
     */
    private void showFileManage() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(GONE);
        this.fileTypeView.setVisibility(VISIBLE);
    }

    /**
     * 发送文件加载监听
     */
    private void sendFileLoadListener() {
        if (this.onFileLoadListener != null) {
            this.onFileLoadListener.onFileLoadComplete(this.noFileTipsView.getVisibility() == VISIBLE ? new ArrayList<>() : this.adapterObj.getFileInfoList());
        }
    }

    /**
     * 设置没有文件提示
     */
    private void setNoFileTips() {
        if (this.adapterObj.getFileInfoList().isEmpty()) {
            this.showNoTips();
        } else {
            this.showFileManage();
        }
    }

    /**
     * 刷新适配器视图
     */
    private void notifyDataSetChanged() {
        this.activity.runOnUiThread(() -> adapterObj.notifyDataSetChanged());
    }

    /**
     * 是否需要屏幕适配
     *
     * @return true/false
     */
    private boolean isNeedScreenAdaptation() {
        //横屏或者短屏需要适配
        return DeviceTool.isLandscapeScreen(this.getContext()) || DeviceTool.isShortScreen(this.getContext());
    }

    public FileManageAdapter.FileClickListener getFileClickListenerObj() {
        return fileClickListenerObj;
    }

    public void setFileClickListenerObj(FileManageAdapter.FileClickListener fileClickListenerObj) {
        this.fileClickListenerObj = fileClickListenerObj;
    }

    public int getFileSortOrder() {
        return fileSortOrder;
    }

    public void setFileSortOrder(int fileSortOrder) {
        this.fileSortOrder = fileSortOrder;
    }

    @Override
    public void onClick(View view, FileInfo fileInfo) {
        this.clickFileHandle(view, fileInfo);
    }

    @Override
    public void onLongClick(View view, FileInfo fileInfo) {
        this.longClickFileHandle(view, fileInfo);
    }

    public void setOnFileLoadListener(OnFileLoadListener onFileLoadListener) {
        this.onFileLoadListener = onFileLoadListener;
    }

    public void setOnFileOpenListener(OnFileOpenListener onFileOpenListener) {
        this.onFileOpenListener = onFileOpenListener;
    }
}