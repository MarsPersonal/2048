package com.lear.game2048.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lear.game2048.R;
import com.lear.game2048.utils.DrawableUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 记分板
 */
public class ScoreBoardView extends FrameLayout {

    public static final String TAG = "ScoreBoardView";

    @BindView(R.id.board_title)
    TextView mTitleView;
    @BindView(R.id.board_score)
    TextView mScoreView;
    @BindView(R.id.board)
    FrameLayout mBoard;

    private int mScore;

    public ScoreBoardView(@NonNull Context context) {
        this(context, null);
    }

    public ScoreBoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreBoardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void init(Context context) {
        View v = inflate(context, R.layout.view_score_board_layout, this);
        ButterKnife.bind(this, v);
    }

    /**
     * 初始化Attrs
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            setBgColor(Color.BLACK);
        } else {
            TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.ScoreBoardView);

            mTitleView.setText(type.getString(R.styleable.ScoreBoardView_title));
            mScoreView.setText(type.getString(R.styleable.ScoreBoardView_score));

            setBgColor(type.getColor(R.styleable.ScoreBoardView_bgcolor, Color.BLACK));

            //资源回收
            type.recycle();
        }
    }

    /**
     * 设置标题
     *
     * @param titleView 标题
     */
    public void setTitle(String titleView) {
        mTitleView.setText(titleView);
    }

    /**
     * 设置分数
     *
     * @param score 分数
     */
    public void setScore(int score) {
        mScore = score;
        mScoreView.setText(String.valueOf(score));
    }

    /**
     * 获取分数
     *
     * @return 分数
     */
    public int getScore() {
        return mScore;
    }

    /**
     * 设置背景颜色
     *
     * @param color 颜色
     */
    public void setBgColor(int color) {
        GradientDrawable drawable = (GradientDrawable) mBoard.getBackground();
        DrawableUtils.setColor(drawable, color);
    }

}
