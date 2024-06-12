package com.next.view.file.manage;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.next.module.file2.File2;
import com.next.module.file2.tool.FilePathTool;
import com.next.view.file.R;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.DeviceTool;
import com.next.view.file.tool.list.FileLoadException;
import com.next.view.file.tool.list.GetFileListTool;
import com.next.view.loading.LoadingView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * ClassName:文件管理控件类
 *
 * @author Afton
 * @time 2023/7/8
 * @auditor
 */
public class FileManageView extends LinearLayout {

    //文件加载监听接口
    public interface OnFileLoadListener {

        /**
         * 文件加载完成
         *
         * @param list 文件信息对象列表
         */
        void onLoadComplete(ArrayList<FileInfo> list);

        /**
         * 文件加载失败
         *
         * @param e 错误信息
         */
        void onLoadError(FileLoadException e);
    }

    //文件管理列表控件
    private RecyclerView fileManageView;

    //没有文件提示控件
    private LinearLayout noFileTipsView;

    //没有文件文本控件
    private TextView noFileTextView;

    //加载控件
    private LoadingView loadingView;

    //文件管理适配对象
    private FileManageAdapter adapterObj;

    //文件加载监听接口
    private OnFileLoadListener onFileLoadListener;

    //主线程Handler
    private Handler mainHandler;

    //获取文件列表工具对象
    private GetFileListTool getFileListTool;

    //选择模式
    private int selectMode = GetFileListTool.SelectMode.SELECT_CLOSE;

    //排序模式
    private int sortMode = GetFileListTool.SortMode.NAME_FORWARD;

    //显示模式
    private int showMode = GetFileListTool.ShowMode.SHOW_ALL;

    //是否显示隐藏文件
    private boolean isShowHideFile = false;

    //是否正在加载
    private boolean isLoading = false;

