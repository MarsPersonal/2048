package com.lear.game2048.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.lear.game2048.R;

/**
 * author: song
 * created on : 2020/8/19 0:31
 * description: 显示等级图片，提示用
 */
public class LevelImageView extends View {

    private static Drawable mNoBitmap = null;

    private Bitmap mBitmap = null;
    private Rect mShowSize;
    private Rect mClip;
    private Paint mPaint;

    public LevelImageView(Context context) {
        this(context, null);
    }

    public LevelImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LevelImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化
     *
     * @param context      上下文
     * @param attrs        属性集
     * @param defStyleAttr 默认属性集
     * @param defStyleRes  默认资源
     */
    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mShowSize = new Rect();
        mClip = new Rect();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        if (mNoBitmap == null) {
            mNoBitmap = ResourcesCompat.getDrawable(getResources(), R.drawable.click_load_bitmap, null);
        }

    }

    /**
     * 设置显示图片
     *
     * @param bitmap 图片
     */
    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mClip.left = mClip.top = 0;
        mClip.right = mBitmap.getWidth();
        mClip.bottom = mBitmap.getHeight();
        postInvalidate();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * 设置截取位置
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setClip(int left, int top, int right, int bottom) {
        mClip.left = Math.max(left, 0);
        mClip.top = Math.max(top, 0);
        mClip.right = Math.max(left, right);
        mClip.bottom = Math.max(top, bottom);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getBackground() != null) getBackground().draw(canvas);

        mShowSize.left = getPaddingLeft();
        mShowSize.top = getPaddingTop();
        mShowSize.right = getWidth() - getPaddingRight();
        mShowSize.bottom = getHeight() - getPaddingBottom();

        if (mBitmap != null) {
            mClip.right = Math.min(mBitmap.getWidth(), mClip.right);
            mClip.bottom = Math.min(mBitmap.getHeight(), mClip.bottom);
            canvas.drawBitmap(mBitmap, mClip, mShowSize, mPaint);
        } else {
            canvas.save();
            mNoBitmap.setBounds(0, 0, mShowSize.width(), mShowSize.height());
            canvas.translate(mShowSize.left, mShowSize.top);
            mNoBitmap.draw(canvas);
            canvas.restore();
        }


    }
}
