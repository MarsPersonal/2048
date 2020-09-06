package com.lear.game2048.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lear.game2048.R;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.dialog.PromptDialog;
import com.lear.game2048.dialog.SettlementDialog;
import com.lear.game2048.model.GameDataStorage;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.utils.DisplayUtils;
import com.lear.game2048.utils.DrawableUtils;
import com.lear.game2048.utils.GestureListener;
import com.lear.game2048.utils.Message;
import com.lear.game2048.utils.SharedPreferencesUtils;
import com.lear.game2048.view.GameDrawView;
import com.lear.game2048.view.ScoreBoardView;
import com.lear.game2048.view_model.GameDataModel;

import java.io.Serializable;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GameFragment extends BaseFragment implements Serializable {

    public static final String TAG = "GameFragment";

    private Unbinder mUnbinder;

    private GameTypeModel mGameType;

    @BindView(R.id.game_relative_layout)
    RelativeLayout mRelativeLayout;

    @BindView(R.id.game_score)
    ScoreBoardView mCurrentScore;
    @BindView(R.id.game_max_score)
    ScoreBoardView mMaxScore;

    @BindView(R.id.game_prompt_text)
    TextView mPromptText;

    @BindView(R.id.game_mode_name)
    TextView mModeName;
    @BindView(R.id.game_canvas)
    GameDrawView mGameDrawView;

    private Bitmap mBitmap = null;

    private GestureDetector mGesture;

    private boolean isPause;
    private boolean isSettlementDialogShow;//结算页面是否显示

    private GameDataModel mGameDataModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();

        if (bundle == null) throw new NullPointerException();

        mGameType = (GameTypeModel) bundle.getSerializable(TAG);

    }

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
            isPause = false;
            mGameDrawView.load(mGameDataModel);
            mGameDataModel = null;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        mGameDrawView.setHiddenState(hidden);
        if (hidden) {
            isPause = false;
            mGameDrawView.setPause();
        } else {
            mGameDrawView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        mGameDataModel = mGameDrawView.save();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑
        mUnbinder.unbind();

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    /**
     * 初始化
     *
     * @param view View
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        //绑定ButterKnife
        mUnbinder = ButterKnife.bind(this, view);
        isSettlementDialogShow = false;

        initGameTypeData();

        //初始化游戏View
        initGameDrawView();

        //设置按钮背景色
        Button menu = view.findViewById(R.id.game_menu);
        Button newGame = view.findViewById(R.id.new_game);
        int color = getResources().getColor(R.color.colorOrange);
        DrawableUtils.setColor((GradientDrawable) menu.getBackground(), color);
        DrawableUtils.setColor((GradientDrawable) newGame.getBackground(), color);

        //设置滑屏监听器
        mGesture = new GestureDetector(getContext(), new GestureListener(mGameDrawView));
        mRelativeLayout.setOnTouchListener((v, event) -> mGesture.onTouchEvent(event));
        mGameDrawView.setOnSlideEndListener((view1, model) -> {
            mCurrentScore.setScore(model.getScore());
            if (model.getGameResult() == GameDataModel.GAME_STATUS_LOSE && !isSettlementDialogShow) {
                gameSettlement(model, false);
            } else if (model.getGameResult() == GameDataModel.GAME_STATUS_WIN && !isSettlementDialogShow) {
                gameSettlement(model, true);
            }
        });

    }

    /**
     * 初始化GameDrawView
     */
    private void initGameDrawView() {

        //计算大小与间距
        int width = DisplayUtils.getWidth(getContext());
        int margin = width / 20;

        //计算viewSize
        int viewSize = width - (margin << 1);


        //设置LayoutParams
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGameDrawView.getLayoutParams();
        params.width = params.height = viewSize;
        params.setMarginStart(margin);
        params.setMarginEnd(margin);

        //设置view的位置大小与游戏类型
        mGameDrawView.setLayoutParams(params);
        setGameType(mGameType);
    }

    /**
     * 初始化游戏类型数据
     */
    private void initGameTypeData() {

        //设置分数提示板
        mCurrentScore.setTitle(getResources().getString(R.string.score));
        mCurrentScore.setScore(0);
        mMaxScore.setTitle(getResources().getString(R.string.max_score));
        mMaxScore.setScore(GameDataStorage.getInstance().getMaxScore(mGameType.getName()));

        //设置游戏类型提示板
        mPromptText.setText(mGameType.getName());
        mModeName.setText(mGameType.getName());
    }

    @Override
    @OnClick(R.id.game_menu)
    public void back() {
        MainActivity activity = (MainActivity) getContext();
        Message mes = new Message(MainActivity.Status.TO_SELECT);
        Objects.requireNonNull(activity).onTransferMessage(this, mes);
    }

    /**
     * 设置游戏类型
     *
     * @param type 游戏类型
     */
    public void setGameType(@NonNull GameTypeModel type) {
//        Log.i(TAG, "setGameType: 调用");
        mGameType = type;

        final Context context = getContext();
        if (context == null) return;

        if (mGameType.getDisplayType() == GameTypeModel.DISPLAY_IMG) {

            final JsonObject object = new Gson().fromJson(mGameType.getContent(), JsonObject.class);
            final String url = object.get("bitmap").getAsString();
            new Thread(() -> Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    mBitmap = resource;
                    mGameDrawView.setGameType(mGameType, resource);
                    mGameDrawView.start();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }

            })).start();
        } else {
            mGameDrawView.setGameType(mGameType, null);
            mGameDrawView.start();
        }
        initGameTypeData();

    }

    /**
     * 新游戏
     */
    @OnClick(R.id.new_game)
    public void newGame() {

        if (mCurrentScore.getScore() > mMaxScore.getScore()) {
            mMaxScore.setScore(mCurrentScore.getScore());
        }

        mCurrentScore.setScore(0);
        mGameDrawView.restart();
    }

    @OnClick(R.id.game_prompt_text)
    public void showPromptDialog() {
        if (mGameType.getDisplayType() == GameTypeModel.DISPLAY_TEXT) {
            PromptDialog.getFragment(mGameType, null).show(getChildFragmentManager(), PromptDialog.TAG);
        } else if (mBitmap != null) {
            PromptDialog.getFragment(mGameType, mBitmap).show(getChildFragmentManager(), PromptDialog.TAG);
        }
    }

    /**
     * 游戏结算
     *
     * @param model  游戏模型
     * @param result 游戏是否胜利，如果胜利应该为true
     */
    public void gameSettlement(GameDataModel model, boolean result) {

        String name = SharedPreferencesUtils.getInstance().getUserName(getContext());
        SettlementDialog dialog =
                SettlementDialog.getFragment(name, model.getScore(), mGameType, result);
        Message message = new Message();

        if (result) {
            dialog.setPositiveButton("返回菜单", (fragment, view) -> {
                message.what = MainActivity.Status.TO_MAIN;
                gameOver(message);
            });

            dialog.setNegativeButton("查看排行版", (fragment, view) -> {
                message.what = MainActivity.Status.TO_LEADER_BOARD;
                gameOver(message);
            });

        } else {

            dialog.setPositiveButton("重新开始", (fragment, view) -> {
                newGame();
                isSettlementDialogShow = false;
            });

            dialog.setNegativeButton("返回菜单", (fragment, view) -> {
                message.what = MainActivity.Status.TO_MAIN;
                gameOver(message);
            });

        }

        dialog.show(getChildFragmentManager(), SettlementDialog.TAG);
        isSettlementDialogShow = true;
    }

    /**
     * 游戏结束
     *
     * @param message 信息
     */
    public void gameOver(Message message) {
        MainActivity main = (MainActivity) getContext();
        if (main != null) {
            main.onTransferMessage(this, message);
        }
        isSettlementDialogShow = false;
    }

    /**
     * 获取实例
     *
     * @param game 游戏类型
     * @return BaseFragment
     */
    public static BaseFragment newFragment(@Nullable GameTypeModel game) {
        Bundle bundle = new Bundle();

        if (game != null) bundle.putSerializable(TAG, game);

        BaseFragment fragment = new GameFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