    public FileManageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        //初始化视图
        this.initView();
        //初始化数据
        this.initData();
    }

    /**
     * 加载路径
     */
    public void loadPath(String path) {
        if (this.isLoading) {
            return;
        }

        //显示加载
        this.showLoading();

        new Thread(() -> {
            try {
                ArrayList<FileInfo> fileInfoObjList = this.getFileListTool.getFileInfoList(path, this.isShowHideFile, this.sortMode, this.showMode, this.selectMode);
                this.adapterObj.setFileInfoList(fileInfoObjList);
                this.mainHandler.post(() -> {
                    //关闭加载
                    this.closeLoading();
                    this.adapterObj.notifyDataSetChanged();
                    this.sendLoadComplete();
                });
            } catch (FileLoadException e) {
                this.mainHandler.post(() -> {
                    //关闭加载
                    this.closeLoading(e);
                    this.sendLoadError(e);
                });
            }
        }).start();
    }

    /**
     * 刷新列表
     */
    public void refreshPath() {
        if (this.isLoading) {
            return;
        }

        //显示加载
        this.showLoading();

        new Thread(() -> {
            try {
                String path = this.getFileListTool.getNowPath();
                ArrayList<FileInfo> fileInfoObjList = this.getFileListTool.getFileInfoList(path, this.isShowHideFile, this.sortMode, this.showMode, this.selectMode);
                this.adapterObj.setFileInfoList(fileInfoObjList);
                this.mainHandler.post(() -> {
                    //关闭加载
                    this.closeLoading();
                    this.adapterObj.notifyDataSetChanged();
                    this.sendLoadComplete();
                });
            } catch (FileLoadException e) {
                this.mainHandler.post(() -> {
                    //关闭加载
                    this.closeLoading(e);
                    this.sendLoadError(e);
                });
            }
        }).start();
    }

    /**
     * 返回上一级路径
     *
     * @return 是否返回上一级路径
     */
    public boolean backLastPath() {
        if (this.selectMode != GetFileListTool.SelectMode.SELECT_CLOSE) {
            return false;
        }

        String nowPath = this.getFileListTool.getNowPath();
        if (FilePathTool.ROOT_PATH.equals(nowPath)) {
            return false;
        }

        this.loadPath(FilePathTool.getParentPath(nowPath));
        return true;
    }

    /**
     * 设置文件选择类型
     *
     * @param isSelect 是否选择
     * @param fileInfo 文件信息对象
     */
    public void setItemSelectType(boolean isSelect, FileInfo fileInfo) {
        if (this.selectMode == GetFileListTool.SelectMode.SELECT_CLOSE) {
            return;
        }

        if (this.selectMode == GetFileListTool.SelectMode.SELECT_FILE && fileInfo.isDirectory()) {
            return;
        }

        //设置选择类型
        fileInfo.setSelectType(isSelect ? FileInfo.SelectType.SELECT_TYPE_SELECT : FileInfo.SelectType.SELECT_TYPE_UNSELECT);
        //通知数据更新
        this.adapterObj.notifyItemChanged(fileInfo);
    }

    /**
     * 关闭选择模式
     */
    public void closeSelect() {
        if (this.selectMode == GetFileListTool.SelectMode.SELECT_CLOSE) {
            return;
        }

        //设置选择模式
        this.selectMode = GetFileListTool.SelectMode.SELECT_CLOSE;
        ArrayList<FileInfo> fileInfoList = this.adapterObj.getFileInfoList();
        this.getFileListTool.setItemSelectMode(fileInfoList, this.selectMode);
        for (int i = 0; i < fileInfoList.size(); i++) {
            this.adapterObj.notifyItemChanged(i);
        }
    }

    /**
     * 获取文件信息对象列表
     *
     * @return 文件信息对象列表
     */
    public ArrayList<FileInfo> getFileInfoList() {
        return this.adapterObj.getFileInfoList();
    }

    /**
     * 获取选中的文件信息对象列表
     *
     * @return 文件信息对象列表
     */
    public ArrayList<FileInfo> getSelectFileInfoList() {
        return this.adapterObj.getSelectFileInfoList();
    }

    /**
     * 获取当前路径
     *
     * @return 当前路径
     */
    public String getNowPath() {
        return this.getFileListTool.getNowPath();
    }

    /**
     * 获取父文件对象
     *
     * @return 父文件对象
     */
    public File2 getParentFile() {
        return this.getFileListTool.getParentFile();
    }

    /**
     * 设置文件点击监听接口
     *
     * @param fileClickListenerObj 文件点击监听接口
     */
    public void setFileClickListener(FileManageAdapter.FileClickListener fileClickListenerObj) {
        this.adapterObj.setFileClickListener(fileClickListenerObj);
    }

    /**
     * 添加文件点击监听接口
     *
     * @param fileClickListenerObj 文件点击监听接口
     */
    public void addFileClickListener(FileManageAdapter.FileClickListener fileClickListenerObj) {
        this.adapterObj.addFileClickListener(fileClickListenerObj);
    }

    /**
     * 显示加载中视图
     */
    private void showLoading() {
        this.loadingView.setVisibility(VISIBLE);
        this.noFileTipsView.setVisibility(GONE);
        this.adapterObj.clear();
        this.isLoading = true;
    }

    /**
     * 关闭加载中视图
     */
    private void closeLoading() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(GONE);
        this.isLoading = false;

        if (this.adapterObj.getFileInfoList().isEmpty()) {
            this.showNoTips(this.getString(R.string.file_manage_no_file_tips));
        }
    }

    /**
     * 关闭加载中视图
     *
     * @param e 文件加载异常
     */
    private void closeLoading(FileLoadException e) {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(GONE);
        this.isLoading = false;

        if (this.adapterObj.getFileInfoList().isEmpty()) {
            String tips;
            switch (e.getErrorCode()) {
                case FileLoadException.ErrorCode.ERROR_CODE_NO_PERMISSION ->
                        tips = this.getString(R.string.file_manage_limit_tips);
                default -> tips = this.getString(R.string.file_manage_no_file_tips);
            }

            this.showNoTips(tips);
        }
    }

    /**
     * 显示没有文件提示视图
     */
    private void showNoTips(String tips) {
        this.noFileTipsView.setVisibility(VISIBLE);
        this.noFileTextView.setText(tips);
        this.noFileTextView.setVisibility(this.isNeedScreenAdaptation() ? GONE : VISIBLE);
    }

    /**
     * 获取字符串
     *
     * @param stringResId 字符串资源Id
     * @return 字符串
     */
    private String getString(@StringRes int stringResId) {
        return this.getContext().getString(stringResId);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.next_view_file_manage, this);
        this.fileManageView = this.findViewById(R.id.rv_file_manage);
        this.noFileTipsView = this.findViewById(R.id.layout_no_file_tips);
        this.noFileTextView = this.findViewById(R.id.tv_no_file_tips);
        this.loadingView = this.findViewById(R.id.loadingView);
    }

    /**
     * 加载数据
     */
    private void initData() {
        //初始化主线程Handler
        this.mainHandler = this.getMainHandler();
        //初始化获取文件列表工具对象
        this.getFileListTool = new GetFileListTool();
        //初始化文件管理适配器对象
        this.adapterObj = new FileManageAdapter(this.getContext());
        //添加文件点击监听
        this.addFileClickListener(new FileManageAdapter.FileClickListener() {
            @Override
            public void onClick(FileInfo fileInfo) {
                FileManageView.this.itemClick(fileInfo);
            }

            @Override
            public void onLongClick(FileInfo fileInfo) {
                FileManageView.this.itemLongClick(fileInfo);
            }
        });

        this.fileManageView.setLayoutManager(this.isNeedScreenAdaptation() ? new GridLayoutManager(this.getContext(), 2) : new LinearLayoutManager(getContext()));
        this.fileManageView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int itemPosition = parent.getChildLayoutPosition(view);

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

        this.fileManageView.setAdapter(this.adapterObj);
        ((SimpleItemAnimator) Objects.requireNonNull(this.fileManageView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    /**
     * 文件点击事件
     *
     * @param fileInfo 文件信息对象
     */
    private void itemClick(FileInfo fileInfo) {
        if (this.selectMode == GetFileListTool.SelectMode.SELECT_CLOSE) {
            //未选择模式文件点击事件
            this.unSelectModeItemClick(fileInfo);
        } else {
            //选择模式文件点击事件
            this.selectModeItemClick(fileInfo);
        }
    }

    /**
     * 选择模式文件点击事件
     *
     * @param fileInfo 文件信息对象
     */
    private void selectModeItemClick(FileInfo fileInfo) {
        //设置文件选择类型
        this.setItemSelectType(fileInfo.getSelectType() != FileInfo.SelectType.SELECT_TYPE_SELECT, fileInfo);
    }

    /**
     * 未选择模式文件点击事件
     *
     * @param fileInfo
     */
    private void unSelectModeItemClick(FileInfo fileInfo) {
        if (fileInfo.isDirectory()) {
            String path = FilePathTool.getChildPath(this.getFileListTool.getNowPath(), fileInfo.getFileName());
            this.loadPath(path);
        }
    }

    /**
     * 文件长按事件
     *
     * @param fileInfo 文件信息对象
     */
    private void itemLongClick(FileInfo fileInfo) {
        if (this.selectMode == GetFileListTool.SelectMode.SELECT_CLOSE && !fileInfo.isDirectory()) {
            //设置选择模式
            this.selectMode = GetFileListTool.SelectMode.SELECT_FILE;
            ArrayList<FileInfo> fileInfoList = this.adapterObj.getFileInfoList();
            this.getFileListTool.setItemSelectMode(fileInfoList, this.selectMode);
            fileInfo.setSelectType(FileInfo.SelectType.SELECT_TYPE_SELECT);
            for (int i = 0; i < fileInfoList.size(); i++) {
                this.adapterObj.notifyItemChanged(i);
            }
        }
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

    /**
     * 获取主线程Handler
     *
     * @return 主线程Handler
     */
    private Handler getMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    /**
     * 发送文件加载完成监听
     */
    private void sendLoadComplete() {
        if (this.onFileLoadListener != null) {
            this.onFileLoadListener.onLoadComplete(this.adapterObj.getFileInfoList());
        }
    }

    /**
     * 发送文件加载错误监听
     *
     * @param e 文件加载异常
     */
    private void sendLoadError(FileLoadException e) {
        if (this.onFileLoadListener != null) {
            this.onFileLoadListener.onLoadError(e);
        }
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public int getSortMode() {
        return sortMode;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    public int getShowMode() {
        return showMode;
    }

    public void setShowMode(int showMode) {
        this.showMode = showMode;
    }

    public boolean isShowHideFile() {
        return isShowHideFile;
    }

    public void setShowHideFile(boolean showHideFile) {
        isShowHideFile = showHideFile;
    }

    public OnFileLoadListener getOnFileLoadListener() {
        return onFileLoadListener;
    }

    public void setOnFileLoadListener(OnFileLoadListener onFileLoadListener) {
        this.onFileLoadListener = onFileLoadListener;
    }
}