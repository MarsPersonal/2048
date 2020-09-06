package com.lear.game2048.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lear.game2048.R;
import com.lear.game2048.model.GameBlockUnit;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.service.MusicService;
import com.lear.game2048.utils.DrawableUtils;
import com.lear.game2048.utils.MathCalculationUtils;
import com.lear.game2048.utils.SharedPreferencesUtils;
import com.lear.game2048.view_model.GameDataModel;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GameDrawView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static final String TAG = "GameDrawView";

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private RectF mBgRect, mBlockRect;      //背景与块
    private Rect mTextBlock;        //字体块
    private Rect[] mClipImgBlocks;         //裁剪图片块组
    private int mRound;             //圆角
    private int mBgRound;           //背景圆角

    private boolean isRunning;      //是否在运行
    private boolean isDrawing;      //是否在绘图
    private long mRunTime;          //运行时间
    private Executor mThreadPool;   //线程池

    private boolean isCreated = false;      //创建完成
    private boolean isStart = false;        //是否已启动
    private boolean isConfigured = false;   //是否配置游戏类型
    private boolean isHidden = false;       //是否处于隐藏状态

    private GameTypeModel mGameType = null;        //游戏类型
    private GameDataModel mGameModel = null;       //数据模型
    private volatile GameBlockUnit[][] mBlockArrays; //块单元数组

    //显示内容
    private boolean isVersion_N;                 //版本号是否大于24
    private boolean isText;             //是否为文本模式
    private String[] mLevelStrings;     //等级显示文本,或者图片坐标
    private Bitmap mLevelImage;         //等级显示图片
    private int[] mBlockBgColor;        //块背景颜色
    private int[] mFontSizeArray = null;        //字体大小
    //监听器
    private OnSlideEndListener mListener = null;

    //是否播放音效
    private boolean isSoundEffect;
    //是否已保存
    private boolean isLoad;
    //广播用
    private Intent mBroadcastIntent;

    public GameDrawView(Context context) {
        this(context, null);
    }

    public GameDrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        initBlock();

        //绘制背景
        try {
            mCanvas = mHolder.lockCanvas();
            drawBg(mCanvas);
        } finally {
            mHolder.unlockCanvasAndPost(mCanvas);
        }

        if (!isCreated) isCreated = true;
        if (isConfigured) setGameType(mGameType, mLevelImage);
        if (isStart) start();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        this.removeCallbacks(this);
        isRunning = false;
    }

    @Override
    public void run() {
        long startTime, endTime, time;
        //是否在运行, 且是否需要绘图, 最后判断Fragment否处于隐藏状态
        while (isRunning && isDrawing && !isHidden) {
            startTime = System.currentTimeMillis();
            draw();

            if (mGameModel.isEnd()) {
                isDrawing = false;
                isRunning = mGameModel.getGameResult() == GameDataModel.GAME_STATUS_CONTINUE;
                new EndAsync(this, mGameModel).execute(mListener);
            }
            //开始时间与结束时间
            endTime = System.currentTimeMillis();
            time = endTime - startTime;
            if (time < mRunTime) {
//                Log.i(TAG, "run: time=" + time);
                try {
                    Thread.sleep(mRunTime - time);
                } catch (InterruptedException ignored) {
                }
//            } else {
//                Log.e(TAG, "run: time=" + time);
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {


        isVersion_N = Build.VERSION.SDK_INT > Build.VERSION_CODES.N;

        //多线程
        mThreadPool = Executors.newSingleThreadExecutor();
        mRunTime = SharedPreferencesUtils.getInstance().getHighFrameRateModeSwitch(getContext()) ? 16L : 33L;
        //添加回调
        mHolder = getHolder();
        mHolder.addCallback(this);

        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.rgb(170, 170, 170));
        mPaint.setStrokeWidth(2f);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.CENTER);    //设置文本居中（x轴）

        /*设置背景透明*/
        /*背景透明，但会导致Fragment被隐藏时仍然显示在最上面，这个问题在android7.0上出现*/
        /*因此要判断版本号是否大于24*/
        if (isVersion_N) {
            setZOrderOnTop(true);
        }
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        //块背景色
        mBlockBgColor = DrawableUtils.getBgColor(getContext());

        //圆角
        mRound = (int) (getContext().getResources().getDisplayMetrics().density * 5 + 0.5f);
        mBgRound = (int) (getContext().getResources().getDisplayMetrics().density * 10 + 0.5f);

        mBroadcastIntent = new Intent(MusicService.MusicPlayReceiver.ACTION_PLAY_BLOCK_MOVE_MUSIC);
        //添加前台广播Flag
        mBroadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

    }

    /**
     * 初始化RectF与Margin
     */
    private void initBlock() {
        //区块与背景
        mBgRect = new RectF();
        mBgRect.left = mBgRect.top = 0;
        mBgRect.right = getWidth();
        mBgRect.bottom = getHeight();

        mBlockRect = new RectF();

    }

    /**
     * 初始化显示内容
     */
    private void initDisplayContent() {
        if (isText) {
            mTextBlock = new Rect();
            mLevelStrings = new String[mGameType.getMaxLevel()];
        } else {
            mClipImgBlocks = new Rect[mGameType.getMaxLevel()];
        }

        JsonObject json = (new Gson()).fromJson(mGameType.getContent(), JsonObject.class);
        int i = 0;
        if (isText) {
            for (String key : json.keySet()) mLevelStrings[i++] = json.get(key).getAsString();
        } else {
            for (String key : json.keySet()) {
                if (key.equals("bitmap")) continue;

                final JsonObject object = new Gson().fromJson(json.getAsJsonPrimitive(key).getAsString(), JsonObject.class);
                final Rect rect = new Rect(object.get("left").getAsInt(), object.get("top").getAsInt(),
                        object.get("right").getAsInt(), object.get("bottom").getAsInt());
                mClipImgBlocks[i++] = rect;
            }
        }

    }

    /**
     * 绘图
     */
    private void draw() {
        //锁定画面并返回画面对象
        mCanvas = mHolder.lockCanvas();
        try {
            if (mCanvas == null) return;

            mBlockArrays = mGameModel.getUpdateData();
            drawBg(mCanvas);
            drawBlock(mCanvas);

        } catch (Exception ignored) {
        } finally {
            if (mCanvas != null) mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    private void drawBg(Canvas canvas) {

        if (!isVersion_N) {
            canvas.drawColor(getResources().getColor(R.color.colorMainBg));
        }

        mPaint.setColor(getResources().getColor(R.color.colorGameBg));
        canvas.drawRoundRect(mBgRect, mBgRound, mBgRound, mPaint);

        if (mGameModel == null) return;
        mPaint.setColor(mBlockBgColor[0]);
        for (GameBlockUnit[] arr : mGameModel.getBgUnit()) {
            for (GameBlockUnit unit : arr) {
                mBlockRect.left = unit.baseX;
                mBlockRect.right = mBlockRect.left + unit.width;
                mBlockRect.top = unit.baseY;
                mBlockRect.bottom = mBlockRect.top + unit.height;
                canvas.drawRoundRect(mBlockRect, mRound, mRound, mPaint);
            }
        }
    }

    /**
     * 绘制块
     *
     * @param canvas 画布
     */
    private void drawBlock(Canvas canvas) {
        for (int i = 0, size = mGameType.getCheckerBoardMax(); i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (mBlockArrays[i][j].status != GameBlockUnit.STATUS_VOID) {

                    setRectBlock(mBlockArrays[i][j]);
                    drawBgBlock(canvas, mBlockArrays[i][j]);


                    if (isText) drawTextBlock(canvas, mBlockArrays[i][j]);
                    else drawImgBlock(canvas, mBlockArrays[i][j]);
                }
            }
        }
    }

    /**
     * 绘制背景块
     *
     * @param canvas 画布
     * @param unit   块单元
     */
    private void drawBgBlock(Canvas canvas, GameBlockUnit unit) {
        mPaint.setColor(mBlockBgColor[unit.level]);
        canvas.drawRoundRect(mBlockRect, mRound, mRound, mPaint);
    }

    /**
     * 绘制文本块
     *
     * @param canvas 画面
     * @param unit   块单元
     */
    private void drawTextBlock(Canvas canvas, GameBlockUnit unit) {
        String text = mLevelStrings[unit.level - 1];
        int fontSize = getFontSize(unit);
        int fontColor = unit.level < 3 ? Color.rgb(100, 100, 100) : Color.WHITE;

        mPaint.setColor(fontColor);
        mPaint.setTextSize(fontSize);
        mPaint.getTextBounds(text, 0, text.length(), mTextBlock);

        int x = (int) (mBlockRect.left + ((int) mBlockRect.width() >> 1));
        int y = (int) (mBlockRect.top + ((int) mBlockRect.height() >> 1) + (mTextBlock.height() >> 1));

        canvas.drawText(text, x, y, mPaint);
    }

    /**
     * 绘制图片块
     *
     * @param canvas 画布
     * @param unit   块单元
     */
    private void drawImgBlock(Canvas canvas, GameBlockUnit unit) {
        final int padding = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);

        mBlockRect.left += padding;
        mBlockRect.top += padding;
        mBlockRect.right -= padding;
        mBlockRect.bottom -= padding;

        canvas.drawBitmap(mLevelImage, mClipImgBlocks[unit.level - 1], mBlockRect, mPaint);

    }

    /**
     * 设置块矩形参数
     *
     * @param unit GameBlockUnit
     */
    private void setRectBlock(GameBlockUnit unit) {
        mBlockRect.left = unit.toX != 0 ? unit.baseX + unit.offset : unit.baseX;
        mBlockRect.top = unit.toY != 0 ? unit.baseY + unit.offset : unit.baseY;
        mBlockRect.right = mBlockRect.left + unit.width;
        mBlockRect.bottom = mBlockRect.top + unit.height;
        if (unit.scale != 1.0f) {
            int widthOverflow = (int) (unit.width * unit.scale - unit.width) >> 1;
            int heightOverflow = (int) (unit.height * unit.scale - unit.height) >> 1;
            mBlockRect.left -= widthOverflow;
            mBlockRect.top -= heightOverflow;
            mBlockRect.right += widthOverflow;
            mBlockRect.bottom += heightOverflow;
        }

    }

    /**
     * 获取字体大小
     *
     * @param unit 单元
     * @return 字体大小
     */
    private int getFontSize(GameBlockUnit unit) {
        //计算所有的字体大小
        if (mFontSizeArray == null) {

            float sd = getResources().getDisplayMetrics().scaledDensity;
            int baseSize = mGameModel.getBgUnit()[0][0].width;//基础大小
            Rect r = new Rect();
            mFontSizeArray = new int[mGameType.getMaxLevel()];

            for (int i = 0; i < mGameType.getMaxLevel(); i++) {
                String temp = mLevelStrings[i];

                //临时大小
                int tempSize;
                if (temp.equals("1")) tempSize = (int) (baseSize * 0.1f + 0.5f);
                else if (temp.length() == 1) tempSize = (int) (baseSize * 0.2f + 0.5f);
                else if (temp.length() == 2) tempSize = (int) (baseSize * 0.35f + 0.5f);
                else if (temp.length() == 3) tempSize = (int) (baseSize * 0.4f + 0.5f);
                else tempSize = (int) (baseSize * 0.5f + 0.5f);

                int tempFontSize = (int) (40 * sd + 0.5f);
                mPaint.setTextSize(tempFontSize);
                mPaint.getTextBounds(temp, 0, temp.length(), r);
                while (r.width() > tempSize) {
                    tempFontSize -= 5;
                    mPaint.setTextSize(tempFontSize);
                    mPaint.getTextBounds(temp, 0, temp.length(), r);
                }

                mFontSizeArray[i] = tempFontSize;
            }
        }

        float fontSize = getResources().getDisplayMetrics().scaledDensity * unit.scale * mFontSizeArray[unit.level - 1];

        return (int) (fontSize + 0.5f);
    }

    /**
     * 设置游戏类型
     *
     * @param gameType 游戏类型
     * @param bitmap   图片，如果游戏类型不为 DISPLAY_IMG 则无效
     *                 如果游戏类型为 DISPLAY_IMG 且bitmap为空则报NullPointerException
     */
    public void setGameType(@NonNull GameTypeModel gameType, @Nullable Bitmap bitmap) {
//        Log.i(TAG, "setGameType: 调用");
        mGameType = gameType;
        mLevelImage = bitmap;
        isConfigured = true;

        Log.i(TAG, "setGameType: size=" + gameType);

        //如果没有创建完成，后面的流程不用走
        if (!isCreated) return;

        //显示内容类型
        isText = mGameType.getDisplayType() == GameTypeModel.DISPLAY_TEXT;
        if (!isText && bitmap == null) throw new NullPointerException();
        initDisplayContent();
        //重置
        reset();
    }

    /**
     * 重置
     */
    public void reset() {
        if (!isLoad) {
            mGameModel = new GameDataModel(mGameType, mRunTime);
            mGameModel.setCanvasSize(getWidth());
        }

        isLoad = false;
        isSoundEffect = SharedPreferencesUtils.getInstance().getSoundEffectSwitch(getContext());
        mRunTime = SharedPreferencesUtils.getInstance().getHighFrameRateModeSwitch(getContext()) ? 16L : 33L;
        mFontSizeArray = null;
    }

    /**
     * 开始游戏
     */
    public void start() {
        if (!isStart) isStart = true;
        if (!isCreated) return;

        isRunning = isDrawing = true;
        mThreadPool.execute(this);
    }

    /**
     * 重新启动
     */
    public void restart() {
        Log.i(TAG, "restart: 调用");
        reset();
        start();
    }

    /**
     * 设置暂停
     * 暂停的是整个View
     */
    public void setPause() {
        isRunning = false;
        mCanvas = mHolder.lockCanvas();
        try {
            drawBg(mCanvas);
        } finally {
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    /**
     * 设置隐藏状态
     *
     * @param state 状态
     */
    public void setHiddenState(boolean state) {
        isHidden = state;
    }

    /**
     * 设置滑动方向
     * 在设置好滑动方向后就应该开始移动块了
     *
     * @param angle 角度
     */
    public void setFlingDirection(float angle) {
        if (!isDrawing) {
            isDrawing = true;
            mGameModel.setDirection(MathCalculationUtils.getDirection(angle));
            mThreadPool.execute(this);

            if (isSoundEffect) getContext().sendBroadcast(mBroadcastIntent);
        }
    }

    /**
     * 保存
     *
     * @return GameDataModel
     */
    public GameDataModel save() {
        return mGameModel;
    }

    /**
     * 加载
     *
     * @param model GameDataModel
     */
    public void load(GameDataModel model) {
        isLoad = true;
        this.mGameModel = model;
    }

    /**
     * 设置监听器
     *
     * @param listener OnSlideEndListener
     */
    public void setOnSlideEndListener(OnSlideEndListener listener) {
        mListener = listener;
    }

    /**
     * 滑动结束监听器
     */
    public interface OnSlideEndListener {

        /**
         * 滑动结束监听器
         *
         * @param view  this
         * @param model 游戏模型
         */
        void onSlideEndListener(GameDrawView view, GameDataModel model);
    }

    /**
     * 异步任务
     */
    private static class EndAsync extends AsyncTask<OnSlideEndListener, Void, OnSlideEndListener> {

        private WeakReference<GameDrawView> mView;
        private GameDataModel mDataModel;

        public EndAsync(GameDrawView view, GameDataModel model) {
            mView = new WeakReference<>(view);
            mDataModel = model;
        }


        @Override
        protected OnSlideEndListener doInBackground(OnSlideEndListener... onSlideEndListeners) {
            return onSlideEndListeners[0];
        }

        @Override
        protected void onPostExecute(OnSlideEndListener onSlideEndListener) {
            if (onSlideEndListener != null && mView.get() != null && mDataModel != null) {
                onSlideEndListener.onSlideEndListener(mView.get(), mDataModel);
            }

        }

    }

}

