package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;

public class Player extends Sprite {

    public boolean isAlive = true;

    private Explosion explosion;

    public Player(Bitmap bitmap) {
        super((float) DataManager.screenWidth / 2 - 100, (float) DataManager.screenHeight - 400, 200, 200, bitmap);
    }

    @Override
    public void update(long delta) {

        if (!isAlive) {
            explosion.update(delta);
        }

        if (DataManager.getInstance().gameState != DataManager.STATE_RUNNING)
            return;


        if (DataManager.getInstance().isTouching()) {
            float tx = DataManager.getInstance().getTouchX() - 100;
            float ty = DataManager.getInstance().getTouchY() - 100;


            this.setX(this.getX() + delta * 0.01f * (tx - this.getX()));
            this.setY(this.getY() + delta * 0.01f * (ty - this.getY()));
            if (this.getX()>=DataManager.screenWidth-getWidth()-20)
                this.setX(DataManager.screenWidth-getWidth()-20);
            if (this.getY()>=DataManager.screenHeight-getHeight()-20)
                this.setY(DataManager.screenHeight-getHeight()-20);

            if (this.getY()<20)
                this.setY(20);
            if (this.getX()<20)
                this.setX(20);

        }

        if (System.currentTimeMillis() % 300 == 0 && delta != 0)
            DataManager.getInstance().createNewPlayerBullet();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isAlive)
            canvas.drawBitmap(getAsset(), getX(), getY(), null);
        else
            explosion.draw(canvas);
    }

    @Override
    public void onCollide(Sprite other) {
        if (!isAlive)
            return;

        if (other instanceof Enemy) {
            Enemy enemy = (Enemy) other;
            if (!enemy.isAlive) {
                this.isAlive = false;
                explosion = new Explosion((int) getX(), (int) getY());
                DataManager.getInstance().gameState = DataManager.STATE_GAME_OVER;
                DataManager.getInstance().saveScore();
            }
        } else {
            this.isAlive = false;
            explosion = new Explosion((int) getX(), (int) getY());
            DataManager.getInstance().gameState = DataManager.STATE_GAME_OVER;
            DataManager.getInstance().saveScore();
        }

    }

    @Override
    public RectF getArea() {
        return new RectF(getX() + 25, getY() + 25, getWidth() + getX() - 25, getHeight() + getY() - 25);
    }
}
