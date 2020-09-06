package com.lear.game2048.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lear.game2048.R;
import com.lear.game2048.activity.BaseActivity;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.adapter.BaseAdapter;
import com.lear.game2048.adapter.SelectGameAdapter;
import com.lear.game2048.model.GameDataStorage;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.DisplayUtils;
import com.lear.game2048.utils.DrawableUtils;
import com.lear.game2048.utils.Message;
import com.lear.game2048.utils.SQLUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SelectFragment extends BaseFragment implements
        BaseAdapter.OnRecyclerItemClickListener<GameTypeModel, SelectGameAdapter.SelectAdapterViewHolder>,
        BaseAdapter.OnRecyclerItemLongClickListener<GameTypeModel, SelectGameAdapter.SelectAdapterViewHolder> {

    public static final String TAG = "SelectFragment";

    private Unbinder mUnbinder;
    private int mWidth, mHeight;
    private SelectGameAdapter mAdapter = null;

    private List<GameTypeModel> mList = null;

    private PopupWindow mPopupWindow;
    private int touchX, touchY;

    @BindView(R.id.fragment_select_game_list_view)
    RecyclerView mRecyclerView;

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    @OnClick(R.id.back)
    public void back() {
        MainActivity activity = (MainActivity) getContext();
        Message mes = new Message(MainActivity.Status.TO_MAIN);
        Objects.requireNonNull(activity).onTransferMessage(this, mes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mList = GameDataStorage.getInstance().getGameTypeModelList();
            mAdapter.setData(mList);
        }
    }

    /**
     * 初始化View
     *
     * @param view view
     */
    private void initView(View view) {
        mUnbinder = ButterKnife.bind(this, view);

        mWidth = DisplayUtils.getWidth(getContext());
        mHeight = DisplayUtils.getHeight(getContext());

        //初始化弹窗
        Button delete = new Button(getContext());
        delete.setText("删除");
        delete.setBackgroundResource(R.drawable.circle_shape);

        DrawableUtils.setColor((GradientDrawable) delete.getBackground(), Color.WHITE);
        mPopupWindow = new PopupWindow(delete,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //重设颜色指针
        DrawableUtils.reset();
        //初始化RecyclerView
        initRecyclerView();

    }

    /**
     * 初始化RecyclerView及Adapter
     */
    private void initRecyclerView() {
        mList = GameDataStorage.getInstance().getGameTypeModelList();
        //创建适配器
        mAdapter = new SelectGameAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);

        //设置监听器
        mAdapter.setOnRecyclerItemClickListener(this);

        mAdapter.setOnRecyclerItemLongClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    @Override
    public void onRecyclerItemClickListener(RecyclerView recyclerView, BaseAdapter<GameTypeModel,
            SelectGameAdapter.SelectAdapterViewHolder> adapter, View view, int position) {
        GameTypeModel game = adapter.getItem(position);
        ITransfer transfer = (ITransfer) getContext();

        if (transfer == null) return;
        Message message = new Message(MainActivity.Status.TO_GAME_FRAGMENT);
        message.obj = game;
        transfer.onTransferMessage(SelectFragment.this, message);
    }

    @Override
    public boolean onRecyclerItemLongClickListener(RecyclerView recyclerView, BaseAdapter<GameTypeModel,
            SelectGameAdapter.SelectAdapterViewHolder> adapter, View view, int position) {

        if (getContext() == null || !(getContext() instanceof BaseActivity)) return false;
        final BaseActivity context = (BaseActivity) getContext();

        final GameTypeModel model = adapter.getItem(position);
        if (model.getName().equals("经典模式") || model.getName().equals("图片模式")) return false;


        mPopupWindow.getContentView().setOnClickListener(v -> {
            int number = SQLUtils.deleteGame(context, model);

            if (number != -1) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                mList.remove(model);
                mAdapter.setData(mList);
                GameDataStorage.getInstance().update(context, GameDataStorage.KEY_GAME_TYPE);
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            }

            mPopupWindow.dismiss();
        });

        mPopupWindow.showAsDropDown(view,
                (getResources().getDisplayMetrics().widthPixels - view.getWidth()) >> 1, 0);

        return true;
    }


    /**
     * 创建自身
     *
     * @return BaseFragment
     */
    public static BaseFragment newFragment() {
        BaseFragment fragment = new SelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


}