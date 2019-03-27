package com.kio.kplane.main;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class Stage extends SurfaceView implements SurfaceHolder.Callback, Runnable, View.OnTouchListener {
    public boolean isAlive;
    private SurfaceHolder mHolder;
    private Context context;
    public Stage(Context context) {
        super(context);
        this.context = context;
        this.mHolder = getHolder();
        mHolder.addCallback(this);
        this.setKeepScreenOn(true);
        this.setOnTouchListener(this);
    }

    public Stage(Context context, AttributeSet attrs) {
        this(context);
    }

    public Stage(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("SSS", "holder create");
        isAlive = true;
        DataManager.getInstance().start(context);
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {

        //等待更新线程先启动
        if (!DataManager.getInstance().loadOver)
            synchronized (DataManager.getInstance()) {
                try {
                    DataManager.getInstance().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        while (isAlive) {
            Canvas canvas = null;
            try {
                //尝试使用GPU进行渲染
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    canvas = mHolder.lockHardwareCanvas();
                } else {
                    canvas = mHolder.lockCanvas();
                }
                if (canvas != null) {
                    DataManager.getInstance().draw(canvas);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }

            }

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        DataManager.getInstance().handlerTouch(event);
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
