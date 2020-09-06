package com.lear.game2048.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lear.game2048.R;

public class WelcomeFragment extends BaseFragment {

    public static final String TAG = "WelcomeFragment";

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new ImageView(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.WHITE);
        Log.i(TAG, "onViewCreated: id=" + R.drawable.welcome);
        ((ImageView) view).setImageResource(R.drawable.welcome);

    }

    @Override
    public void back() {

    }

    public static BaseFragment newFragment() {
        return new WelcomeFragment();
    }
}
