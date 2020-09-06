package com.lear.game2048.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lear.game2048.R;
import com.lear.game2048.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserNameDialog extends BaseDialogFragment {

    public static final String TAG = "UserNameDialog";

    @BindView(R.id.edit)
    EditText mEditText;

    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_set_user_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        if (getDialog() == null) return;
        getDialog().setOnShowListener(dialog -> showKeyboard());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getDialog() == null || getDialog().getWindow() == null) return;
        getDialog().getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.7f),
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCancelable(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @OnClick(R.id.sub)
    public void subUserName() {
        String name = mEditText.getText().toString();
        if (name.equals("")) {
            Toast.makeText(getContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferencesUtils.getInstance().setUserName(getContext(), name);
        dismiss();
    }


    /**
     * 显示键盘
     */
    public void showKeyboard() {
        mEditText.setText("");
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();

        //弹出输入框
        InputMethodManager inputMethodManager = (InputMethodManager) mEditText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static DialogFragment getFragment() {
        return new UserNameDialog();
    }

}
