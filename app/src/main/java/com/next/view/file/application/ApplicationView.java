package com.next.view.file.application;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.next.module.filehelper.config.FileManageConfig;
import com.next.module.filehelper.info.ApkInfo;
import com.next.module.filehelper.info.FileInfo;
import com.next.view.file.R;
import com.next.view.file.tool.DeviceTool;
import com.next.view.loading.LoadingView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * ClassName:应用控件类
 *
 * @author Afton
 * @time 2023/9/28
 * @auditor
 */
public class ApplicationView extends LinearLayout implements ApplicationAdapter.FileClickListener {

    //应用类型
    public static class ApplicationType {
        //所有类型
        public static final int ALL_TYPE = 0;
        //只显示用户应用
        public static final int USER_TYPE = 1;
        //只显示系统应用
        public static final int SYSTEM_TYPE = 2;
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
    private RecyclerView applicationView;

    //没有文件提示控件
    private LinearLayout noFileTipsView;

    //没有文件文本控件
    private TextView noFileTextView;

    //加载控件
    private LoadingView loadingView;

    //文件点击监听接口
    private ApplicationAdapter.FileClickListener fileClickListenerObj;

    //文件管理适配对象
    private ApplicationAdapter adapterObj;

    //文件排列顺序
    private int fileSortOrder = FileManageConfig.FileSort.SORT_NO;

    //应用显示类型
    private int applicationType = ApplicationType.USER_TYPE;

    //Activity对象
    private Activity activity;

    //文件加载监听接口
    private OnFileLoadListener onFileLoadListener;

    //文件打开监听接口
    private OnFileOpenListener onFileOpenListener;

    //文件信息对象列表
    private ArrayList<FileInfo> fileInfoObjList = new ArrayList<>();

    public ApplicationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    /**
     * 初始化
     *
     * @param activity Activity对象
     */
    public void init(Activity activity) {
        this.activity = activity;

        this.initData();

        LinearLayoutManager layoutManager;

        //是否需要屏幕适配
        if (this.isNeedScreenAdaptation()) {
            layoutManager = new GridLayoutManager(this.getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }

        this.applicationView.setLayoutManager(layoutManager);
        this.applicationView.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        this.applicationView.setAdapter(this.adapterObj);
        this.setOnSearchListener();
        ((SimpleItemAnimator) Objects.requireNonNull(this.applicationView.getItemAnimator())).setSupportsChangeAnimations(false);

        this.showLoading();
        //生成并显示应用列表
        this.showApplicationList();
    }

    /**
     * 刷新列表
     */
    public void update() {
        this.clear();
        this.showLoading();
        this.showApplicationList();
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

        if (selectMode == ApplicationAdapter.SelectMode.SELECT_CLOSE) {
            return;
        }

        for (int i = 0; i < fileInfoObjList.size(); i++) {
            FileInfo fileInfo = fileInfoObjList.get(i);

            if (fileInfo.getFileType().equals(FileInfo.FileType.TYPE_FOLDER)) {
                if (selectMode == ApplicationAdapter.SelectMode.SELECT_ALL || selectMode == ApplicationAdapter.SelectMode.SELECT_FOLDER) {
                    this.adapterObj.addSelectFile(fileInfo);
                }
            } else {
                if (selectMode == ApplicationAdapter.SelectMode.SELECT_ALL || selectMode == ApplicationAdapter.SelectMode.SELECT_FILE) {
                    this.adapterObj.addSelectFile(fileInfo);
                }
            }
        }
    }

    /**
     * 取消选择
     */
    public void cancelSelect() {
        if (this.getSelectMode() == ApplicationAdapter.SelectMode.SELECT_CLOSE) {
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

        if (selectMode == ApplicationAdapter.SelectMode.SELECT_CLOSE) {
            return;
        }

        if (selectMode == ApplicationAdapter.SelectMode.SELECT_ALL || selectMode == ApplicationAdapter.SelectMode.SELECT_FILE) {
            //新增选中文件
            this.adapterObj.addSelectFile(fileInfo);
        }
    }

    /**
     * 释放
     */
    public void recycle() {
        this.fileInfoObjList.clear();
        this.fileClickListenerObj = null;
        this.onFileLoadListener = null;
        this.onFileOpenListener = null;
    }

    /**
     * 清空列表
     */
    public void clear() {
        this.fileInfoObjList.clear();
        this.adapterObj.setFileInfoList(this.fileInfoObjList);
        //刷新适配器视图
        this.notifyDataSetChanged();
    }

    /**
     * 生成并显示应用列表
     */
    private void showApplicationList() {
        new Thread(() -> {
            //生成应用对象列表
            this.creatApplicationObjList();

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
     * 生成应用对象列表
     */
    private void creatApplicationObjList() {
        PackageManager pm = this.activity.getPackageManager();
        List<PackageInfo> appList = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);

        for (PackageInfo packageInfo : appList) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户应用
                switch (applicationType) {
                    case ApplicationType.ALL_TYPE:
                    case ApplicationType.USER_TYPE:
                        String packageName = packageInfo.packageName;
                        File file = new File(packageInfo.applicationInfo.sourceDir);
                        if (!file.exists()) {
                            continue;
                        }

                        ApkInfo apkInfo = new ApkInfo();
                        apkInfo.creatFileInfoObj(file);
                        //获取详细数据
                        apkInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                        apkInfo.setPackageName(packageName);
                        this.fileInfoObjList.add(apkInfo);
                        break;
                }
            } else {
                //系统应用
                switch (applicationType) {
                    case ApplicationType.ALL_TYPE:
                    case ApplicationType.SYSTEM_TYPE:
                        String packageName = packageInfo.packageName;
                        File file = new File(packageInfo.applicationInfo.sourceDir);
                        if (!file.exists()) {
                            continue;
                        }

                        ApkInfo apkInfo = new ApkInfo();
                        apkInfo.creatFileInfoObj(file);
                        //获取详细数据
                        apkInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                        apkInfo.setPackageName(packageName);
                        this.fileInfoObjList.add(apkInfo);
                        break;
                }
            }
        }
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
        this.applicationView = this.findViewById(R.id.rv_file_manage);
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
            this.adapterObj = new ApplicationAdapter(this.getContext());
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
        if (this.getSelectMode() == ApplicationAdapter.SelectMode.SELECT_CLOSE) {
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
        if (selectMode == ApplicationAdapter.SelectMode.SELECT_ALL) {
            this.changeSelectState(fileInfo);
        } else if (selectMode == ApplicationAdapter.SelectMode.SELECT_FILE) {
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
            this.applicationView.setVisibility(GONE);
        });
    }

    /**
     * 显示没有文件提示视图
     */
    private void showNoTips() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(VISIBLE);
        this.applicationView.setVisibility(GONE);

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
        this.applicationView.setVisibility(GONE);

        this.noFileTextView.setText(tips);
        this.noFileTextView.setVisibility(this.isNeedScreenAdaptation() ? GONE : VISIBLE);
    }

    /**
     * 显示文件管理视图
     */
    private void showFileManage() {
        this.loadingView.setVisibility(GONE);
        this.noFileTipsView.setVisibility(GONE);
        this.applicationView.setVisibility(VISIBLE);
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
            this.applicationView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
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

    public ApplicationAdapter.FileClickListener getFileClickListenerObj() {
        return fileClickListenerObj;
    }

    public void setFileClickListenerObj(ApplicationAdapter.FileClickListener fileClickListenerObj) {
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

    public int getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }
}