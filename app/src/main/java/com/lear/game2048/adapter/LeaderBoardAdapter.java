package com.lear.game2048.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lear.game2048.R;
import com.lear.game2048.model.GameScoreModel;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderBoardAdapter<T extends GameScoreModel> extends
        BaseAdapter<T, RecyclerView.ViewHolder> {

    public static final String TAG = "LeaderBoardAdapter";
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_DATA = 1;
    private static final int TYPE_LAST = 2;

    private Context mContext;
    private List<T> mList;

    public LeaderBoardAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
        Collections.sort(mList);
        //添加空数据，表示最后一行
        mList.add((T) new GameScoreModel("", "", -1, ""));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_EMPTY)
            return new EmptyDataViewHolder(inflater.inflate(R.layout.item_not_leader_board, parent, false));
        if (viewType == TYPE_LAST)
            return new LastItemViewHolder(inflater.inflate(R.layout.item_bottom, parent, false));

        //正常数据类型
        View view = inflater.inflate(R.layout.item_leader_board_score, parent, false);

        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DATA)
            ((DataViewHolder) holder).setData(getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mList != null ? mList.get(position) : null;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1) return TYPE_EMPTY;
        if (position == getItemCount() - 1) return TYPE_LAST;
        return TYPE_DATA;
    }

    /**
     * 设置数据
     *
     * @param list 数据
     */
    public void setData(List<T> list) {
        mList = list;
        Collections.sort(mList);
        //添加空数据，表示最后一行
        mList.add((T) new GameScoreModel("", "", -1, ""));
        notifyDataSetChanged();
    }

    /**
     * 数据ViewHolder
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.serial_number)
        TextView serialNumber;
        @BindView(R.id.user_name)
        TextView userNameView;

        @BindView(R.id.score)
        TextView scoreView;
        @BindView(R.id.date)
        TextView dateView;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(GameScoreModel data, int position) {
            serialNumber.setText(String.valueOf(position + 1));
            userNameView.setText(data.userName);
            scoreView.setText(String.valueOf(data.score));
            dateView.setText(data.date);

        }
    }

    /**
     * 空数据ViewHolder
     */
    public static class EmptyDataViewHolder extends RecyclerView.ViewHolder {

        public EmptyDataViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * 最后一行ViewHolder
     */
    public static class LastItemViewHolder extends RecyclerView.ViewHolder {

        public LastItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class SpecialDividerItemDecoration extends RecyclerView.ItemDecoration {
        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        public static final String TAG = "MyDividerItemDecoration";

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };


        private Drawable mDivider;

        private int mOrientation;

        public SpecialDividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            if (mDivider == null) {
                Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                        + "DividerItemDecoration. Please set that attribute all call setDrawable()");
            }
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST
                    && orientation != VERTICAL_LIST) {
                try {
                    throw new IllegalAccessException("invalid orientation");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

            if (parent.getLayoutManager() == null || mDivider == null) return;
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }

        }


        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }


    }

}
