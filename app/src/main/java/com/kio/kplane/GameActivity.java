package com.kio.kplane;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.main.Stage;

import java.io.IOException;


public class GameActivity extends AppCompatActivity {

    MediaPlayer player;

    Stage stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.fade);
        setContentView(R.layout.activity_game);

        stage = findViewById(R.id.main_stage);

        AssetManager assetManager = this.getAssets();
        AssetFileDescriptor descriptor = null;

        try {
            descriptor = assetManager.openFd("sounds/background.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //播放背景音乐
        player = new MediaPlayer();
        player.setLooping(true);
        player.reset();
        try {
            if (descriptor != null) {
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                player.prepare();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.start();
                    }
                }).start();

                System.out.println("Playing");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            DataManager.getInstance().gameState = DataManager.STATE_EXIT;
        }

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            DataManager.getInstance().gameState = DataManager.STATE_GAME_PAUSE;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.player.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (player != null)
            player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            this.player.stop();
            this.player.release();
        }
        stage.isAlive = false;
    }
}
