package com.next.view.file.path;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.next.view.file.R;

/**
 * ClassName:文件路径列表控件类
 *
 * @author Afton
 * @time 2023/7/18
 * @auditor
 */
public class FilePathListView extends RecyclerView {

    //文件路径列表适配对象
    private FilePathListAdapter adapterObj;

    public FilePathListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    /**
     * 设置当前路径
     *
     * @param nowPath 路径
     */
    public void setNowPath(String nowPath) {
        this.adapterObj.setNowPath(nowPath);
        //滚动到最后一项
        this.scrollToPosition(this.adapterObj.size() - 1);
    }

    /**
     * 获取当前路径
     *
     * @return 路径
     */
    public String getNowPath() {
        return adapterObj.getNowPath();
    }

    /**
     * 设置路径点击回调接口
     *
     * @param filePathClickListener 路径点击回调接口
     */
    public void setCallBack(FilePathListAdapter.FilePathClickListener filePathClickListener) {
        this.adapterObj.setFilePathClickListener(filePathClickListener);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        this.adapterObj = new FilePathListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.setLayoutManager(layoutManager);
        this.addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildLayoutPosition(view) == adapterObj.size() - 1) {
                    outRect.right = 0;
                } else {
                    outRect.right = (int) getContext().getResources().getDimension(R.dimen.dp_5);
                }
            }
        });
        this.setAdapter(this.adapterObj);
    }
}