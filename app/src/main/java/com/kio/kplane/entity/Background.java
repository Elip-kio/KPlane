package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.kio.kplane.main.DataManager;

public class Background extends Sprite {


    public Background(Bitmap asset) {
        super(0, -DataManager.screenHeight, DataManager.screenWidth, 2 * DataManager.screenHeight, asset);
    }

    @Override
    public void update(long delta) {
        this.setY(this.getY() + delta * 0.1f);
        if (this.getY() >= 0)
            this.setY(-DataManager.screenHeight);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getAsset(), getX(), getY(), null);
    }
}
