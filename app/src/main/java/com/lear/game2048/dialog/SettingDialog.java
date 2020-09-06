package com.lear.game2048.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.lear.game2048.R;
import com.lear.game2048.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;

/**
 * author: song
 * created on : 2020/8/2 21:36
 * description:设置Fragment，负责音乐与高帧率模式的开关
 */
public class SettingDialog extends BaseDialogFragment {

    public static final String TAG = "SettingFragment";

    private Unbinder mUnbinder;
//    @BindView(R.id.sound_effect_switch)
//    SwitchCompat mMusicSwitch;

    @BindView(R.id.sound_effect_switch)
    SwitchCompat mSoundEffectSwitch;

    @BindView(R.id.frame_rate)
    SwitchCompat mFrameRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_setting_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
//        mMusicSwitch.setChecked(SharedPreferencesUtils.getInstance().getMusicSwitch(getContext()));
        mSoundEffectSwitch.setChecked(SharedPreferencesUtils.getInstance().getSoundEffectSwitch(getContext()));
        mFrameRate.setChecked(SharedPreferencesUtils.getInstance().getHighFrameRateModeSwitch(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    /**
     * SwitchCompat修改
     *
     * @param button    按钮
     * @param isChecked 是否勾选
     */
    @OnCheckedChanged({R.id.sound_effect_switch, R.id.frame_rate})
    public void checkChanged(CompoundButton button, boolean isChecked) {
        switch (button.getId()) {
            case R.id.sound_effect_switch:
//                musicSwitch(isChecked);
                soundEffectSwitch(isChecked);
                break;
            case R.id.frame_rate:
                highFrameRateModeSwitch(isChecked);
                break;
        }
    }

//    /**
//     * 音乐开关
//     *
//     * @param isChecked 是否勾选
//     */
//    public void musicSwitch(boolean isChecked) {
//        SharedPreferencesUtils.getInstance().setMusicSwitch(getContext(), isChecked);
//    }

    /**
     * 音效开关
     * @param isChecked 是否勾选
     */
    public void soundEffectSwitch(boolean isChecked) {
        SharedPreferencesUtils.getInstance().setSoundEffectSwitch(getContext(), isChecked);
    }

    /**
     * 高帧率模式开关
     *
     * @param isChecked 是否勾选
     */
    public void highFrameRateModeSwitch(boolean isChecked) {
        SharedPreferencesUtils.getInstance().setHighFrameRateModeSwitch(getContext(), isChecked);
    }

    /**
     * 获取DialogFragment
     *
     * @return DialogFragment
     */
    public static DialogFragment getFragment() {
        return new SettingDialog();
    }

}
