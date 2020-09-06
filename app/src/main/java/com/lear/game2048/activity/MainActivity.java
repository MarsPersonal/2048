package com.lear.game2048.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lear.game2048.R;
import com.lear.game2048.fragment.AddNewGameFragment;
import com.lear.game2048.fragment.AuthorFragment;
import com.lear.game2048.fragment.BaseFragment;
import com.lear.game2048.fragment.GameFragment;
import com.lear.game2048.fragment.LeaderBoardFragment;
import com.lear.game2048.fragment.MainMenuFragment;
import com.lear.game2048.fragment.SelectFragment;
import com.lear.game2048.fragment.SelectImageFragment;
import com.lear.game2048.fragment.WelcomeFragment;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.service.MusicService;
import com.lear.game2048.transfer.ITransfer;
import com.lear.game2048.utils.Message;
import com.lear.game2048.utils.PressedUtils;
import com.lear.game2048.view.AddImgItemView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements ITransfer {

    public static final String TAG = "MainActivity";
    public static final int REQUEST_IMAGE_CODE = 10;


    @BindView(R.id.main_layout)
    RelativeLayout mMainLayout;

    private String mCurrentTag;
    private AddImgItemView mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //初始化
        init();
    }

    @Override
    protected int onViewId() {
        return R.id.frame;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SQLHelper.getInstance(this).close();
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                if (data.getData() != null) {
                    Log.i(TAG, "onActivityResult: uri=" + data.getData().toString());
                }
                toSelectImagePage(mItem, data.getData());
            }
            mItem = null;
        }
    }

    @Override
    public void onTransferMessage(ITransfer self, Message message) {
        if (message.what == Status.TO_MAIN) {
            toMain();
        } else if (message.what == Status.TO_SELECT) {
            toSelectPage();
        } else if (message.what == Status.TO_ADD_GAME) {
            toAddGamePage();
        } else if (message.what == Status.TO_GAME_FRAGMENT) {
            if (message.obj == null)
                throw new NullPointerException("开始游戏时，Message.obj对象不能为空");
            toGamePage((GameTypeModel) message.obj);
        } else if (message.what == Status.TO_AUTHOR) {
            toAuthorPage();
        } else if (message.what == Status.TO_LEADER_BOARD) {
            toLeaderBoard();
        } else if (message.what == Status.END_GAME) {
            finish();
        } else if (message.what == Status.TO_SELECT_IMAGE) {
            if (!(message.obj instanceof AddImgItemView)) throw new NullPointerException("view为空");
            mItem = (AddImgItemView) message.obj;
            getGallery();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = getFrag().get(mCurrentTag);
        Log.i(TAG, "onKeyDown: tag=" + mCurrentTag);
        if (getFrag().isShow(mCurrentTag) && fragment != null &&
                ((BaseFragment) fragment).onKeyDown(keyCode, event)) {
            return true;
        } else if (!mCurrentTag.equals(WelcomeFragment.TAG) && keyCode == KeyEvent.KEYCODE_BACK) {
            if (PressedUtils.isPressedBackTwice()) finish();
            else Toast.makeText(this, "再按一次退出游戏",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 初始化
     */
    private void init() {
        new ToMainAsyncTask().execute(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getFrag().add(WelcomeFragment.newFragment(), WelcomeFragment.TAG);
        getFrag().add(MainMenuFragment.newFragment(), MainMenuFragment.TAG);
        getFrag().show(WelcomeFragment.TAG);
        mCurrentTag = WelcomeFragment.TAG;

        startService(new Intent(this, MusicService.class));
    }

    /**
     * 转跳主页
     */
    public void toMain() {
        mCurrentTag = MainMenuFragment.TAG;

        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(MainMenuFragment.newFragment(), mCurrentTag);
        }
        getFrag().show(mCurrentTag);


    }

    /**
     * 转跳选择页
     */
    public void toSelectPage() {
        mCurrentTag = SelectFragment.TAG;
        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(SelectFragment.newFragment(), mCurrentTag);
        }
        getFrag().show(mCurrentTag);

    }

    /**
     * 转跳到游戏页
     *
     * @param model 游戏类型
     */
    public void toGamePage(GameTypeModel model) {
        mCurrentTag = GameFragment.TAG;
        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(GameFragment.newFragment(model), mCurrentTag);
        } else {
            GameFragment fragment = (GameFragment) getFrag().get(mCurrentTag);
            fragment.setGameType(model);
        }
        getFrag().show(mCurrentTag);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 转跳到添加游戏页
     */
    public void toAddGamePage() {
        mCurrentTag = AddNewGameFragment.TAG;
        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(AddNewGameFragment.newFragment(), mCurrentTag);
        }
        getFrag().show(mCurrentTag);
    }

    /**
     * 到作者页
     */
    public void toAuthorPage() {
        mCurrentTag = AuthorFragment.TAG;
        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(AuthorFragment.newFragment(), mCurrentTag);
        }
        getFrag().show(mCurrentTag);
    }

    /**
     * 转跳到排行榜
     */
    public void toLeaderBoard() {
        mCurrentTag = LeaderBoardFragment.TAG;
        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(LeaderBoardFragment.newFragment(), LeaderBoardFragment.TAG);
        }
        getFrag().show(mCurrentTag);
    }

    /**
     * 获取图库
     */
    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
        startActivityForResult(intent, REQUEST_IMAGE_CODE);
    }

    /**
     * 转跳到图片选择页
     *
     * @param view 选择图片后将图片加载到的view
     * @param uri  返回图片的uri
     */
    public void toSelectImagePage(AddImgItemView view, Uri uri) {

        mCurrentTag = SelectImageFragment.TAG;

        if (!getFrag().contains(mCurrentTag)) {
            getFrag().add(SelectImageFragment.newFragment(), mCurrentTag);
        }
        SelectImageFragment fragment = (SelectImageFragment) getFrag().get(mCurrentTag);
        fragment.setLoadingView(view);
        fragment.setUri(uri);
        getFrag().show(mCurrentTag);
    }

    /**
     * Fragment返回监听按钮
     */
    public interface FragmentKeyDownListener {
        boolean onKeyDown(int keycode, KeyEvent event);
    }

    /**
     * 状态
     */
    public static class Status {
        public static final int END_GAME = 0;       //结束游戏
        public static final int TO_MAIN = 0x100;  //从Fragment返回到首页
        public static final int TO_SELECT = 0x101;            //从Fragment返回到SelectFragment
        public static final int TO_GAME_FRAGMENT = 0x102;    //转跳到GameFragment
        public static final int TO_AUTHOR = 0x103;          //转跳到AuthorFragment
        public static final int TO_LEADER_BOARD = 0x104;   //转跳到LeaderBoardFragment
        public static final int TO_ADD_GAME = 0x105;        //转跳到AddNewGameFragment
        public static final int TO_SELECT_IMAGE = 0x106;    //转味到SelectImageFragment

    }

    /**
     * 转跳到主页
     * 用于从欢迎屏转跳到主页
     */
    private static class ToMainAsyncTask extends AsyncTask<MainActivity, Void, MainActivity> {

        @Override
        protected MainActivity doInBackground(MainActivity... mainActivities) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            return mainActivities[0];
        }

        @Override
        protected void onPostExecute(MainActivity mainActivity) {
            mainActivity.toMain();
        }

    }
}

