package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.utils.ObjectPool;

public class Enemy extends Sprite implements ObjectPool.Poolable {

    public boolean isAlive = true;

    private Explosion explosion;

    private float xSpeed;

    public Enemy(Bitmap bitmap) {
        super((float) (Math.random() * (DataManager.screenWidth - 300)), -300, 200, 300, bitmap);
        xSpeed = (float) Math.random();
    }

    @Override
    public void update(long delta) {
        if (isAlive) {
            setY(getY() + delta);
            setX(getX() + delta * xSpeed);
            if ((getX()<0||getX()>DataManager.screenWidth-400)&&delta>0)
                xSpeed = -xSpeed;
            if (System.currentTimeMillis() % 400 == 0 && delta != 0){
                DataManager.getInstance().createNewEnemyBullet(this.getX() + 175, this.getY() + 100);
                DataManager.getInstance().createNewEnemyBullet(this.getX() - 25, this.getY() + 100);
            }
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
        if (isAlive) {
            if (DataManager.getInstance().gameState == DataManager.STATE_RUNNING) {
                canvas.drawBitmap(getAsset(), getX(), getY(), null);
            }
        } else if (explosion!=null)
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
        if (!isAlive) return;
        boolean flag = false;

        if (other instanceof Player) {
            Player player = (Player) other;
            if (player.isAlive)
                flag = true;
        } else
            flag = true;

        if (flag) {
            isAlive = false;
            explosion = new Explosion((int) getX(), (int) getY());
            DataManager.getInstance().score++;
        }

    }

    @Override
    public RectF getArea() {
        return new RectF(getX() + 30, getY() + 30, getWidth() + getX() - 30, getHeight() + getY() - 30);
    }
}
