package com.kio.kplane.main;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import java.io.IOException;


public class AssetLoader {
    private Context context;
    private AssetGetter assetGetter;
    private LoadCompeleteListener loadCompeleteListener;

    public void setAssetGetter(AssetGetter assetGetter) {
        this.assetGetter = assetGetter;
    }

    public void setLoadCompeleteListener(LoadCompeleteListener loadCompeleteListener) {
        this.loadCompeleteListener = loadCompeleteListener;
    }

    public interface AssetGetter {
        void callback(Bitmap[] bitmaps, Typeface[] typefaces);

    }

    public interface LoadCompeleteListener {
        void onLoadCompelete();
    }

    public AssetLoader(Context context) {
        this.context = context;
    }

    public void loadAssets() {


        //加载图片资源
        String[] bitmapPaths = new String[13];
        bitmapPaths[0] = "images/background.png";
        bitmapPaths[1] = "images/player.png";
        bitmapPaths[2] = "images/bullet_player.png";

        bitmapPaths[3] = "images/clouds/cloud1.png";
        bitmapPaths[4] = "images/clouds/cloud2.png";
        bitmapPaths[5] = "images/clouds/cloud3.png";
        bitmapPaths[6] = "images/clouds/cloud4.png";
        bitmapPaths[7] = "images/clouds/cloud5.png";

        bitmapPaths[8] = "images/enemy.png";

        bitmapPaths[9] = "images/explosion.png";

        bitmapPaths[10] = "images/bullet_enemy.png";

        bitmapPaths[11] = "images/button.png";

        bitmapPaths[12] = "images/ui_back.png";

        AssetManager assetManager = context.getAssets();

        Bitmap[] bitmaps = new Bitmap[bitmapPaths.length];

        for (int i = 0; i < bitmapPaths.length; i++) {
            try {
                bitmaps[i] = BitmapFactory.decodeStream(assetManager.open(bitmapPaths[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //加载字体资源

        String[] typefacePaths = new String[1];

        typefacePaths[0] = "fonts/english.TTF";

        Typeface[] typefaces = new Typeface[typefacePaths.length];

        for (int i = 0; i < typefaces.length; i++) {
            typefaces[i] = Typeface.createFromAsset(assetManager, typefacePaths[0]);
        }


        if (this.loadCompeleteListener != null)
            loadCompeleteListener.onLoadCompelete();

        if (this.assetGetter != null)
            assetGetter.callback(bitmaps, typefaces);

    }

}
