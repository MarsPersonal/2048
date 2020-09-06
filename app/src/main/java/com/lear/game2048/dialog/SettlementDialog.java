package com.lear.game2048.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lear.game2048.R;
import com.lear.game2048.model.GameDataStorage;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.utils.SQLUtils;
import com.lear.game2048.utils.SharedPreferencesUtils;

/**
 * 结算DialogFragment
 */
public class SettlementDialog extends BaseDialogFragment implements View.OnClickListener {

    public static final String TAG = "SettlementDialog";

    private static final String KEY_USER_NAME = "USER_NAME";
    private static final String KEY_SCORE = "SCORE";
    private static final String KEY_MODE = "MODE";
    private static final String KEY_RESULT = "RESULT";

    private int mScore;
    private String mUserName;
    private GameTypeModel mGameTypeModel;
    private boolean isResult;

    private OnClickListener mPositiverListener = null;
    private OnClickListener mNegativeListener = null;

    private Button mPositiveButton;
    private Button mNegativeButton;

    private String mPositiveText = "";
    private String mNegativeText = "";

    private EditText mEditText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle == null) return;

        mScore = bundle.getInt(KEY_SCORE);
        mUserName = bundle.getString(KEY_USER_NAME);
        mGameTypeModel = (GameTypeModel) bundle.getSerializable(KEY_MODE);
        isResult = bundle.getBoolean(KEY_RESULT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_settlement_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //标题内容
        TextView titleView = view.findViewById(R.id.dialog_title);
        String title;
        if (isResult) {
            title = "通关成功";
        } else {
            title = "游戏结束";
        }
        titleView.setText(title);

        //用户名
        mEditText = view.findViewById(R.id.user_name);
        mEditText.setText(mUserName);

        //分数
        TextView score = view.findViewById(R.id.message);
        score.setText(String.format(getString(R.string.you_score), mScore));

        //两个按钮
        mPositiveButton = view.findViewById(R.id.positive_button);
        mPositiveButton.setText(mPositiveText);
        mPositiveButton.setOnClickListener(this);

        mNegativeButton = view.findViewById(R.id.negative_button);
        mNegativeButton.setText(mNegativeText);
        mNegativeButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mEditText.clearFocus();
        if (getDialog() == null) return;
        getDialog().setCancelable(false);
    }

    /**
     * 设置确定按钮
     *
     * @param text     显示文本
     * @param listener 接口回调
     */
    public void setPositiveButton(CharSequence text, final OnClickListener listener) {
        mPositiveText = text.toString();
        mPositiverListener = listener;
        if (mPositiveButton != null) mPositiveButton.setText(text);
    }

    /**
     * 设置取消按钮
     *
     * @param text     显示文本
     * @param listener 接口回调
     */
    public void setNegativeButton(CharSequence text, final OnClickListener listener) {
        mNegativeText = text.toString();
        mNegativeListener = listener;
        if (mNegativeButton != null) mNegativeButton.setText(text);
    }

    /**
     * 插入新成绩
     */
    private void insertNewScore() {
        long id = SQLUtils.insertNewScore(getContext(), mGameTypeModel, mUserName, mScore);
        Log.i(TAG, "insertNewScore: 新成绩id=" + id);
        if (id == -1) Toast.makeText(getContext(), "数据错误，请重启游戏", Toast.LENGTH_SHORT).show();
        GameDataStorage.getInstance().update(getContext(), GameDataStorage.KEY_GAME_SCORE);
    }

    /**
     * 更换用户名
     *
     * @param name 新的用户名
     */
    private void updateUserName(String name) {
        if (!mUserName.equals(name)) {
            SharedPreferencesUtils.getInstance().setUserName(getContext(), name);
            mUserName = name;
        }
    }

    /**
     * 获取用户名
     *
     * @return String
     */
    public String getUserName() {
        return mEditText.toString();
    }

    /**
     * 检查用户名
     *
     * @return 如果检查通过则返回true
     */
    public boolean checkUserName() {
        String userName = mEditText.getText().toString();
        return !userName.equals("") && userName.length() > 0;
    }

    /**
     * 获取Fragment
     *
     * @param name   用户名
     * @param score  分数
     * @param model  游戏类型
     * @param result 游戏结果
     * @return SettlementDialogFragment
     */
    public static SettlementDialog getFragment(String name, int score, GameTypeModel model, boolean result) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_USER_NAME, name);
        bundle.putInt(KEY_SCORE, score);
        bundle.putSerializable(KEY_MODE, model);
        bundle.putBoolean(KEY_RESULT, result);

        SettlementDialog fragment = new SettlementDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        //检查
        if (!checkUserName()) {
            Toast.makeText(getContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //回调
        if (view.getId() == R.id.positive_button && mPositiverListener != null) {
            mPositiverListener.onClick(this, view);
        } else if (view.getId() == R.id.negative_button && mNegativeListener != null) {
            mNegativeListener.onClick(this, view);
        }

        updateUserName(mEditText.getText().toString());
        insertNewScore();
        dismiss();
    }

    /**
     * 单击监听器
     */
    public interface OnClickListener {

        /**
         * 单击回调
         *
         * @param fragment DialogFragment
         * @param view     view
         */
        void onClick(SettlementDialog fragment, View view);
    }

}
