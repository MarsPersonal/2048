package com.lear.game2048.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lear.game2048.R;

/**
 * author: song
 * created on : 2020/8/26 23:03
 * description: 等待dialog
 */
public class LoadingDialog extends BaseDialogFragment {

    public static final String TAG = "LoadingDialog";
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_loading, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null || getDialog().getWindow() == null) return;
        getDialog().getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.5 + 0.5f),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.5 + 0.5f));
        setCancelable(false);
    }

    public static BaseDialogFragment getFragment() {
        return new LoadingDialog();
    }
}
