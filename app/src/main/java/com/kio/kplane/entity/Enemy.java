package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.utils.ObjectPool;

public class Enemy extends Sprite implements ObjectPool.Poolable {

    public boolean isAlive = true;

    private Explosion explosion;

    public Enemy(Bitmap bitmap) {
        super((float) (Math.random() * (DataManager.screenWidth - 400)), -400, 400, 400, bitmap);
    }

    @Override
    public void update(long delta) {
        if (isAlive) {
            setY(getY() + delta);
            if (getY() > DataManager.screenHeight)
                DataManager.getInstance().recycleEnemy(this);
        } else {
            explosion.update(delta);
            if (explosion.isOver)
                DataManager.getInstance().recycleEnemy(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (isAlive){
            if(DataManager.getInstance().gameState==DataManager.STATE_RUNNING){
                canvas.drawBitmap(getAsset(), getX(), getY(), null);
            }
        }
        else
            explosion.draw(canvas);
    }

    @Override
    public void beforeReuse() {
        this.setY(-200);
        this.setX((float) Math.random() * (DataManager.screenWidth - 400));
        this.isAlive = true;
        this.explosion = null;
    }

    @Override
    public void beforeRecycle() {

    }

    @Override
    public void onCollide(Sprite other) {
        if(!isAlive)return;
        isAlive = false;
        explosion = new Explosion((int) getX(), (int) getY());
        DataManager.getInstance().score++;
    }

    @Override
    public RectF getArea() {
        return new RectF(getX() + 50, getY() + 50, getWidth() + getX() - 50, getHeight() + getY() - 50);
    }
}
