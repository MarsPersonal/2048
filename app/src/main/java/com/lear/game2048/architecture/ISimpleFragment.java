package com.lear.game2048.architecture;

import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * 简单Fragment框架
 */
public interface ISimpleFragment {

    /**
     * 获取Fragment
     *
     * @param tag 与Fragment绑定的Fragment
     * @return Fragment
     */
    Fragment get(String tag);

    /**
     * 添加Fragment
     *
     * @param fragment Fragment
     * @param tag      Fragment的名字，用于获取Fragment
     * @return 添加成功返回true
     */
    boolean add(Fragment fragment, String tag);

    /**
     * 移除Fragment
     *
     * @param tag 标签
     * @return 移除成功返回true
     */
    boolean remove(String tag);

    /**
     * 检查这tag是否为空
     *
     * @param tag 与Fragment绑定的tag
     * @return 如果这个绑定了Fragment，则返回true
     */
    boolean contains(String tag);

    /**
     * 清空
     */
    void clear();

    /**
     * 大小
     *
     * @return 返回Fragment数量
     */
    int size();

    /**
     * 设置保存的Fragment最大数量
     *
     * @param max 最大值
     */
    void setMaxCacheSize(int max);

    /**
     * 回收不常用的Fragment
     */
    void gc();

    /**
     * 显示
     *
     * @param tag 标签
     */
    void show(String tag);

    /**
     * 隐藏所有的Fragment
     */
    void hide();

    /**
     * fragment是否显示
     * @param tag   fragment想对应的tag
     * @return  如果在显示则返回true
     */
    boolean isShow(String tag);


}
