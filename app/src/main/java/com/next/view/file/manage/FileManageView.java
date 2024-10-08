package com.next.view.file.manage;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.next.module.file2.File2;
import com.next.module.file2.tool.FilePathTool;
import com.next.view.file.OnFileClickListener;
import com.next.view.file.OnFileLoadListener;
import com.next.view.file.OnSelectStateListener;
import com.next.view.file.R;
import com.next.view.file.application.tool.GetAppListTool;
import com.next.view.file.info.FileInfo;
import com.next.view.file.manage.tool.FileLoadException;
import com.next.view.file.manage.tool.GetFileListTool;
import com.next.view.file.tool.DeviceTool;
import com.next.view.file.tool.ViewTool;
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

    //选择状态监听接口
    private OnSelectStateListener onSelectStateListener;

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
     * 加载列表
     *
     * @param path 路径
     */
    public void loadList(String path) {
        if (this.isLoading) {
            return;
        }

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(150);
        alphaAnimation.setInterpolator(PathInterpolatorCompat.create(0f, 1f, 1f, 1f));
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //显示加载
                FileManageView.this.showLoading();

                new Thread(() -> {
                    try {
                        ArrayList<FileInfo> fileInfoObjList = FileManageView.this.getFileListTool.getFileInfoList(path, FileManageView.this.isShowHideFile, FileManageView.this.sortMode, FileManageView.this.showMode, FileManageView.this.selectMode);
                        FileManageView.this.adapterObj.setFileInfoList(fileInfoObjList);
                        FileManageView.this.mainHandler.post(() -> {
                            //关闭加载
                            FileManageView.this.closeLoading();
                            FileManageView.this.adapterObj.notifyDataSetChanged();
                            FileManageView.this.sendLoadComplete();
                        });
                    } catch (FileLoadException e) {
                        FileManageView.this.mainHandler.post(() -> {
                            //关闭加载
                            FileManageView.this.closeLoading(e);
                            FileManageView.this.sendLoadError(e);
                        });
                    }
                }).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.fileManageView.startAnimation(alphaAnimation);
    }

    /**
     * 刷新列表
     */
    public void refreshList() {
        String path = this.getFileListTool.getNowPath();
        this.loadList(path);
    }

    /**
     * 滚动到位置
     *
     * @param fileName 文件名称
     */
    public void scrollToPosition(String fileName) {
        ArrayList<FileInfo> fileInfoObjList = this.adapterObj.getFileInfoList();
        for (int i = 0; i < fileInfoObjList.size(); i++) {
            FileInfo fileInfo = fileInfoObjList.get(i);
            if (fileInfo.getFileName().equals(fileName)) {
                //滚动到文件位置
                this.scrollToPosition(i);
                return;
            }
        }
    }

    /**
     * 滚动到位置
     *
     * @param position 位置
     */
    public void scrollToPosition(int position) {
        LinearLayoutManager manager = (LinearLayoutManager) this.fileManageView.getLayoutManager();
        Objects.requireNonNull(manager).scrollToPositionWithOffset(position, 0);
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

        this.loadList(FilePathTool.getParentPath(nowPath));
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
     * 全选
     */
    public void selectAll() {
        ArrayList<FileInfo> list = this.adapterObj.getFileInfoList();
        for (FileInfo fileInfo : list) {
            if (fileInfo.getSelectType() != FileInfo.SelectType.SELECT_TYPE_SELECT) {
                //设置文件选择类型
                this.setItemSelectType(true, fileInfo);
            }
        }
    }

    /**
     * 取消全选
     */
    public void unSelectAll() {
        ArrayList<FileInfo> list = this.adapterObj.getFileInfoList();
        for (FileInfo fileInfo : list) {
            if (fileInfo.getSelectType() != FileInfo.SelectType.SELECT_TYPE_UNSELECT) {
                //设置文件选择类型
                this.setItemSelectType(false, fileInfo);
            }
        }
    }

    /**
     * 修改选择模式
     *
     * @param isSelect 是否选择
     */
    public void changeSelectMode(boolean isSelect) {
        if (this.selectMode == GetAppListTool.SelectMode.SELECT_CLOSE && !isSelect) {
            return;
        }

        if (this.selectMode == GetAppListTool.SelectMode.SELECT_FILE && isSelect) {
            return;
        }

        //设置选择模式
        this.selectMode = isSelect ? GetAppListTool.SelectMode.SELECT_FILE : GetAppListTool.SelectMode.SELECT_CLOSE;
        ArrayList<FileInfo> fileInfoList = this.adapterObj.getFileInfoList();
        this.getFileListTool.setItemSelectMode(fileInfoList, this.selectMode);
        for (int i = 0; i < fileInfoList.size(); i++) {
            this.adapterObj.notifyItemChanged(i);
        }

        //发送选择状态改变监听
        this.sendSelectStateChange(isSelect);
    }

    /**
     * 修改选择模式
     *
     * @param isSelect 是否选择
     * @param fileInfo 文件信息对象
     */
    public void changeSelectMode(boolean isSelect, FileInfo fileInfo) {
        if (this.selectMode == GetAppListTool.SelectMode.SELECT_CLOSE && !isSelect) {
            return;
        }

        if (this.selectMode == GetAppListTool.SelectMode.SELECT_FILE && isSelect) {
            return;
        }

        //设置选择模式
        this.selectMode = isSelect ? GetAppListTool.SelectMode.SELECT_FILE : GetAppListTool.SelectMode.SELECT_CLOSE;
        ArrayList<FileInfo> fileInfoList = this.adapterObj.getFileInfoList();
        this.getFileListTool.setItemSelectMode(fileInfoList, this.selectMode);
        fileInfo.setSelectType(isSelect ? FileInfo.SelectType.SELECT_TYPE_SELECT : FileInfo.SelectType.SELECT_TYPE_NONE);
        for (int i = 0; i < fileInfoList.size(); i++) {
            this.adapterObj.notifyItemChanged(i);
        }

        //发送选择状态改变监听
        this.sendSelectStateChange(isSelect);
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
     * @param onFileClickListener 文件点击监听接口
     */
    public void setFileClickListener(OnFileClickListener onFileClickListener) {
        this.adapterObj.setOnFileClickListeners(onFileClickListener);
    }

    /**
     * 添加文件点击监听接口
     *
     * @param onFileClickListener 文件点击监听接口
     */
    public void addFileClickListener(OnFileClickListener onFileClickListener) {
        this.adapterObj.addFileClickListener(onFileClickListener);
    }

    /**
     * 设置选择状态监听接口
     *
     * @param onSelectStateListener 选择状态监听接口
     */
    public void setOnSelectStateListener(OnSelectStateListener onSelectStateListener) {
        this.onSelectStateListener = onSelectStateListener;
    }

    /**
     * 搜索
     *
     * @param constraint 搜索内容
     */
    public void search(CharSequence constraint) {
        //显示加载
        this.showLoading();
        //搜索
        this.adapterObj.getFilter().filter(constraint);
    }

    /**
     * 设置搜索监听
     */
    private void setOnSearchListener() {
        this.adapterObj.setOnSearchListener(() -> {
            //关闭加载
            this.closeLoading();
            //发送文件加载完成监听
            this.sendLoadComplete();
        });
    }

    /**
     * 显示列表动画
     */
    private void showListAnim() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, this.fileManageView.getHeight(), 0);
        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(PathInterpolatorCompat.create(0f, 1f, 0f, 1f));
        this.fileManageView.startAnimation(translateAnimation);
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
        } else {
            //显示列表动画
            this.showListAnim();
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

        String tips;
        switch (e.getErrorCode()) {
            case FileLoadException.ErrorCode.ERROR_CODE_NO_PERMISSION ->
                    tips = this.getString(R.string.file_manage_limit_tips);
            default -> tips = this.getString(R.string.file_manage_no_file_tips);
        }

        this.showNoTips(tips);
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
        this.addFileClickListener(new OnFileClickListener() {
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
        //设置搜索监听
        this.setOnSearchListener();

        this.post(() -> {
            ViewTool.setViewOnScreenCenter(this.loadingView);
        });
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
            this.loadList(path);
        }
    }

    /**
     * 文件长按事件
     *
     * @param fileInfo 文件信息对象
     */
    private void itemLongClick(FileInfo fileInfo) {
        if (this.selectMode == GetFileListTool.SelectMode.SELECT_CLOSE && !fileInfo.isDirectory()) {
            //修改选择模式
            this.changeSelectMode(true, fileInfo);
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
            this.onFileLoadListener.onLoadComplete();
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

    /**
     * 发送选择状态改变监听
     *
     * @param isSelect 是否选中
     */
    private void sendSelectStateChange(boolean isSelect) {
        if (this.onSelectStateListener != null) {
            this.onSelectStateListener.onSelectStateChanged(isSelect);
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