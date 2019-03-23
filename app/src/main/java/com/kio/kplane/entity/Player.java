package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;

public class Player extends Sprite {


    public Player(Bitmap bitmap) {
        super((float) DataManager.screenWidth / 2 - 100, (float) DataManager.screenHeight - 400, 200, 200, bitmap);
    }

    @Override
    public void update(long delta) {

        if (DataManager.getInstance().gameState != DataManager.STATE_RUNNING)
            return;

        if (DataManager.getInstance().isTouching()) {
            float tx = DataManager.getInstance().getTouchX() - 100;
            float ty = DataManager.getInstance().getTouchY() - 100;


            this.setX(this.getX() + delta * 0.01f * (tx - this.getX()));
            this.setY(this.getY() + delta * 0.01f * (ty - this.getY()));
        }

        if (System.currentTimeMillis() % 400 == 0)
            DataManager.getInstance().createNewPlayerBullet();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getAsset(), getX(), getY(), null);
    }

    @Override
    public void onCollide(Sprite other) {

        if (other instanceof Enemy) {
            Enemy enemy = (Enemy) other;
            if (!enemy.isAlive) {
                DataManager.getInstance().gameState = DataManager.STATE_GAME_OVER;
                this.setX(DataManager.screenWidth / 2f - 100);
                this.setY(DataManager.screenHeight - 400);
            }
        }

    }

    @Override
    public RectF getArea() {
        return new RectF(getX() + 50, getY() + 50, getWidth() + getX() - 50, getHeight() + getY() - 50);
    }
}
