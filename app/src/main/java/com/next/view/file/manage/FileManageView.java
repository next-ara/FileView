package com.next.view.file.manage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.next.module.filehelper.ExecuteCallBack;
import com.next.module.filehelper.FileManageTool;
import com.next.module.filehelper.config.FileManageConfig;
import com.next.module.filehelper.info.FileInfo;
import com.next.view.file.R;
import com.next.view.file.tool.DeviceTool;
import com.next.view.file.type.FileTypeView;
import com.next.view.loading.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * ClassName:文件管理控件类
 *
 * @author Afton
 * @time 2023/7/8
 * @auditor
 */
public class FileManageView extends LinearLayout implements ExecuteCallBack, FileManageAdapter.FileClickListener {

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
    private RecyclerView fileManageView;

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

    //文件管理工具对象
    private FileManageTool fileManageToolObj = new FileManageTool();

    //文件排列顺序
    private int fileSortOrder = FileManageConfig.FileSort.SORT_NO;

    //Activity对象
    private Activity activity;

    //是否显示隐藏文件
    private boolean isShowHideFile = false;

    //文件加载监听接口
    private OnFileLoadListener onFileLoadListener;

    //文件打开监听接口
    private FileTypeView.OnFileOpenListener onFileOpenListener;

    public FileManageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    /**
     * 初始化
     *
     * @param activity Activity对象
     * @param path     文件路径
     */
    public void init(Activity activity, String path) {
        this.activity = activity;

        this.initData(path);

        LinearLayoutManager layoutManager;

        //是否需要屏幕适配
        if (this.isNeedScreenAdaptation()) {
            layoutManager = new GridLayoutManager(this.getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }

        this.fileManageView.setLayoutManager(layoutManager);
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
        this.setOnSearchListener();
        ((SimpleItemAnimator) Objects.requireNonNull(this.fileManageView.getItemAnimator())).setSupportsChangeAnimations(false);
        this.showLoading();
        this.fileManageToolObj.creatFileInfoList();
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
     * 重命名
     *
     * @param nowName    当前文件名称
     * @param changeName 修改的文件名称
     */
    public void reName(String nowName, String changeName) {
        this.fileManageToolObj.reNameFile(nowName, changeName);
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     */
    public void delete(String fileName) {
        this.fileManageToolObj.deleteFile(fileName);
    }

    /**
     * 新建文件
     *
     * @param fileName 文件名称
     */
    public void creatFile(String fileName) {
        this.fileManageToolObj.creatFile(fileName);
    }

    /**
     * 新建文件夹
     *
     * @param fileName 文件夹名称
     */
    public void creatFolder(String fileName) {
        this.fileManageToolObj.creatFolder(fileName);
    }

    /**
     * 返回上一页
     */
    public void back() {
        this.showLoading();
        this.fileManageToolObj.backLastDir();
    }

    /**
     * 刷新列表
     */
    public void update() {
        this.showLoading();
        this.fileManageToolObj.updateFileList();
    }

    /**
     * 设置新路径
     *
     * @param path 文件路径
     */
    public void setNewPath(String path) {
        this.fileManageToolObj.setNowFilePath(path);
        this.update();
    }

    /**
     * 滚动到文件
     *
     * @param fileName 文件名称
     */
    public void scrollToFile(String fileName) {
        ArrayList<FileInfo> fileInfoObjList = this.fileManageToolObj.getFileInfoObjList();
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
     * 滚动到顶部
     */
    public void scrollToTop() {
        if (!this.adapterObj.getFileInfoList().isEmpty()) {
            this.scrollToPosition(0);
        }
    }

    /**
     * 获取文件列表长度
     *
     * @return 长度
     */
    public int size() {
        return this.fileManageToolObj.getFileInfoObjList().size();
    }

    /**
     * 获取文件列表
     *
     * @return 文件信息对象列表
     */
    public ArrayList<FileInfo> getFileList() {
        return fileManageToolObj.getFileInfoObjList();
    }

    /**
     * 设置显示模式
     *
     * @param showMode 显示模式
     */
    public void setShowMode(int showMode) {
        this.fileManageToolObj.setShowMode(showMode);
    }

    /**
     * 获取当前文件路径
     *
     * @return 文件路径
     */
    public String getNowPath() {
        return this.fileManageToolObj.getNowFilePath();
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

        ArrayList<FileInfo> fileInfoList = this.getFileList();
        for (int i = 0; i < fileInfoList.size(); i++) {
            FileInfo fileInfo = fileInfoList.get(i);

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
        //刷新适配器视图
        this.notifyDataSetChanged();
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
     * 验证是否是限制访问文件夹
     *
     * @return true/false
     */
    public boolean isLimitFolder() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (this.getNowPath().contains(FileManageTool.Path.ROOT + "/Android/data")
                    || this.getNowPath().contains(FileManageTool.Path.ROOT + "/Android/obb")) {
                return true;
            }
        }

        return false;
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
        this.fileManageView = this.findViewById(R.id.rv_file_manage);
        this.noFileTipsView = this.findViewById(R.id.layout_no_file_tips);
        this.noFileTextView = this.findViewById(R.id.tv_no_file_tips);
        this.loadingView = this.findViewById(R.id.loadingView);
    }

    /**
     * 加载数据
     */
    private void initData(String path) {
        if (this.adapterObj == null) {
            this.adapterObj = new FileManageAdapter(this.getContext());
        }

        this.adapterObj.setFileClickListener(this);
        this.fileManageToolObj.setExecuteCallBackObj(this);
        this.fileManageToolObj.setFileSortOrder(this.fileSortOrder);
        this.fileManageToolObj.setShowHideFile(this.isShowHideFile);
        this.fileManageToolObj.setNowFilePath(path);
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
        if (fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
            this.showLoading();
            this.fileManageToolObj.openDir(fileInfo.getFileName());
        } else {
            if (this.onFileOpenListener != null) {
                this.onFileOpenListener.onFileOpen(fileInfo);
            }
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
        this.loadingView.setVisibility(VISIBLE);
        this.noFileTipsView.setVisibility(GONE);
        this.fileManageView.setVisibility(GONE);
    }

    /**
     * 显示没有文件提示视图
     */
    private void showNoTips() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(VISIBLE);
        this.fileManageView.setVisibility(GONE);

        this.noFileTextView.setText(this.activity.getString(R.string.file_manage_no_file_tips));
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
        this.fileManageView.setVisibility(GONE);

        this.noFileTextView.setText(tips);
        this.noFileTextView.setVisibility(this.isNeedScreenAdaptation() ? GONE : VISIBLE);
    }

    /**
     * 显示文件管理视图
     */
    private void showFileManage() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(GONE);
        this.fileManageView.setVisibility(VISIBLE);
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
        this.activity.runOnUiThread(() -> {
            //停止滑动
            this.fileManageView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
            //刷新
            this.adapterObj.notifyDataSetChanged();
        });
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

    public boolean isShowHideFile() {
        return isShowHideFile;
    }

    public void setShowHideFile(boolean showHideFile) {
        isShowHideFile = showHideFile;

        this.fileManageToolObj.setShowHideFile(this.isShowHideFile);
    }

    @Override
    public void success() {
        this.activity.runOnUiThread(() -> {
            this.adapterObj.setFileInfoList(this.fileManageToolObj.getFileInfoObjList());
            //刷新适配器视图
            this.notifyDataSetChanged();
            //滚动到顶部
            this.scrollToTop();

            //设置没有文件提示
            this.setNoFileTips();
            //发送文件加载监听
            this.sendFileLoadListener();
        });
    }

    @Override
    public void fail(String info) {
        this.activity.runOnUiThread(() -> {
            if (info.equals(activity.getString(R.string.file_manage_limit_tips))) {
                showNoTips(info);
            } else {
                Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
                //设置没有文件提示
                this.setNoFileTips();
            }

            //发送文件加载监听
            this.sendFileLoadListener();
        });
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

    public void setOnFileOpenListener(FileTypeView.OnFileOpenListener onFileOpenListener) {
        this.onFileOpenListener = onFileOpenListener;
    }
}