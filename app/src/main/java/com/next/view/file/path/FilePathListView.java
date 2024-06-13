package com.next.view.file.path;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.animation.PathInterpolatorCompat;

import com.next.module.file2.tool.FilePathTool;
import com.next.view.anim.AnimButton;
import com.next.view.file.R;

import java.io.File;
import java.util.ArrayList;

/**
 * ClassName:文件路径列表控件类
 *
 * @author Afton
 * @time 2023/7/18
 * @auditor
 */
public class FilePathListView extends LinearLayout {

    //路径点击监听接口
    public interface OnPathClickListener {

        /**
         * 路径按钮点击事件
         *
         * @param path 路径
         */
        void onClick(String path);

        /**
         * 路径按钮长按事件
         *
         * @param path 路径
         */
        void onLongClick(String path);
    }

    //路径布局
    private LinearLayout pathLayout;

    //路径列表
    private ArrayList<String> pathList = new ArrayList<>();

    //路径视图列表
    private ArrayList<View> pathViewList = new ArrayList<>();

    //当前路径
    private String nowPath = FilePathTool.ROOT_PATH;

    //路径点击监听接口
    private OnPathClickListener onPathClickListener;

    public FilePathListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    /**
     * 设置当前路径
     *
     * @param targetPath 目标路径
     */
    public void setNowPath(String targetPath) {
        if (!targetPath.contains(FilePathTool.ROOT_PATH)) {
            return;
        }

        if (this.nowPath.equals(targetPath)) {
            return;
        }

        if (this.isChildPath(targetPath)) {
            this.setChildPath(targetPath);
        } else if (this.isParentPath(targetPath)) {
            this.setParentPath(targetPath);
        } else {
            //如果既不是子路径也不是父路径，则返回根路径
            this.setNowPath(FilePathTool.ROOT_PATH);
            //然后再设置目标路径
            this.setNowPath(targetPath);
        }

        this.nowPath = targetPath;
    }

    /**
     * 获取当前路径
     *
     * @return 路径
     */
    public String getNowPath() {
        return this.nowPath;
    }

    /**
     * 设置路径点击监听接口
     *
     * @param onPathClickListener 路径点击监听接口
     */
    public void setOnPathClickListener(OnPathClickListener onPathClickListener) {
        this.onPathClickListener = onPathClickListener;
    }

    /**
     * 设置子路径
     *
     * @param targetPath 目标路径
     */
    private void setChildPath(String targetPath) {
        ArrayList<String> waitAddPathNameList = new ArrayList<>();
        for (String nowPath = targetPath; !nowPath.equals(this.nowPath); nowPath = FilePathTool.getParentPath(nowPath)) {
            waitAddPathNameList.add(0, new File(nowPath).getName());
        }

        for (String name : waitAddPathNameList) {
            this.addPathItem(name);
        }
    }

    /**
     * 设置父路径
     *
     * @param targetPath 目标路径
     */
    private void setParentPath(String targetPath) {
        int waitRemovePathCount = 0;
        for (String nowPath = this.nowPath; !nowPath.equals(targetPath); nowPath = FilePathTool.getParentPath(nowPath)) {
            waitRemovePathCount++;
        }

        for (int i = 0; i < waitRemovePathCount; i++) {
            this.removePathItem(this.pathList.size() - 1);
        }
    }

    /**
     * 是否是子路径
     *
     * @param targetPath 目标路径
     * @return 是否是子路径
     */
    private boolean isChildPath(String targetPath) {
        //如果当前路径跟目标路径不同且目标路径包含当前路径
        return !this.nowPath.equals(targetPath) && targetPath.contains(this.nowPath);
    }

    /**
     * 是否是父路径
     *
     * @param targetPath 目标路径
     * @return 是否是父路径
     */
    private boolean isParentPath(String targetPath) {
        //如果当前路径跟目标路径不同且当前路径包含目标路径
        return !this.nowPath.equals(targetPath) && this.nowPath.contains(targetPath);
    }

    /**
     * 添加路径项
     *
     * @param folderName 路径名称
     */
    private void addPathItem(String folderName) {
        int index = this.pathLayout.getChildCount();
        View itemView = View.inflate(this.getContext(), R.layout.next_item_path, null);
        AnimButton animButton = itemView.findViewById(R.id.item_path);
        animButton.setText(folderName);
        animButton.setOnClickListener(view -> {
            if (this.onPathClickListener != null) {
                this.onPathClickListener.onClick(this.getPath(index));
            }
        });

        animButton.setOnLongClickListener(view -> {
            if (this.onPathClickListener != null) {
                this.onPathClickListener.onLongClick(this.getPath(index));
            }
            return true;
        });

        this.pathList.add(folderName);
        this.pathViewList.add(itemView);
        this.pathLayout.addView(itemView, index, this.getItemLayoutParams());

        //滚动到最右侧
        this.scrollToRight();

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(200);
        animationSet.setInterpolator(PathInterpolatorCompat.create(0f, 0.6f, 0f, 1f));
        animationSet.addAnimation(new AlphaAnimation(0f, 1f));
        animationSet.addAnimation(new ScaleAnimation(0.8f, 1f, 0.8f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        itemView.startAnimation(animationSet);
    }

    /**
     * 获取子布局参数
     *
     * @return 布局参数
     */
    private LinearLayout.LayoutParams getItemLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) this.getContext().getResources().getDimension(R.dimen.dp_34));
        return layoutParams;
    }

    /**
     * 滚动到最右侧
     */
    private void scrollToRight() {
        new Handler().post(() -> {
            //滚动到最右侧
            ((HorizontalScrollView) this.pathLayout.getParent()).fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        });
    }

    /**
     * 移除路径项
     *
     * @param index 路径索引
     */
    private void removePathItem(int index) {
        if (index >= this.pathList.size()) {
            return;
        }

        if (index == 0) {
            return;
        }

        View itemView = this.pathViewList.get(index);
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        FilePathListView.this.pathList.remove(index);
        FilePathListView.this.pathViewList.remove(index);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(200);
        animationSet.setFillAfter(true);
        animationSet.setInterpolator(PathInterpolatorCompat.create(0f, 0.6f, 0f, 1f));
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(() -> {
                    //显示隐藏动画
                    FilePathListView.this.showHideViewAnim(itemView);
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.addAnimation(new AlphaAnimation(1f, 0f));
        animationSet.addAnimation(new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        itemView.startAnimation(animationSet);
    }

    /**
     * 显示隐藏动画
     *
     * @param view 视图对象
     */
    private void showHideViewAnim(View view) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(view.getWidth(), 0);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(PathInterpolatorCompat.create(0f, 0.6f, 0f, 1f));
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            view.getLayoutParams().width = (int) valueAnimator1.getAnimatedValue();
            view.requestLayout();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                FilePathListView.this.pathLayout.removeView(view);
            }
        });
        valueAnimator.start();
    }

    /**
     * 获取路径
     *
     * @param index 路径索引
     * @return 路径
     */
    private String getPath(int index) {
        if (index >= this.pathList.size() - 1) {
            return this.nowPath;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <= index; i++) {
            if (i == 0) {
                stringBuilder.append(FilePathTool.ROOT_PATH);
            } else {
                stringBuilder.append(File.separator);
                stringBuilder.append(this.pathList.get(i));
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        View view = View.inflate(this.getContext(), R.layout.next_view_path, this);
        this.pathLayout = view.findViewById(R.id.layout_path);
        this.addPathItem(this.getContext().getString(R.string.file_path_list_root_path));
    }
}