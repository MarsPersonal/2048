package com.lear.game2048.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lear.game2048.R;

import java.io.Serializable;

/**
 * author: song
 * created on : 2020/8/26 1:58
 * description:封装添加图片View
 */
public class AddImgItemView extends BaseAddItemView implements View.OnClickListener, Serializable {


    private TextView mLevelText;
    private LevelImageView mImageView;
    private ImageButton mRemoveView;
    private int mLevel;

    public AddImgItemView(Context context) {
        this(context, null);
    }

    public AddImgItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddImgItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AddImgItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化
     *
     * @param context      上下文
     * @param attrs        属性集合
     * @param defStyleAttr 默认样式
     * @param defStyleRes  默认主题
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        View view = inflate(context, R.layout.item_add_level_img, this);

        mLevelText = view.findViewById(R.id.level);
        mImageView = view.findViewById(R.id.level_img);
        mRemoveView = view.findViewById(R.id.remove);

        mRemoveView.setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }

    @Override
    public void setLevel(int level) {
        mLevel = level;
        String str = level + ".";
        mLevelText.setText(str);
    }

    @Override
    public int getLevel() {
        return mLevel;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        mImageView.setBitmap(bitmap);
    }

    @Override
    public Bitmap getBitmap() {
        return mImageView.getBitmap();
    }

    @Override
    public void hideRemove() {
        mRemoveView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.remove && getOnRemoveListener() != null) {
            getOnRemoveListener().onRemoveClick(this, mLevel);
        } else if (v.getId() == R.id.level_img && getOnClickLoadingImageListener() != null) {
            getOnClickLoadingImageListener().onClickLoading(this);
        }
    }
}
