package com.kio.kplane;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.kio.kplane.main.AssetLoader;
import com.kio.kplane.main.DataManager;

/**
 * @author kio
 * 资源加载完成前使用的Activity
 */
public class LoadActivity extends AppCompatActivity implements AssetLoader.LoadCompeleteListener {

    private Animation loadAnimation;
    private ProgressBar loadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setWindowAnimations(R.style.fade);
        setContentView(R.layout.activity_load);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);


        DataManager.screenWidth = outMetrics.widthPixels;
        DataManager.screenHeight = outMetrics.heightPixels;
        DataManager.screenRectF = new RectF(0,0,DataManager.screenWidth,DataManager.screenHeight);

        LinearLayout content = findViewById(R.id.content);

        loadProgress = findViewById(R.id.load_progress);

        content.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        loadAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);

        loadAnimation.setDuration(1500);


        loadProgress.startAnimation(loadAnimation);

        final AssetLoader loader = new AssetLoader(this);

        loader.setLoadCompeleteListener(this);
        loader.setAssetGetter(DataManager.getInstance());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loader.loadAssets();
            }
        }).start();

    }


    @Override
    public void onLoadCompelete() {

        if (loadAnimation != null) {
            loadAnimation.cancel();
            loadProgress.setScaleX(1);
        }


        Intent i = new Intent(LoadActivity.this, GameActivity.class);
        LoadActivity.this.startActivity(i);
        LoadActivity.this.finish();

    }
}
