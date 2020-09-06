package com.lear.game2048.utils;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

public class MathCalculationUtilsTest {

    @Test
    public void getAngle() {

        float angle;
        float x, y;
        double a = 0;
        for (int i = 0; i < 360; i++) {
            x = (float) (Math.cos(a) * 100);
            y = (float) (Math.sin(a) * 100);
            angle = MathCalculationUtils.getAngle(0, 0, x, y);
            System.out.println("角度：" + i + "=" + angle);
            a += 0.01f;
        }
    }
}