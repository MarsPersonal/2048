package com.lear.game2048.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * author: song
 * created on : 2020/8/26 21:55
 * description: 添加item基类
 */
public abstract class BaseAddItemView extends FrameLayout implements IAddItem {

    private OnRemoveClickListener mOnRemoveClickListener = null;
    private OnClickLoadingImageListener mOnClickLoadingImageListener = null;

    public BaseAddItemView(@NonNull Context context) {
        super(context);
    }

    public BaseAddItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseAddItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseAddItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public abstract void setLevel(int level);

    @Override
    public abstract int getLevel();

    @Override
    public void setBitmap(Bitmap bitmap) {

    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public abstract void hideRemove();

    @Override
    public OnRemoveClickListener getOnRemoveListener() {
        return mOnRemoveClickListener;
    }

    @Override
    public void setOnClickLoadingImageListener(OnClickLoadingImageListener listener) {
        mOnClickLoadingImageListener = listener;
    }

    @Override
    public OnClickLoadingImageListener getOnClickLoadingImageListener() {
        return mOnClickLoadingImageListener;
    }

    @Override
    public void setOnRemoveListener(OnRemoveClickListener listener) {
        mOnRemoveClickListener = listener;
    }
}
