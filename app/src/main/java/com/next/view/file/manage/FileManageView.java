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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.next.module.file2.tool.FileListFactory;
import com.next.module.file2.tool.FileLoadException;
import com.next.view.file.R;
import com.next.view.file.info.FileInfo;
import com.next.view.file.tool.DeviceTool;
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

    //选择模式
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
        this.nowPath = path;

        new Thread(() -> {
            try {
                FileListFactory.FileListInfo fileListInfo = this.factory.getFileList(path);
                this.parentFile = fileListInfo.getParentFile();
                ArrayList<FileInfo> fileInfoObjList = this.file2ListToFileInfoList(fileListInfo.getChildFileList());
                this.adapterObj.setFileInfoList(fileInfoObjList);
                this.mainHandler.post(() -> {
                    this.adapterObj.notifyDataSetChanged();
                    this.sendLoadComplete();
                });
            } catch (FileLoadException e) {
                this.mainHandler.post(() -> {
                    this.sendLoadError(e);
                });
            }
        }).start();
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
        this.adapterObj.setFileClickListener(new FileManageAdapter.FileClickListener() {
            @Override
            public void onClick(FileInfo fileInfo) {

            }

            @Override
            public void onLongClick(FileInfo fileInfo) {

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
}