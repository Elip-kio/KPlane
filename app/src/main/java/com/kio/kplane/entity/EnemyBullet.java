package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.utils.ObjectPool;

public class EnemyBullet extends Sprite implements ObjectPool.Poolable {

    private boolean isAlive = false;

    public EnemyBullet(Bitmap bitmap, float x, float y) {
        super(x + 50, y, 100, 100, bitmap);
    }

    @Override
    public void update(long delta) {
        if (isAlive) {
            this.setY(this.getY() + 2f*delta);
        }

        if (this.getY() > DataManager.screenHeight || !isAlive) {
            DataManager.getInstance().recycleEnemyBullet(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getAsset(), getX(), getY(), null);
    }

    @Override
    public void beforeReuse() {
        this.isAlive = true;
    }

    @Override
    public void beforeRecycle() {

    }

    @Override
    public void onCollide(Sprite other) {
        this.isAlive = false;
    }

    @Override
    public RectF getArea() {
        return new RectF(getX() + 20, getY() + 20, getWidth() + getX() - 20, getHeight() + getY() - 20);
    }

}
