package com.lear.game2048.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lear.game2048.adapter.BaseAdapter.OnRecyclerItemClickListener;
import com.lear.game2048.model.GameTypeModel;

import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private OnRecyclerItemClickListener<T, VH> mOnClickListener = null;
    private OnRecyclerItemLongClickListener<T, VH> mOnRecyclerItemLongClickListener = null;
    private RecyclerView mRecyclerView;

    public abstract T getItem(int position);

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<T, VH> listener) {
        this.mOnClickListener = listener;
    }

    public OnRecyclerItemClickListener<T, VH> getOnRecyclerItemClickListener() {
        return mOnClickListener;
    }

    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener<T, VH> onRecyclerItemLongClickListener) {
        mOnRecyclerItemLongClickListener = onRecyclerItemLongClickListener;
    }

    public OnRecyclerItemLongClickListener<T, VH> getOnRecyclerItemLongClickListener() {
        return mOnRecyclerItemLongClickListener;
    }

    /**
     * RecyclerItem点击监听器
     *
     * @param <T>  数据类型
     * @param <VH> ViewHolder
     */
    public interface OnRecyclerItemClickListener<T, VH extends RecyclerView.ViewHolder> {

        /**
         * item点击事件监听器
         *
         * @param recyclerView RecyclerView
         * @param adapter      适配器
         * @param view         itemView
         * @param position     位置
         */
        void onRecyclerItemClickListener(RecyclerView recyclerView, BaseAdapter<T, VH> adapter, View view, int position);
    }

    /**
     * RecyclerItem长按监听器
     *
     * @param <T>  数据类型
     * @param <VH> ViewHolder
     */
    public interface OnRecyclerItemLongClickListener<T, VH extends RecyclerView.ViewHolder> {
        /**
         * item长按事件监听器
         *
         * @param recyclerView RecyclerView
         * @param adapter      适配器
         * @param view         itemView
         * @param position     位置
         * @return 如果消费事件，则返回true
         */
        boolean onRecyclerItemLongClickListener(RecyclerView recyclerView, BaseAdapter<T, VH> adapter, View view, int position);
    }

}
