package com.lear.game2048.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.lear.game2048.R;

import java.lang.ref.WeakReference;

/**
 * author: song
 * created on : 2020/8/22 12:22
 * description: 音乐服务，用于播放音乐与音效
 */
public class MusicService extends Service {
    public static final String TAG = "MusicService";
    private SoundPool mSoundPool = null;
    private int mBlockMoveId;

    private MusicPlayReceiver mReceiver = null;

    @Override
    public void onCreate() {
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSoundPool.release();
        mSoundPool = null;

        unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    /**
     * 初始化
     */
    private void init() {
        initSoundPool();

        if (mReceiver == null) {
            mReceiver = new MusicPlayReceiver(this);

            IntentFilter filter = new IntentFilter();
            filter.addAction(MusicPlayReceiver.ACTION_PLAY_BLOCK_MOVE_MUSIC);

            registerReceiver(mReceiver, filter);
        }
    }

    /**
     * 初始化音乐池
     */
    private void initSoundPool() {
        if (mSoundPool != null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder()
                    .setMaxStreams(2);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            mSoundPool = builder.setAudioAttributes(attributes).build();
        } else {
            mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        mBlockMoveId = mSoundPool.load(getApplicationContext(), R.raw.block_move, 1);
    }


    /**
     * 播放块移动音乐
     */
    public void playBlockMoveMusic() {
        mSoundPool.play(mBlockMoveId, 1, 1, 1, 0, 1);
    }


    /**
     * 音效播放广播接收器
     */
    public static class MusicPlayReceiver extends BroadcastReceiver {

        public static final String ACTION_PLAY_BLOCK_MOVE_MUSIC = "com.lear.game2048.service.MusicService.MusicPlayReceiver.ACTION_PLAY_BLOCK_MOVE_MUSIC";

        private WeakReference<MusicService> mService;

        public MusicPlayReceiver(MusicService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mService == null || mService.get() == null || intent.getAction() == null) return;
            switch (intent.getAction()) {
                case ACTION_PLAY_BLOCK_MOVE_MUSIC:
                    mService.get().playBlockMoveMusic();
                    break;
            }
        }
    }
}
