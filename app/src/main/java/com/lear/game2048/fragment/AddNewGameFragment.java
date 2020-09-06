package com.lear.game2048.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.lear.game2048.R;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.dialog.LoadingDialog;
import com.lear.game2048.model.GameDataStorage;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.FileUtils;
import com.lear.game2048.utils.Message;
import com.lear.game2048.utils.SQLUtils;
import com.lear.game2048.view.AddImgItemView;
import com.lear.game2048.view.AddTextItemView;
import com.lear.game2048.view.IAddItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author: song
 * created on : 2020/8/22 23:10
 * description: 添加新游戏的Fragment
 */
public class AddNewGameFragment extends BaseFragment implements View.OnClickListener, IAddItem.OnRemoveClickListener {

    public static final String TAG = "AddNewGameFragment";

    public static final int REQUEST_CODE = 10;
    @BindView(R.id.layout)
    RelativeLayout mLayout;

    @BindView(R.id.game_name)
    EditText mGameName;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    @BindView(R.id.board_size_spinner)
    Spinner mBoardSizeView;

    @BindView(R.id.text_layout)
    LinearLayout mTextLayout;

    @BindView(R.id.img_layout)
    LinearLayout mImgLayout;

    @BindView(R.id.add_item)
    Button mAddItem;

    @BindView(R.id.sub)
    Button mSub;

    private List<AddTextItemView> mTextViews;
    private List<AddImgItemView> mImageViews;
    private int mBoardSize;

    //是否是文本模式
    private boolean isTextModel;

    private OnAsyncTaskListener mTextInsert;
    private OnAsyncTaskListener mImgInsert;

    private LoadingDialog mLoadingDialog;

