package com.lear.game2048.view;

import android.graphics.Bitmap;

/**
 * author: song
 * created on : 2020/8/26 21:52
 * description: 添加item接口
 */
public interface IAddItem {

    /**
     * 设置等级
     *
     * @param level 等级
     */
    void setLevel(int level);

    /**
     * 获取等级
     *
     * @return level
     */
    int getLevel();

    /**
     * 设置bitmap
     *
     * @param bitmap 位图
     */
    void setBitmap(Bitmap bitmap);

    /**
     * 获取bitmap
     *
     * @return bitmap
     */
    Bitmap getBitmap();

    /**
     * 隐藏移除按钮
     */
    void hideRemove();

    /**
     * 设置移除点击监听器
     *
     * @param listener 监听器
     */
    void setOnRemoveListener(OnRemoveClickListener listener);

    /**
     * 获取监听器
     *
     * @return OnRemoveClickListener
     */
    OnRemoveClickListener getOnRemoveListener();

    /**
     * 设置点击加载图片监听器
     * @param listener 监听器
     */
    void setOnClickLoadingImageListener(OnClickLoadingImageListener listener);

    /**
     * 获取点击加载图片监听器
     * @return 点击加载图片监听器
     */
    OnClickLoadingImageListener getOnClickLoadingImageListener();

    /**
     * 移除按钮点击监听器
     */
    interface OnRemoveClickListener {

        /**
         * 移除按钮点击监听器
         *
         * @param view  this
         * @param level 等级
         */
        void onRemoveClick(IAddItem view, int level);

    }

    /**
     * 点击加载图片监听器
     */
    interface OnClickLoadingImageListener {

        /**
         * 点击图片加载监听器
         *
         * @param view 选择图片后将图片加载到的view
         */
        void onClickLoading(IAddItem view);
    }


}
