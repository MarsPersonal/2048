package com.lear.game2048.fragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lear.game2048.R;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthorFragment extends BaseFragment {

    public static final String TAG = "AuthorFragment";
    private Unbinder mUnbinder;

    @BindView(R.id.content)
    TextView mTextView;

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_author_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mUnbinder = ButterKnife.bind(this, view);

        //这个设置可以使文本里的url被点击并转跳
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    @OnClick(R.id.back)
    public void back() {
        MainActivity activity = (MainActivity) getContext();
        if (activity == null) return;
        Message mes = new Message(MainActivity.Status.TO_MAIN);
        ((ITransfer) activity).onTransferMessage(this, mes);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return false;
    }

    /**
     * 新建BaseFragment
     *
     * @return BaseFragment
     */
    public static BaseFragment newFragment() {
        return new AuthorFragment();
    }
}
