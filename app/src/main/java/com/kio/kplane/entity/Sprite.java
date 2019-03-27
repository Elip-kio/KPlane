package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;


public abstract class Sprite {

    private float x, y, width, height;
    private Bitmap asset;

    public static Bitmap scaleBitmap(Bitmap origin, float width, float height) {
        Matrix matrix = new Matrix();
        matrix.postScale(width / (origin.getWidth()), height / (origin.getHeight()));// 使用后乘
        return Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), matrix, false);

    }

    public Sprite(float x, float y, float width, float height, Bitmap asset) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.asset = scaleBitmap(asset,width,height);
    }

    public Sprite(float x, float y, Bitmap asset) {
        this.x = x;
        this.y = y;
        this.asset = asset;
    }

    public abstract void update(long delta);

    public abstract void draw(Canvas canvas);

    public final float getY() {
        return y;
    }

    public synchronized final void setY(float y) {
        this.y = y;
    }

    public final float getX() {
        return x;
    }

    public synchronized final void setX(float x) {
        this.x = x;
    }

    public final float getWidth() {
        return asset.getWidth();
    }

    public synchronized final void setWidth(float width) {
        this.width = width;
        this.asset = scaleBitmap(this.asset,width,height);
    }

    public final float getHeight() {
        return asset.getHeight();
    }

    public synchronized final void setHeight(float height) {
        this.height = height;
        this.asset = scaleBitmap(this.asset,width,height);
    }

    public final Bitmap getAsset() {
        return asset;
    }

    public final void setAsset(Bitmap asset) {
        this.asset = asset;
    }

    public void onCollide(Sprite other) {
    }

    public RectF getArea() {
        return new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight());
    }

    public void collideWith(Sprite other) {

        RectF area = other.getArea();
        RectF mArea = this.getArea();
        RectF sArea = DataManager.screenRectF;

        if (sArea.contains(mArea) && sArea.contains(area) && area.intersect(mArea)) {
            this.onCollide(other);
            other.onCollide(this);
        }

    }
}
