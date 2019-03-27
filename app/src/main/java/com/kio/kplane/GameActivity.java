package com.kio.kplane;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.kio.kplane.main.DataManager;
import com.kio.kplane.main.Stage;

import java.io.IOException;


public class GameActivity extends AppCompatActivity {

    MediaPlayer player;

    Stage stage;

    GridLayout gridLayout;
    TextView score1, score2, score3;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (gridLayout.getVisibility() == View.GONE) {
                SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
                score1.setText(String.format(getResources().getString(R.string.scoreInt), preferences.getInt("first", 0)));
                score2.setText(String.format(getResources().getString(R.string.scoreInt), preferences.getInt("second", 0)));
                score3.setText(String.format(getResources().getString(R.string.scoreInt), preferences.getInt("third", 0)));
                gridLayout.setVisibility(View.VISIBLE);
            } else
                gridLayout.setVisibility(View.GONE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.fade);
        setContentView(R.layout.activity_game);

        stage = findViewById(R.id.main_stage);

        this.score1 = findViewById(R.id.first);
        this.score2 = findViewById(R.id.second);
        this.score3 = findViewById(R.id.third);

        AssetManager assetManager = this.getAssets();
        AssetFileDescriptor descriptor = null;

        gridLayout = findViewById(R.id.record_board);
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
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                player.seekTo(0);
                                player.start();
                            }
                        });
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
        System.out.println(stage);
        stage.isAlive = false;
    }

    public void goRecord() {
        handler.sendEmptyMessage(0x123);
    }

}

