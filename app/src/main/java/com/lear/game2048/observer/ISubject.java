package com.lear.game2048.observer;


/**
 * 被观察者接口
 */
public interface ISubject {

    /**
     * 注册观察者对象
     *
     * @param observer 观察者
     */
    void attach(IObserver observer);

    /**
     * 注销观察者
     *
     * @param observer 观察者
     */
    void detach(IObserver observer);

    /**
     * 通知观察者
     */
    void notifyObserver();

}
