package com.lear.game2048.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lear.game2048.R;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.utils.DisplayUtils;
import com.lear.game2048.utils.DrawableUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectGameAdapter extends BaseAdapter<GameTypeModel, SelectGameAdapter.SelectAdapterViewHolder> {
    private Context mContext;
    private List<GameTypeModel> mList;

    public SelectGameAdapter(Context context, List<GameTypeModel> list) {
        this.mContext = context;
        this.mList = list;

    }

    @NonNull
    @Override
    public SelectAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_select_game, parent, false);
        int width = DisplayUtils.getWidth(mContext);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        SelectAdapterViewHolder holder = new SelectAdapterViewHolder(view);

        //设置监听
        view.setOnClickListener(v -> {
            if (getOnRecyclerItemClickListener() != null)
                getOnRecyclerItemClickListener().onRecyclerItemClickListener(getRecyclerView(), SelectGameAdapter.this, view, holder.getAdapterPosition());
        });

        view.setOnLongClickListener(v -> {
            if (getOnRecyclerItemLongClickListener() != null)
                return getOnRecyclerItemLongClickListener()
                        .onRecyclerItemLongClickListener(getRecyclerView(), SelectGameAdapter.this, view, holder.getAdapterPosition());
            return false;
        });

        //设置大小
        params.width = DisplayUtils.scale(width, 0.6f);
        params.setMarginStart((width / 2) - (params.width / 2));


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectAdapterViewHolder holder, int position) {
        holder.setText(getItem(position).getName());
        //动态设置背景色
        GradientDrawable drawable = (GradientDrawable) holder.textView.getBackground();
        DrawableUtils.setColor(drawable, DrawableUtils.getItemBgColor(position));
    }

    @Override
    public int getItemCount() {
        return mList != null && mList.size() != 0 ? mList.size() : 0;
    }

    @Override
    public GameTypeModel getItem(int position) {
        return mList.get(position);
    }

    /**
     * 设置数据并修改
     *
     * @param list list
     */
    public void setData(List<GameTypeModel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    /**
     * 辅助类
     */
    public static class SelectAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.select_game_item)
        TextView textView;

        SelectAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * 设置文本
         *
         * @param str 文本
         */
        void setText(String str) {
            textView.setText(str);


        }

    }
}
