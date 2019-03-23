package com.kio.kplane.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.kio.kplane.main.DataManager;

public class UI extends Sprite {

    private Paint p;

    private RectF board;

    public UI(Bitmap bitmap) {
        super((float) DataManager.screenWidth / 2 - 100, (float) DataManager.screenHeight / 2 - 100, bitmap);
        p = new Paint();
        board = new RectF(this.getX(), this.getY(), 200 + this.getX(), 200 + this.getY());
    }

    @Override
    public void update(long delta) {
        DataManager manager = DataManager.getInstance();
        if (manager.gameState==DataManager.STATE_GAME_OVER){
            if (manager.isTouching()){
                if (board.contains(manager.getTouchX(),manager.getTouchY()))
                    manager.restart();
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
                p.setColor(Color.RED);
                p.setTextSize(100);
                canvas.drawRect(board, p);
                p.setColor(Color.WHITE);
                canvas.drawText("重来", getX() + 100, getY() + 100, p);
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
