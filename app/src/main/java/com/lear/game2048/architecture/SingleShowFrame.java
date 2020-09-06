package com.lear.game2048.architecture;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 单Fragment显示框架
 */
public class SingleShowFrame implements ISimpleFragment {

    private static final String TAG = "SingleShowFrame";

    private List<DataStruct> mList;
    private int mMaxCacheSize;
    private int mQuoteCount = 0;//引用计数器

    private AppCompatActivity mContext;
    private DataStruct mCurrentShow;

    private int mViewId;


    public SingleShowFrame(AppCompatActivity activity, int viewId) {
        mList = new LinkedList<>();
        mContext = activity;
        mMaxCacheSize = 4;
        mViewId = viewId;
    }

    @Override
    public Fragment get(String tag) {
        DataStruct struct = getStruct(tag);
        return struct != null ? struct.fragment : null;
    }

    @Override
    public boolean add(Fragment fragment, String tag) {
        if (contains(tag)) throw new RuntimeException("tag:" + tag + "已存在");
        DataStruct struct = new DataStruct(tag, fragment);
        boolean flag = mList.add(struct);
        Log.i(TAG, "add: maxCacheSize=" + mMaxCacheSize + " size=" + mList.size());
        return flag;
    }

    @Override
    public boolean remove(String tag) {
        if (!contains(tag)) return false;

        if (mCurrentShow.tag.equals(tag)) {

            FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
            transaction.remove(mCurrentShow.fragment);
            mCurrentShow.isQuote = false;
            mCurrentShow.fragment = null;
            mCurrentShow.tag = "";
            mQuoteCount--;
        }

        Iterator<DataStruct> iterator = mList.iterator();
        DataStruct struct;
        boolean flag = false;
        while (iterator.hasNext()) {
            struct = iterator.next();

            if (struct.tag.equals(tag)) {
                //如果被引用，在FragmentManager里
                flag = true;
                if (struct.isQuote) {
                    mContext.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(struct.fragment)
                            .commit();
                    struct.isQuote = false;
                    struct.fragment = null;
                    mQuoteCount--;
                }

                iterator.remove();
                break;
            }
        }
        Log.i(TAG, "remove: maxCacheSize=" + mMaxCacheSize + " size=" + mList.size());
        return flag;
    }

    @Override
    public boolean contains(String tag) {
        Iterator<DataStruct> iterator = mList.iterator();
        DataStruct struct;
        while (iterator.hasNext()) {
            struct = iterator.next();
            if (struct.tag.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        FragmentTransaction transaction = mContext.getSupportFragmentManager()
                .beginTransaction();

        if (mCurrentShow != null && mCurrentShow.isQuote) {
            transaction.remove(mCurrentShow.fragment);
            mCurrentShow.isQuote = false;
            mCurrentShow.fragment = null;
            mCurrentShow.tag = null;
            mCurrentShow = null;
        }

        Iterator<DataStruct> iterator = mList.iterator();
        DataStruct struct;

        while (iterator.hasNext()) {
            struct = iterator.next();
            if (struct.isQuote) {
                transaction.remove(struct.fragment);
                struct.isQuote = false;
                mQuoteCount--;
            }
            struct.fragment = null;
            struct.tag = "";
            iterator.remove();
        }

        transaction.commit();
    }

    @Override
    public int size() {
        return mList.size();
    }

    @Override
    public void setMaxCacheSize(int max) {
        mMaxCacheSize = max;
    }

    @Override
    public void gc() {

        if (mList.size() <= mMaxCacheSize) return;
        Iterator<DataStruct> iterator = mList.iterator();
        DataStruct struct;

        StringBuilder builder = new StringBuilder("移除tag:");

        //第一次循环移除没有被引用的fragment
        while (mList.size() > mMaxCacheSize && iterator.hasNext()) {

            struct = iterator.next();
            //如果没有被引用，则直接移除
            if (!struct.isQuote) {

                builder.append(struct.tag).append(",");

                struct.fragment = null;

                iterator.remove();

            }

        }

        //如果第一次循环后缓存数量还是大于最大缓存大小
        //则移除已被引用的fragment
        iterator = mList.iterator();
        FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
        while (mList.size() > mMaxCacheSize && iterator.hasNext()) {

            struct = iterator.next();

            if (struct.isQuote) {
                builder.append(struct.tag).append(",");

                transaction.remove(struct.fragment);
                struct.isQuote = false;
                struct.tag = "";
                struct.fragment = null;

                mQuoteCount--;
                iterator.remove();

            }

        }
        transaction.commit();
        Log.i(TAG, "gc: maxCacheSize=" + mMaxCacheSize + " size=" + mList.size());
        Log.i(TAG, "gc: " + builder.toString());

    }

    @Override
    public void show(String tag) {
        if (!contains(tag)) return;

        FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
        //隐藏当前的fragment
        if (mCurrentShow != null && mCurrentShow.fragment != null) {
            transaction.hide(mCurrentShow.fragment);
            mCurrentShow.isQuote = true;
        }

        //找到对应的DataStruct
        DataStruct struct = getStruct(tag);
        //添加或显示
        if (struct != null) {
            mList.remove(struct);
            mList.add(struct);
            if (!struct.isQuote) {
                transaction.add(mViewId, struct.fragment, struct.tag);
                mQuoteCount++;
            }
            transaction.show(struct.fragment);
            struct.isQuote = true;
            mCurrentShow = struct;
        }

        transaction.commitAllowingStateLoss();

        if (mList.size() > mMaxCacheSize) {
            gc();
        }
    }

    @Override
    public void hide() {

        FragmentTransaction t = mContext.getSupportFragmentManager().beginTransaction();
        if (mCurrentShow.isQuote) {
            t.hide(mCurrentShow.fragment);
        }

        for (DataStruct data : mList) {
            if (data.isQuote) {
                t.hide(data.fragment);
            }
        }

        t.commit();
    }

    @Override
    public boolean isShow(String tag) {
        DataStruct struct = getStruct(tag);
        if (struct == null) return false;
        return struct.isQuote;
    }

    @Nullable
    private DataStruct getStruct(String tag) {
        DataStruct struct = null;
        for (DataStruct s : mList) {
            if (s.tag.equals(tag)) {
                struct = s;
                break;
            }
        }
        return struct;
    }

    private static class DataStruct {

        String tag;
        Fragment fragment;
        boolean isQuote;  //是否被引用

        public DataStruct(String tag, Fragment fragment) {
            this.tag = tag;
            this.fragment = fragment;
            this.isQuote = false;
        }
    }
}
