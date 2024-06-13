package com.next.view.file.type;

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

import com.next.view.file.R;
import com.next.view.file.info.FileInfo;
import com.next.view.file.manage.FileManageAdapter;
import com.next.view.file.tool.DeviceTool;
import com.next.view.file.type.tool.GetTypeListTool;
import com.next.view.loading.LoadingView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * ClassName:文件类型控件类
 *
 * @author Afton
 * @time 2023/8/22
 * @auditor
 */
public class FileTypeView extends LinearLayout {

    //文件加载监听接口
    public interface OnFileLoadListener {

        /**
         * 文件加载完成
         *
         * @param list 文件信息对象列表
         */
        void onLoadComplete(ArrayList<FileInfo> list);
    }

    //文件类型列表控件
    private RecyclerView fileTypeView;

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

    //获取类型列表工具对象
    private GetTypeListTool getTypeListTool;

    //选择模式
    private int selectMode = GetTypeListTool.SelectMode.SELECT_CLOSE;

    //是否正在加载
    private boolean isLoading = false;

    public FileTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
     * @param fileType 文件类型
     */
    public void loadList(String fileType) {
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
                FileTypeView.this.showLoading();

                new Thread(() -> {
                    ArrayList<FileInfo> fileInfoObjList = FileTypeView.this.getTypeListTool.getFileInfoList(fileType, FileTypeView.this.selectMode);
                    FileTypeView.this.adapterObj.setFileInfoList(fileInfoObjList);
                    FileTypeView.this.mainHandler.post(() -> {
                        //关闭加载
                        FileTypeView.this.closeLoading();
                        FileTypeView.this.adapterObj.notifyDataSetChanged();
                        FileTypeView.this.sendLoadComplete();
                    });
                }).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.fileTypeView.startAnimation(alphaAnimation);
    }

    /**
     * 刷新列表
     */
    public void refreshList() {
        String fileType = this.getTypeListTool.getNowFileType();
        this.loadList(fileType);
    }

    /**
     * 设置文件选择类型
     *
     * @param isSelect 是否选择
     * @param fileInfo 文件信息对象
     */
    public void setItemSelectType(boolean isSelect, FileInfo fileInfo) {
        if (this.selectMode == GetTypeListTool.SelectMode.SELECT_CLOSE) {
            return;
        }

        if (this.selectMode == GetTypeListTool.SelectMode.SELECT_FILE && fileInfo.isDirectory()) {
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
            //设置文件选择类型
            this.setItemSelectType(true, fileInfo);
        }
    }

    /**
     * 取消全选
     */
    public void unSelectAll() {
        ArrayList<FileInfo> list = this.adapterObj.getFileInfoList();
        for (FileInfo fileInfo : list) {
            //设置文件选择类型
            this.setItemSelectType(false, fileInfo);
        }
    }

    /**
     * 关闭选择模式
     */
    public void closeSelect() {
        if (this.selectMode == GetTypeListTool.SelectMode.SELECT_CLOSE) {
            return;
        }

        //设置选择模式
        this.selectMode = GetTypeListTool.SelectMode.SELECT_CLOSE;
        ArrayList<FileInfo> fileInfoList = this.adapterObj.getFileInfoList();
        this.getTypeListTool.setItemSelectMode(fileInfoList, this.selectMode);
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
     * 获取当前文件类型
     *
     * @return 文件类型
     */
    public String getNowFileType() {
        return this.getTypeListTool.getNowFileType();
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
     * 显示列表动画
     */
    private void showListAnim() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, this.fileTypeView.getHeight(), 0);
        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(PathInterpolatorCompat.create(0f, 1f, 0f, 1f));
        this.fileTypeView.startAnimation(translateAnimation);
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
        this.fileTypeView = this.findViewById(R.id.rv_file_manage);
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
        this.getTypeListTool = new GetTypeListTool();
        //初始化文件管理适配器对象
        this.adapterObj = new FileManageAdapter(this.getContext());
        //添加文件点击监听
        this.addFileClickListener(new FileManageAdapter.FileClickListener() {
            @Override
            public void onClick(FileInfo fileInfo) {
                FileTypeView.this.itemClick(fileInfo);
            }

            @Override
            public void onLongClick(FileInfo fileInfo) {
                FileTypeView.this.itemLongClick(fileInfo);
            }
        });

        this.fileTypeView.setLayoutManager(this.isNeedScreenAdaptation() ? new GridLayoutManager(this.getContext(), 2) : new LinearLayoutManager(getContext()));
        this.fileTypeView.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        this.fileTypeView.setAdapter(this.adapterObj);
        ((SimpleItemAnimator) Objects.requireNonNull(this.fileTypeView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    /**
     * 文件点击事件
     *
     * @param fileInfo 文件信息对象
     */
    private void itemClick(FileInfo fileInfo) {
        if (this.selectMode != GetTypeListTool.SelectMode.SELECT_CLOSE) {
            //设置文件选择类型
            this.setItemSelectType(fileInfo.getSelectType() != FileInfo.SelectType.SELECT_TYPE_SELECT, fileInfo);
        }
    }

    /**
     * 文件长按事件
     *
     * @param fileInfo 文件信息对象
     */
    private void itemLongClick(FileInfo fileInfo) {
        if (this.selectMode == GetTypeListTool.SelectMode.SELECT_CLOSE && !fileInfo.isDirectory()) {
            //设置选择模式
            this.selectMode = GetTypeListTool.SelectMode.SELECT_FILE;
            ArrayList<FileInfo> fileInfoList = this.adapterObj.getFileInfoList();
            this.getTypeListTool.setItemSelectMode(fileInfoList, this.selectMode);
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

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public OnFileLoadListener getOnFileLoadListener() {
        return onFileLoadListener;
    }

    public void setOnFileLoadListener(OnFileLoadListener onFileLoadListener) {
        this.onFileLoadListener = onFileLoadListener;
    }
}