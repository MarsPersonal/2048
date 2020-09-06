package com.lear.game2048.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.FileNotFoundException;

/**
 * author: song
 * created on : 2020/8/29 13:48
 * description: 变化图片View，载到图片后
 */
public class VarietyImageView extends View implements View.OnTouchListener, View.OnClickListener, ScaleGestureDetector.OnScaleGestureListener {

    public static final String TAG = "VarietyImageView";
    private Paint mPaint;
    private Rect mClipOut;
    private Bitmap mBitmap;

    private int baseX, baseY;//图片的左上角坐标

    private float minScale;//最小缩放
    private float maxScale;//最大缩放
    private float scale;//当前缩放
    private float preScale;//上一次缩放

    private boolean isFirstLoad;//第一次加载
    private boolean isChangedModel;  //是否处于修改模式
    private boolean isScale;//是否处于缩放模式

    private ScaleGestureDetector mScaleGestureDetector;

    private int moveX, moveY;//平移用基准坐标
    private int touchX, touchY;//按下的位置

    private OnChangedListener mChangedListener = null;

    public VarietyImageView(Context context) {
        this(context, null);
    }

    public VarietyImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VarietyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VarietyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if (mBitmap == null) return;

        if (isFirstLoad) {
            isFirstLoad = false;

            //中间的小框
            int outSize = getWidth() >> 1;
            mClipOut.left = (getWidth() - outSize) >> 1;
            mClipOut.top = (getHeight() - outSize) >> 1;
            mClipOut.right = mClipOut.left + outSize;
            mClipOut.bottom = mClipOut.top + outSize;

            //缩放与显示高度
            minScale = preScale = scale = (float) this.getWidth() / (float) mBitmap.getWidth();
            minScale /= 2;

            baseX = (this.getWidth() - mBitmap.getWidth()) >> 1;
            baseY = (this.getHeight() - mBitmap.getHeight()) >> 1;
        }

        canvas.save();

        canvas.scale(scale, scale, getWidth() >> 1, getHeight() >> 1);
        canvas.drawBitmap(mBitmap, baseX, baseY, mPaint);
        canvas.restore();

        drawClipLayer(canvas);
    }

    /**
     * 绘制截取层
     * 就是最上面那层
     *
     * @param canvas 画布
     */
    private void drawClipLayer(Canvas canvas) {
        canvas.save();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.clipRect(mClipOut, Region.Op.XOR);
        } else {
            canvas.clipOutRect(mClipOut);
        }
        canvas.drawColor(Color.argb(200, 0, 0, 0));
        canvas.restore();

        canvas.save();

        int length = mClipOut.width() / 5;

        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(5);

        canvas.drawLine(mClipOut.left - 3, mClipOut.top - 2,
                mClipOut.left + length, mClipOut.top - 2, mPaint);
        canvas.drawLine(mClipOut.right + 3, mClipOut.top - 2,
                mClipOut.right - length, mClipOut.top - 2, mPaint);

        canvas.drawLine(mClipOut.left - 3, mClipOut.bottom + 2,
                mClipOut.left + length, mClipOut.bottom + 2, mPaint);
        canvas.drawLine(mClipOut.right + 3, mClipOut.bottom + 2,
                mClipOut.right - length, mClipOut.bottom + 2, mPaint);

        canvas.drawLine(mClipOut.left - 2, mClipOut.top - 3,
                mClipOut.left - 2, mClipOut.top + length, mPaint);
        canvas.drawLine(mClipOut.left - 2, mClipOut.bottom + 3,
                mClipOut.left - 2, mClipOut.bottom - length, mPaint);

        canvas.drawLine(mClipOut.right + 2, mClipOut.top - 3,
                mClipOut.right + 2, mClipOut.top + length, mPaint);
        canvas.drawLine(mClipOut.right + 2, mClipOut.bottom + 3,
                mClipOut.right + 2, mClipOut.bottom - length, mPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isChangedModel) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.i(TAG, "onTouch: 按下");

            touchX = moveX = (int) event.getX();
            touchY = moveY = (int) event.getY();

        } else if (event.getPointerCount() > 1) {

            isScale = true;
            return mScaleGestureDetector.onTouchEvent(event);

        } else if (event.getAction() == MotionEvent.ACTION_MOVE && !isScale) {
            translate((int) (event.getX() + 0.5f), (int) (event.getY() + 0.5f));
            return true;

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            int dx = (int) (event.getX() - touchX);
            int dy = (int) (event.getY() - touchY);
            //如果抬起的位置与按下的位置的差在10个像素内，则认为没有移动
            return Math.abs(dx) > 10 || Math.abs(dy) > 10;

        } else if (event.getPointerCount() == 1 && isScale) {

            moveX = (int) event.getX();
            moveY = (int) event.getY();
            isScale = false;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        isChangedModel = !isChangedModel;
        if (mChangedListener != null) mChangedListener.onChanged(this, isChangedModel);
        invalidate();
    }

    /**
     * 初始化
     *
     * @param context      上下文
     * @param attrs        属性集
     * @param defStyleAttr 默认属性集
     * @param defStyleRes  默认主题
     */
    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);

        mClipOut = new Rect();
        maxScale = 2;

        isChangedModel = false;
        isScale = false;
        setOnTouchListener(this);
        setOnClickListener(this);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

    }

    /**
     * 平移
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void translate(int x, int y) {

        //移动距离
        int dx = x - moveX;
        int dy = y - moveY;

        baseX += dx;
        baseY += dy;


        //重设移动位置
        //不重设将会出问题
        moveX = x;
        moveY = y;

        invalidate();
    }

    /**
     * 缩放
     *
     * @param x 缩放中心x坐标
     * @param y 缩放中心y坐标
     * @param s 缩放比例
     */
    private void scale(int x, int y, float s) {

//        Log.i(TAG, "scale: s=" + s + " scale=" + scale);
        scale = preScale + s - 1.0f;

        if (scale < minScale) scale = minScale;
        else if (scale > maxScale) scale = maxScale;

        invalidate();
    }

    /**
     * 加载图片
     *
     * @param uri 图片的uri
     */
    public void load(Uri uri) {
        if (uri == null) return;
        isFirstLoad = true;
        try {
            mBitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "load: 无法加载文件: " + uri.toString(), e);
            mBitmap = null;
        }
        invalidate();
    }

    /**
     * 输出Bitmap
     *
     * @return 如果没有载入图片，则返回null
     */
    public Bitmap outBitmap() {
        if (mBitmap == null) return null;
        //新建Bitmap, 这个Bitmap将会被返回
        Bitmap bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, 150, 150);

        //新建临时Bitmap
        Bitmap temp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(temp);
        draw(tempCanvas);

        //绘制内容
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(temp, mClipOut, rect, mPaint);

        temp.recycle();
        return bitmap;
    }

    public void recycle() {
        mBitmap.recycle();
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setChangedListener(OnChangedListener listener) {
        mChangedListener = listener;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scale((int) detector.getFocusX(), (int) detector.getFocusY(), detector.getScaleFactor());
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        preScale = scale;
    }

    /**
     * 修改监听器，监听状态变化
     */
    public interface OnChangedListener {
        /**
         * 修改监听器
         *
         * @param view     view
         * @param isChange 状态
         */
        void onChanged(VarietyImageView view, boolean isChange);
    }
}


