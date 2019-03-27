package com.kio.kplane.entity;

import android.graphics.Canvas;

import com.kio.kplane.main.DataManager;

public class Explosion extends Sprite {


    float index = 0;
    public boolean isOver = false;


    public Explosion(int x, int y) {
        super(x, y, 400, 400, DataManager.getInstance().explosion[0]);
    }

    @Override
    public void update(long delta) {
        if (index > 32){
            isOver = true;
        }
        else {
            index += delta * 0.05f;
        }

    }

    @Override
    public void draw(Canvas canvas) {
        if (!isOver) {
            canvas.drawBitmap(DataManager.getInstance().explosion[(int) (index<32?index:31)], getX(), getY(), null);
        }
    }
}
