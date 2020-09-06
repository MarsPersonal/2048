package com.lear.game2048.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements ITransfer, MainActivity.FragmentKeyDownListener {

    public static final String TAG = "BaseFragment";

    private Context mContext;
    private View mView;

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = onView(inflater, container, savedInstanceState);
        return mView;
    }

    /**
     * 创建view
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    public abstract View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 返回上一级
     */
    public void back() {
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onTransferMessage(ITransfer self, Message message) {
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        return false;
    }
}
