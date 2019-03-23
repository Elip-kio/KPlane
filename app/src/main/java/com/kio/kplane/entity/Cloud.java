package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.utils.ObjectPool;


public class Cloud extends Sprite implements ObjectPool.Poolable {

    private float speed;

    private Paint p;

    public Cloud(Bitmap bitmap) {
        super((float) Math.random() * DataManager.screenWidth - bitmap.getWidth() / 2f, -bitmap.getHeight(), bitmap);
        speed = (float) Math.random() * 0.2f + 0.2f;
        p = new Paint();
        p.setAlpha((int) (Math.random() * 205) + 50);
    }

    @Override
    public void update(long delta) {
        this.setY(this.getY() + delta * speed);
        if (this.getY() > DataManager.screenHeight) {
            DataManager.getInstance().recyclePlayerCloud(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {

        canvas.drawBitmap(getAsset(), getX(), getY(), p);
    }

    @Override
    public void beforeReuse() {
        this.speed = (float) Math.random() * 0.2f + 0.2f;
//        int index = (int) (Math.random() * 5) + 3;
//        Bitmap bitmap = DataManager.getInstance().bitmaps[index];
//        this.setAsset(bitmap);
//        this.setX((float) Math.random()*DataManager.screenWidth-bitmap.getWidth()/2f);
        this.setY(-this.getHeight());
    }

    @Override
    public void beforeRecycle() {

    }
}
