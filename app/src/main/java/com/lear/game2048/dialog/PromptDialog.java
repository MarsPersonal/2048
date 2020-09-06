package com.lear.game2048.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lear.game2048.R;
import com.lear.game2048.model.GameBlockUnit;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.utils.DrawableUtils;
import com.lear.game2048.view.LevelImageView;

/**
 * author: song
 * created on : 2020/8/18 1:03
 * description: 提示dialog
 */
public class PromptDialog extends BaseDialogFragment {

    public static final String TAG = "PromptDialog";

    private static final String KEY_BITMAP = "BITMAP";
    private static final String KEY_GAME_TYPE = "GAME_TYPE";

    private ViewGroup mLayout;

    private GameTypeModel mGameTypeModel;
    private Bitmap mBitmap = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        if (bundle == null) return;

        mGameTypeModel = (GameTypeModel) bundle.getSerializable(KEY_GAME_TYPE);
        if (mGameTypeModel == null) throw new NullPointerException("mGameTypeModel为空");
        if (mGameTypeModel.getDisplayType() == GameTypeModel.DISPLAY_IMG) {
            mBitmap = bundle.getParcelable(KEY_BITMAP);
            if (mBitmap == null) throw new NullPointerException("mBitmap为空");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_prompt, container, false);
        mLayout = view.findViewById(R.id.root);
        DrawableUtils.setColor((GradientDrawable) view.getBackground(), Color.WHITE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mGameTypeModel.getDisplayType() == GameTypeModel.DISPLAY_TEXT) initTextLevelView();
        else initImageLevelView();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null || getDialog().getWindow() == null) return;

        getDialog().getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9 + 0.5f),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.8 + 0.5f));

        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);

    }

    /**
     * 初始化文本等级View
     */
    private void initTextLevelView() {
        Context context = getContext();
        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        int[] bgColor = DrawableUtils.getBgColor(context);

        JsonObject json = new Gson().fromJson(mGameTypeModel.getContent(), JsonObject.class);

        for (String key : json.keySet()) {
            View item = inflater.inflate(R.layout.item_level_text_chart, mLayout, false);
            TextView level = item.findViewById(R.id.level);
            TextView correspond = item.findViewById(R.id.correspond);

            String str = json.getAsJsonPrimitive(key).toString();

            level.setText(key);
            correspond.setText(str.substring(1, str.length() - 1));

            DrawableUtils.setColor((GradientDrawable) level.getBackground(), bgColor[Integer.parseInt(key)]);
            DrawableUtils.setColor((GradientDrawable) correspond.getBackground(), bgColor[Integer.parseInt(key)]);

            mLayout.addView(item);
        }

    }

    /**
     * 初始化图片等级View
     */
    private void initImageLevelView() {
        Context context = getContext();
        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        int[] bgColor = DrawableUtils.getBgColor(context);

        JsonObject json = (new Gson()).fromJson(mGameTypeModel.getContent(), JsonObject.class);

        for (String key : json.keySet()) {
            if (key.equals("bitmap")) continue;
            final JsonObject object = new Gson().fromJson(json.getAsJsonPrimitive(key).getAsString(), JsonObject.class);

            final View item = inflater.inflate(R.layout.item_level_img_chart, mLayout, false);
            TextView level = item.findViewById(R.id.level);
            LevelImageView correspond = item.findViewById(R.id.correspond);

            level.setText(key);
            correspond.setBitmap(mBitmap);
            correspond.setClip(object.get("left").getAsInt(),
                    object.get("top").getAsInt(),
                    object.get("right").getAsInt(),
                    object.get("bottom").getAsInt());

            DrawableUtils.setColor((GradientDrawable) level.getBackground(), bgColor[Integer.parseInt(key)]);
            DrawableUtils.setColor((GradientDrawable) correspond.getBackground(), bgColor[Integer.parseInt(key)]);

            mLayout.addView(item);
        }
    }

    /**
     * 获取Fragment
     *
     * @param gameType 游戏类型
     * @param bitmap   图片，可以为空
     * @return BaseDialogFragment
     */
    public static BaseDialogFragment getFragment(GameTypeModel gameType, @Nullable Bitmap bitmap) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_GAME_TYPE, gameType);
        bundle.putParcelable(KEY_BITMAP, bitmap);

        BaseDialogFragment fragment = new PromptDialog();
        fragment.setArguments(bundle);

        return fragment;
    }
}
