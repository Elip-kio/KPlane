package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;

public class UI extends Sprite {

    private Paint p;

    private RectF buttonRect;

    private Bitmap button;

    public UI(Bitmap bitmap) {
        super((float) DataManager.screenWidth / 2 - 400, (float) DataManager.screenHeight / 2 - 300,800,600, bitmap);
        p = new Paint();
        button = Sprite.scaleBitmap(DataManager.getInstance().bitmaps[11],600,200);
        buttonRect = new RectF(this.getX()+100, this.getY()+300, 700 + this.getX(), 500 + this.getY());
    }

    @Override
    public void update(long delta) {
        DataManager manager = DataManager.getInstance();
        if (manager.gameState == DataManager.STATE_GAME_OVER) {
            if (manager.isTouching()) {
                if (buttonRect.contains(manager.getTouchX(), manager.getTouchY())){
                    manager.restart();
                    manager.setTouching(false);
                }

            }
        }
    }

    @Override
    public void draw(Canvas canvas) {

        int state = DataManager.getInstance().gameState;
        RectF rect;


        p.setTypeface(DataManager.getInstance().typefaces[0]);
        p.setTextAlign(Paint.Align.CENTER);

        p.setColor(Color.BLACK);
        p.setTextSize(200f);
        canvas.drawText("" + DataManager.getInstance().score, DataManager.screenWidth / 2f, 180, p);

        switch (state) {
            case DataManager.STATE_GAME_OVER:
                canvas.drawBitmap(getAsset(),getX(),getY(),p);
                canvas.drawBitmap(button,getX()+100,getY()+300,p);
                p.setTextSize(100f);
                p.setColor(0xffcc0000);
                canvas.drawText("Game Over",getX() + 400,getY() + 220,p);
                p.setColor(Color.LTGRAY);
                canvas.drawText("重   来", getX() + 400, getY() + 430, p);
                break;
            case DataManager.STATE_GAME_PAUSE:
                p = new Paint();
                p.setColor(Color.RED);
                p.setTextSize(100);
                rect = new RectF(this.getX(), this.getY(), 200 + this.getX(), 200 + this.getY());
                canvas.drawRect(rect, p);
                p.setColor(Color.BLACK);
                p.setTextSize(25);
                canvas.drawText("继续", getX(), getY(), p);
                break;
        }

    }
}
