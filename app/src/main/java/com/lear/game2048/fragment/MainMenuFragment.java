package com.lear.game2048.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lear.game2048.R;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.dialog.SettingDialog;
import com.lear.game2048.dialog.UserNameDialog;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.DrawableUtils;
import com.lear.game2048.utils.Message;
import com.lear.game2048.utils.SharedPreferencesUtils;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainMenuFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "MainMenuFragment";

    private Unbinder mUnbinder;

    @BindViews({R.id.start_game, R.id.diy_game, R.id.leader_board, R.id.setting,
            R.id.author, R.id.exit})
    List<View> mViewList;

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        DrawableUtils.reset();
        DrawableUtils.setBgColor(mViewList);
        for (View v : mViewList) {
            v.setOnClickListener(this);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (SharedPreferencesUtils.getInstance().getUserName(getContext()).equals("")) {
            UserNameDialog.getFragment().show(getChildFragmentManager(), UserNameDialog.TAG);
        }
    }

    @Override
    public void onClick(View view) {

        ITransfer transfer = (MainActivity) getContext();
        if (transfer == null) return;
        Message message = new Message();

        switch (view.getId()) {
            case R.id.start_game:
                message.what = MainActivity.Status.TO_SELECT;
                break;
            case R.id.diy_game:
                message.what = MainActivity.Status.TO_ADD_GAME;
                break;
            case R.id.leader_board:
                message.what = MainActivity.Status.TO_LEADER_BOARD;
                break;
            case R.id.setting:
                showSettingDialog();
                return;
            case R.id.author:
                message.what = MainActivity.Status.TO_AUTHOR;
                break;
            case R.id.exit:
                message.what = MainActivity.Status.END_GAME;
                break;
        }

        transfer.onTransferMessage(this, message);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

    }

    /**
     * 显示设置Dialog
     */
    private void showSettingDialog() {
        SettingDialog.getFragment().show(getChildFragmentManager(), SettingDialog.TAG);
    }

    /**
     * 新建BaseFragment
     * @return BaseFragment
     */
    public static BaseFragment newFragment() {
        return new MainMenuFragment();
    }


}
