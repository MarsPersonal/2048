package com.lear.game2048.fragment;

import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lear.game2048.R;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.Message;
import com.lear.game2048.view.IAddItem;
import com.lear.game2048.view.VarietyImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * author: song
 * created on : 2020/8/28 1:21
 * description: 从图库中加载并选择图片
 */
public class SelectImageFragment extends BaseFragment implements VarietyImageView.OnChangedListener {

    public static final String TAG = "SelectImageFragment";

    //    @BindView(R.id.grid_view)
//    GridView mGridView;
    @BindView(R.id.image_view)
    VarietyImageView mVarietyImageView;

    @BindView(R.id.top)
    View mTop;
    @BindView(R.id.sub)
    Button mSub;
    @BindView(R.id.cancel)
    Button mCancel;
    private IAddItem mView;

    private Uri mUri;

    private ValueAnimator mAnimator;

    private Unbinder mUnbinder;

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mUnbinder = ButterKnife.bind(this, view);
        mAnimator = ValueAnimator.ofFloat(1, 0);
        mAnimator.setDuration(100);
        mAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mSub.setAlpha(value);
            mTop.setAlpha(value);
            mCancel.setAlpha(value);
        });

        mVarietyImageView.setChangedListener(this);
//        DrawableUtils.setColor((GradientDrawable) mSub.getBackground(), Color.GREEN);
//        mGridView.setNumColumns(4);

//        mSimpleAdapter = new SimpleAdapter()
    }

    @Override
    public void onStart() {
        super.onStart();
        mVarietyImageView.load(mUri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return false;
    }

    @OnClick(R.id.cancel)
    @Override
    public void back() {
        MainActivity activity = (MainActivity) getContext();
        if (activity == null) return;
        Message mes = new Message(MainActivity.Status.TO_ADD_GAME);
        ((ITransfer) activity).onTransferMessage(this, mes);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            mView = null;
        }
    }

    @Override
    public void onChanged(VarietyImageView view, boolean isChange) {
        if (isChange) {
            mAnimator.setFloatValues(1, 0);
        } else {
            mAnimator.setFloatValues(0, 1);
        }
        mSub.setEnabled(!isChange);
        mTop.setEnabled(!isChange);
        mCancel.setEnabled(!isChange);
        mAnimator.start();
    }

    /**
     * 设置加载view
     *
     * @param view view
     */
    public void setLoadingView(IAddItem view) {
        mView = view;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    @OnClick(R.id.sub)
    public void selectImage() {
        mView.setBitmap(mVarietyImageView.outBitmap());
        mVarietyImageView.recycle();
        back();
    }

    /**
     * 新建Fragment
     *
     * @return BaseFragment
     */
    public static BaseFragment newFragment() {

        return new SelectImageFragment();
    }


}