    //当Fragment被隐藏时是否要清除数据
    private boolean isClear;


    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        mTextViews = new ArrayList<>();
        mImageViews = new ArrayList<>();
        mBoardSize = 4;
        isTextModel = true;
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_text) {
                isTextModel = true;
                mTextLayout.setVisibility(View.VISIBLE);
                mImgLayout.setVisibility(View.GONE);
            } else {
                isTextModel = false;
                mTextLayout.setVisibility(View.GONE);
                mImgLayout.setVisibility(View.VISIBLE);
            }
        });

        mBoardSizeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getSelectedItem();
                mBoardSize = Integer.parseInt(str);

                if (mTextViews.size() < (mBoardSize * mBoardSize) - 1) {
                    mAddItem.setVisibility(View.VISIBLE);
                } else {
                    mAddItem.setVisibility(View.GONE);
                    for (int i = mTextViews.size() - 1; i >= (mBoardSize * mBoardSize) - 1; i--) {
                        removeItem(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        isClear = true;

        for (int i = 0; i < 3; i++) addItem();

        initListener();

    }

    @Override
    public void onStart() {
        super.onStart();
        mGameName.clearFocus();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return false;
    }

    @OnClick({R.id.add_item, R.id.sub})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_item:
                addItem();
                break;
            case R.id.sub:
                sub();
                break;
        }
    }

    @OnClick(R.id.back)
    @Override
    public void back() {
        isClear = true;
        MainActivity activity = (MainActivity) getContext();
        if (activity == null) return;
        Message mes = new Message(MainActivity.Status.TO_MAIN);
        ((ITransfer) activity).onTransferMessage(this, mes);
    }

    @Override
    public void onRemoveClick(IAddItem view, int level) {
        removeItem(level - 1);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (!isClear) return;

        if (hidden) {
            mGameName.setText("");
            mRadioGroup.check(R.id.radio_text);
            mBoardSizeView.setSelection(0);
            isTextModel = true;

            mTextViews.clear();
            mImageViews.clear();
            mTextLayout.removeAllViews();
            mImgLayout.removeAllViews();
            mAddItem.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < 3; i++) addItem();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "获取权限成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "权限不足，无法加载图片", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * 添加item
     */
    public void addItem() {
        AddTextItemView textItemView = new AddTextItemView(getContext());
        AddImgItemView imgItemView = new AddImgItemView(getContext());

        textItemView.setLevel(mTextViews.size() + 1);
        imgItemView.setLevel(mImageViews.size() + 1);
        imgItemView.setOnClickLoadingImageListener(view -> toSelectImageFragment((AddImgItemView) view));

        mTextLayout.addView(textItemView);
        mImgLayout.addView(imgItemView);

        mTextViews.add(textItemView);
        mImageViews.add(imgItemView);

        if (mTextViews.size() <= 3) {
            textItemView.hideRemove();
            imgItemView.hideRemove();
        } else {
            textItemView.setOnRemoveListener(this);
            imgItemView.setOnRemoveListener(this);
        }

        if (mTextViews.size() >= (mBoardSize * mBoardSize) - 1) {
            mAddItem.setVisibility(View.GONE);
        }
    }

    /**
     * 移除item
     *
     * @param position 位置，从0开始
     */
    public void removeItem(int position) {
        if (position < 0 || position >= mTextViews.size()) return;

        mTextLayout.removeView(mTextViews.get(position));
        mImgLayout.removeView(mImageViews.get(position));

        mTextViews.remove(position);
        mImageViews.remove(position);

        for (int i = 0; i < mTextViews.size(); i++) {
            mTextViews.get(i).setLevel(i + 1);
            mImageViews.get(i).setLevel(i + 1);
        }

        if (mTextViews.size() < (mBoardSize * mBoardSize) - 1) mAddItem.setVisibility(View.VISIBLE);

    }

    @SuppressLint("SimpleDateFormat")
    private void initListener() {
        if (getContext() == null) return;
        final Context context = getContext();

        mLoadingDialog = (LoadingDialog) LoadingDialog.getFragment();

        mTextInsert = new OnAsyncTaskListener() {

            boolean isSuccess = false;
            String message;

            @Override
            public void doInBackground() {
                JsonObject json = new JsonObject();

                for (AddTextItemView item : mTextViews) {
                    if (item.getLevelContent().equals("")) {
                        isSuccess = false;
                        message = "等级：" + item.getLevel() + "显示内容为空";
                        return;
                    }

                    json.addProperty(String.valueOf(item.getLevel()), item.getLevelContent());
                }

//                GameTypeModel model = new GameTypeModel();
//                model.setName(mGameName.getText().toString())
//                        .setDisplayType(GameTypeModel.DISPLAY_TEXT)
//                        .setCheckerBoardMax(mBoardSize)
//                        .setMaxLevel(mTextViews.size())
//                        .setMode(GameTypeModel.MODE_ClASSIC)
//                        .setContent(json.toString());

                GameTypeModel model = getGameTypeModel(json.toString(), true);

                long id = SQLUtils.insertNewGame(context, model);
                if (id == -1) {
                    isSuccess = false;
                    message = "数据库插入失败";
                } else {
                    isSuccess = true;
                    message = "添加成功";
                }
            }

            @Override
            public void postExecute() {
                mLoadingDialog.dismiss();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                if (isSuccess) {
                    GameDataStorage.getInstance().update(context, GameDataStorage.KEY_GAME_TYPE);
                    back();
                }
            }
        };

        mImgInsert = new OnAsyncTaskListener() {

            boolean isSuccess = false;
            String message;

            @Override
            public void doInBackground() {
                //检查
                for (AddImgItemView item : mImageViews) {
                    if (item.getBitmap() == null) {
                        isSuccess = false;
                        message = "等级：" + item.getLevel() + "内容为空";
                        return;
                    }
                }

                JsonObject json = new JsonObject();

                //每张图片的宽度与高度(px)
                int size = 150;
                //图片与图片之间的空隙
                int divider = 1;

                //创建图片
                Bitmap bitmap = Bitmap.createBitmap(size * mImageViews.size() + divider * (mImageViews.size() - 1),
                        size, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Rect rect = new Rect();

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);

                int top = 0;

                //把图片绘入bitmap中
                //因为bitmap的高度是固定的，变化的只是宽度
                //因此只要计算宽度的位置就可以了
                for (int i = 0; i < mImageViews.size(); i++) {
                    int left = i * (size + divider);
                    int right = left + size;

                    rect.set(left, top, right, size);

                    canvas.drawBitmap(mImageViews.get(i).getBitmap(), null, rect, paint);
                    canvas.save();

                    final JsonObject position = new JsonObject();
                    position.addProperty("left", left);
                    position.addProperty("top", top);
                    position.addProperty("right", right);
                    position.addProperty("bottom", size);
                    json.addProperty(String.valueOf(i + 1), position.toString());

                }

                canvas.save();
                File file;
                try {
                    String fileName = String.format("%s_%s.png", mGameName.getText().toString(),
                            new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date()));
                    file = FileUtils.saveGameImageFile(context, fileName, bitmap);
                } catch (IOException e) {
                    isSuccess = false;
                    message = "写入文件失败";
                    return;
                }

                json.addProperty("bitmap", file.getAbsolutePath());

                GameTypeModel model = getGameTypeModel(json.toString(), false);
                long id = SQLUtils.insertNewGame(context, model);
                if (id == -1) {
                    isSuccess = false;
                    message = "数据库插入失败";
                } else {
                    isSuccess = true;
                    message = "添加成功";
                }
            }

            @Override
            public void postExecute() {
                mLoadingDialog.dismiss();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                if (isSuccess) {
                    GameDataStorage.getInstance().update(context, GameDataStorage.KEY_GAME_TYPE);
                    back();
                }
            }
        };

    }

    /**
     * 确定
     */
    public void sub() {
        if (mGameName.getText().toString().equals("")) {
            Toast.makeText(getContext(), "游戏名不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (SQLUtils.getGameNames(getContext()).contains(mGameName.getText().toString())) {
            Toast.makeText(getContext(), "游戏名重复", Toast.LENGTH_SHORT).show();
            return;
        }

        mLoadingDialog.show(getChildFragmentManager(), LoadingDialog.TAG);
        if (isTextModel) {
//            postTextModel();
            new InsertNewGame().execute(mTextInsert);
        } else {
//            postImgModel();
            new InsertNewGame().execute(mImgInsert);
        }
    }

    /**
     * 将数据保存为GameTypeModel
     *
     * @param content     显示内容
     * @param isTextModel 显示内容是否为文本
     * @return GameTypeModel
     */
    private GameTypeModel getGameTypeModel(String content, boolean isTextModel) {
        GameTypeModel model = new GameTypeModel();
        Log.i(TAG, "getGameTypeModel: mBoardSize=" + mBoardSize);
        model.setName(mGameName.getText().toString())
                .setDisplayType(isTextModel ? GameTypeModel.DISPLAY_TEXT : GameTypeModel.DISPLAY_IMG)
                .setCheckerBoardMax(mBoardSize)
                .setMaxLevel(mTextViews.size())
                .setMode(GameTypeModel.MODE_ClASSIC)
                .setContent(content);

        Log.i(TAG, "getGameTypeModel: size="+model.getCheckerBoardMax());
        return model;
    }

    /**
     * 到选择图片页面
     *
     * @param view 选择图片后将图片加载到的view
     */
    public void toSelectImageFragment(AddImgItemView view) {
        if (!requestPermission()) return;
        isClear = false;

        MainActivity activity = (MainActivity) getContext();
        if (activity == null) return;
        Message mes = new Message(MainActivity.Status.TO_SELECT_IMAGE);
        mes.obj = view;
        ((ITransfer) activity).onTransferMessage(this, mes);
    }

    /**
     * 请求权限
     */
    public boolean requestPermission() {

        if (getContext() == null) return false;
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        //检查是否拥有权限
        int checkSelfPermission = ContextCompat.checkSelfPermission(getContext(), permission);

        //如果已经拥有权限
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) return true;

        //没有权限，检查是否能显示权限申请说明的dialog
        if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("权限申请")
                    .setMessage("我们需要权限来获取设备内部的图片，否则我们将无法加载图片")
                    .setPositiveButton("确定", (dialog, which) ->
                            requestPermissions(new String[]{permission}, REQUEST_CODE))
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        } else {
            requestPermissions(new String[]{permission}, REQUEST_CODE);
        }

        return false;

    }

    /**
     * 新建Fragment
     *
     * @return BaseFragment
     */
    public static BaseFragment newFragment() {
        return new AddNewGameFragment();
    }

    /**
     * 异步任务监听器
     */
    interface OnAsyncTaskListener {
        /**
         * 后台任务
         */
        void doInBackground();

        /**
         * 任务完成后执行
         */
        void postExecute();
    }

    /**
     * 插入游戏异步任务
     */
    private static class InsertNewGame extends AsyncTask<OnAsyncTaskListener, Void, OnAsyncTaskListener> {

        @Override
        protected OnAsyncTaskListener doInBackground(OnAsyncTaskListener... onAsyncTaskCalls) {
            for (OnAsyncTaskListener insert : onAsyncTaskCalls) {
                insert.doInBackground();
            }
            return onAsyncTaskCalls[0];
        }

        @Override
        protected void onPostExecute(OnAsyncTaskListener onAsyncTaskCall) {
            onAsyncTaskCall.postExecute();
        }
    }


}
