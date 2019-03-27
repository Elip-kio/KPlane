package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.utils.ObjectPool;


public class PlayerBullet extends Sprite implements ObjectPool.Poolable {

    private boolean isAlive = false;

    public PlayerBullet(Bitmap bitmap, float x, float y) {
        super(x + 50, y, 100, 100, bitmap);
    }

    @Override
    public void update(long delta) {
        if (isAlive) {
            this.setY(this.getY() - delta);
        }

        if (this.getY() < -200 || !isAlive) {
            DataManager.getInstance().recyclePlayerBullet(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getAsset(), getX(), getY(), null);
    }

    @Override
    public void beforeReuse() {
        float px = DataManager.getInstance().player.getX();
        float py = DataManager.getInstance().player.getY();

        this.setX(px + 50);
        this.setY(py);

        this.isAlive = true;
    }

    @Override
    public void beforeRecycle() {

    }

    @Override
    public void onCollide(Sprite other) {
        if (other instanceof Enemy) {
            Enemy enemy = (Enemy) other;
            if (enemy.isAlive)
                this.isAlive = false;
        }
        if (other instanceof EnemyBullet)
            this.isAlive = false;

    }

    @Override
    public RectF getArea() {
        return new RectF(getX() + 20, getY() + 20, getWidth() + getX() - 20, getHeight() + getY() - 20);
    }
}
