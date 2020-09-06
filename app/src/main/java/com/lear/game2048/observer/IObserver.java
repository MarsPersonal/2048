package com.lear.game2048.observer;

/**
 * 观察者接口
 */
public interface IObserver {

    /**
     * 更新
     *
     * @param subject 被观察者
     * @param object  对象
     */
    void update(ISubject subject, Object object);

}
