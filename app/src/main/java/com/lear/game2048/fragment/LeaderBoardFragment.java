package com.lear.game2048.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lear.game2048.R;
import com.lear.game2048.activity.MainActivity;
import com.lear.game2048.adapter.LeaderBoardAdapter;
import com.lear.game2048.model.GameDataStorage;
import com.lear.game2048.model.GameScoreModel;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LeaderBoardFragment extends BaseFragment {

    public static final String TAG = "LeaderBoardFragment";


    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.item_name)
    View mItemName;

    private Unbinder mUnbinder;
    private LeaderBoardAdapter<GameScoreModel> mAdapter;

    @Override
    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leader_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mUnbinder = ButterKnife.bind(this, view);
        //数据与选项卡
        setTabName();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == null) return;
                setGameTypeLeaderBoard(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //设置项名
        TextView serialNumberView = mItemName.findViewById(R.id.serial_number);
        TextView nameView = mItemName.findViewById(R.id.user_name);
        TextView scoreView = mItemName.findViewById(R.id.score);
        TextView dateView = mItemName.findViewById(R.id.date);


        serialNumberView.setText("排名");
        nameView.setText("用户名");
        scoreView.setText("得分");
        dateView.setText("日期");

        //适配器
        TabLayout.Tab defaultTab = mTabLayout.getTabAt(0);
        if (getContext() == null || defaultTab == null || defaultTab.getText() == null) return;
        List<GameScoreModel> data = GameDataStorage.getInstance().
                getGameScoreModels(defaultTab.getText().toString());

        mAdapter = new LeaderBoardAdapter<>(getContext(), data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new LeaderBoardAdapter.SpecialDividerItemDecoration(getContext(),
                LeaderBoardAdapter.SpecialDividerItemDecoration.VERTICAL_LIST));

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) return;
        setTabName();

        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        mTabLayout.selectTab(tab);
        if (tab == null || tab.getText() == null) return;
        setGameTypeLeaderBoard(tab.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.back)
    @Override
    public void back() {
        MainActivity activity = (MainActivity) getContext();
        if (activity == null) return;
        Message message = new Message();
        message.what = MainActivity.Status.TO_MAIN;
        ((ITransfer) activity).onTransferMessage(this, message);

    }

    /**
     * 设置TabLayout标签
     */
    public void setTabName() {
        mTabLayout.removeAllTabs();

        List<GameTypeModel> tabList = GameDataStorage.getInstance().getGameTypeModelList();
        for (GameTypeModel model : tabList) {
            mTabLayout.addTab(mTabLayout.newTab().setText(model.getName()));
        }
    }

    private void setGameTypeLeaderBoard(String typeName) {
        List<GameScoreModel> data = GameDataStorage.getInstance().
                getGameScoreModels(typeName);
        mAdapter.setData(data);
    }

    /**
     * 获取Fragment
     *
     * @return BaseFragment
     */
    public static BaseFragment newFragment() {
        return new LeaderBoardFragment();
    }

}
