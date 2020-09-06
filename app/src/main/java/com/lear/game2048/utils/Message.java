package com.lear.game2048.utils;

public class Message {
    public int what;
    public int arg1;
    public int arg2;
    public Object obj;
    public String message;

    public Message() {
        this(-1);
    }

    public Message(int what) {
        this.what = what;
    }
}
