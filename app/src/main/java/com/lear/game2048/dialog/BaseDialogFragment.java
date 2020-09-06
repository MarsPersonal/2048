package com.lear.game2048.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * author: song
 * created on : 2020/8/2 22:35
 * description: DialogFragment抽象基类
 */
public abstract class BaseDialogFragment extends DialogFragment {

    public static final String TAG = "BaseDialogFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //隐藏标题栏
        if (getDialog() == null || getDialog().getWindow() == null) return;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }
}

