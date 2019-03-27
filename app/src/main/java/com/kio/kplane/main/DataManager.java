package com.kio.kplane.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;

import com.kio.kplane.GameActivity;
import com.kio.kplane.entity.Background;
import com.kio.kplane.entity.Cloud;
import com.kio.kplane.entity.Enemy;
import com.kio.kplane.entity.EnemyBullet;
import com.kio.kplane.entity.Player;
import com.kio.kplane.entity.PlayerBullet;
import com.kio.kplane.entity.UI;
import com.kio.kplane.utils.ObjectPool;

import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * 数据控制类，所有的系统数据都由该类控制
 * 使用单例模式，保证系统中只有一个数据控制
 * 实现了AssetLoader.AssetGetter接口，以便接收加载完成的资源
 * 开辟了新的线程进行数据的更新
 */
public class DataManager extends Thread implements AssetLoader.AssetGetter {

    private static DataManager instance;
    /**
     * 屏幕参数
     */
    public static RectF screenRectF;
    public static int screenWidth;
    public static int screenHeight;
    /**
     * 系统需要的一些资源
     */
    public Bitmap[] bitmaps;
    public Typeface[] typefaces;
    public Bitmap[] explosion;
    /**
     * 用于计算两次数据更新的时间间隔
     */
    private long start, end;

    /**
     * flag:用于对两次数据更新时间间隔的计算,loadOver:是否加载完成
     */
    private boolean flag;
    public boolean loadOver;
    /**
     * 游戏得分
     */
    public int score = 0;
    /**
     * 标志游戏状态
     */
    public static final int STATE_RUNNING = 0x884;
    public static final int STATE_GAME_PAUSE = 0x885;
    public static final int STATE_GAME_OVER = 0x886;
    public static final int STATE_EXIT = 0x8867;
    public int gameState = STATE_RUNNING;
    /**
     * 触碰事件
     */
    private int touchX, touchY;
    private boolean isTouching;
    /**
     * 最后是需要绘制的一些类
     */
    private UI ui;

    public Player player;

    private Background background;

    public final ArrayList<PlayerBullet> playerBulletList = new ArrayList<>();
    public ObjectPool<PlayerBullet> playerBulletPool = new ObjectPool<>(ObjectPool.INFINITY, playerBulletList);

    public final ArrayList<Cloud> cloudList = new ArrayList<>();
    public ObjectPool<Cloud> cloudPool = new ObjectPool<>(ObjectPool.INFINITY, cloudList);


    public final ArrayList<Enemy> enemyList = new ArrayList<>();
    public ObjectPool<Enemy> enemyPool = new ObjectPool<>(ObjectPool.INFINITY, enemyList);

    public final ArrayList<EnemyBullet> enemyBulletList = new ArrayList<>();
    public ObjectPool<EnemyBullet> enemyBulletPool = new ObjectPool<>(ObjectPool.INFINITY, enemyBulletList);


    private Context context;

    private DataManager() {

    }

    public static DataManager getInstance() {
        if (instance == null)
            return (instance = new DataManager());
        else return instance;
    }

    @Override
    public void callback(Bitmap[] bitmaps, Typeface[] typefaces) {
        this.bitmaps = bitmaps;
        this.typefaces = typefaces;

        init();
    }

    private void init() {
        this.background = new Background(bitmaps[0]);
        this.ui = new UI(bitmaps[12]);
        this.player = new Player(bitmaps[1]);
        loadOver = true;

        //初始化爆炸图片集合
        explosion = new Bitmap[32];
        int width = 3200;
        int height = 1600;

        int perWidth = width / 8;
        int perHeight = height / 4;

        Bitmap source = DataManager.getInstance().bitmaps[9];

        Matrix matrix = new Matrix();
        matrix.postScale(width / ((float) source.getWidth()), height / ((float) source.getHeight()));// 使用后乘
        Bitmap use = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);

        for (int i = 0; i < explosion.length; i++) {
            int xIndex = i % 8;
            int yIndex = i / 8;
            explosion[i] = Bitmap.createBitmap(use, perWidth * xIndex, perHeight * yIndex, perWidth, perHeight);
        }

        //一开始就生成一些白云

        for (int i = 0; i < 60; i++) {
            int index = (int) (Math.random() * 5) + 3;
            Cloud cloud = new Cloud(bitmaps[index]);

            cloud.setY((float) Math.random() * screenHeight - cloud.getHeight());

            cloudList.add(cloud);
        }

    }


    public void update() {
        long delta;

        if (flag) {
            start = System.currentTimeMillis();
            if (end == 0) end = start;
            delta = start - end;
        } else {
            end = System.currentTimeMillis();
            if (start == 0) start = end;
            delta = end - start;
        }

        flag = !flag;
        if (loadOver) {
            this.background.update(delta);
            this.ui.update(delta);
            this.player.update(delta);
            for (int i = 0; i < playerBulletList.size(); i++) {
                playerBulletList.get(i).update(delta);
            }
            for (int i = 0; i < enemyBulletList.size(); i++) {
                enemyBulletList.get(i).update(delta);
            }

            //生成白云
            if (delta > 0 && System.currentTimeMillis() % (100 + (int) (Math.random() * 100)) == 0) {
                Cloud cloud = cloudPool.getInstance();
                if (cloud == null) {
                    int index = (int) (Math.random() * 5) + 3;
                    cloud = new Cloud(bitmaps[index]);
                    cloudList.add(cloud);
                }

            }

            for (int i = 0; i < cloudList.size(); i++) {
                cloudList.get(i).update(delta);
            }


            if (gameState == DataManager.STATE_RUNNING) {
                //生成敌机
                if (delta > 0 && System.currentTimeMillis() % (1000 + (int) (Math.random() * 500)) == 0) {
                    Enemy enemy = enemyPool.getInstance();
                    if (enemy == null) {
                        enemy = new Enemy(bitmaps[8]);
                        enemyList.add(enemy);
                    }

                }
            }

            for (int i = 0; i < enemyList.size(); i++) {
                enemyList.get(i).update(delta);
            }
            checkCollide();
        }
    }

    private void checkCollide() {
        //检测敌机与玩家的子弹,敌机与玩家
        for (int i = 0; i < enemyList.size(); i++) {
            for (int j = 0; j < playerBulletList.size(); j++)
                enemyList.get(i).collideWith(playerBulletList.get(j));
            enemyList.get(i).collideWith(player);
        }

        //检测玩家与敌机的子弹,玩家的子弹与敌机的子弹
        for (int i = 0; i < enemyBulletList.size(); i++) {
            for (int j = 0; j < playerBulletList.size(); j++) {
                playerBulletList.get(j).collideWith(enemyBulletList.get(i));
            }
            enemyBulletList.get(i).collideWith(player);
        }

    }

    public void draw(Canvas canvas) {

        this.background.draw(canvas);

        for (int i = 0; i < cloudList.size(); i++) {
            cloudList.get(i).draw(canvas);
        }

        for (int i = 0; i < enemyList.size(); i++) {
            enemyList.get(i).draw(canvas);
        }

        for (int i = 0; i < playerBulletList.size(); i++) {
            playerBulletList.get(i).draw(canvas);
        }

        for (int i = 0; i < enemyBulletList.size(); i++) {
            enemyBulletList.get(i).draw(canvas);
        }

        this.player.draw(canvas);
        this.ui.draw(canvas);
    }

    @Override
    public void run() {

        //通知绘图线程可以开始了
        synchronized (this) {
            this.notifyAll();
        }
        while (gameState != STATE_EXIT) {
            update();
        }
    }

    public void handlerTouch(MotionEvent event) {
        this.touchX = (int) event.getX();
        this.touchY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN)
            this.isTouching = true;
        else if (event.getAction() == MotionEvent.ACTION_UP)
            this.isTouching = false;
    }

    public boolean isTouching() {
        return this.isTouching;
    }

    public float getTouchX() {
        return (float) touchX;
    }

    public float getTouchY() {
        return (float) touchY;
    }

    public void createNewPlayerBullet() {

        PlayerBullet bullet = playerBulletPool.getInstance();
        if (bullet == null) {
            bullet = new PlayerBullet(bitmaps[2], player.getX(), player.getY());
            playerBulletList.add(bullet);
        }

    }

    public void createNewEnemyBullet(float x, float y) {
        EnemyBullet bullet = enemyBulletPool.getInstance();
        if (bullet == null) {
            bullet = new EnemyBullet(bitmaps[10], x, y);
            enemyBulletList.add(bullet);
        } else {
            bullet.setX(x);
            bullet.setY(y);
        }
    }

    public void recyclePlayerBullet(PlayerBullet playerBullet) {
        try {

            this.playerBulletPool.recycle(playerBullet);

        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    public void recycleEnemy(Enemy enemy) {
        try {
            this.enemyPool.recycle(enemy);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    public void recyclePlayerCloud(Cloud cloud) {
        try {

            this.cloudPool.recycle(cloud);

        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    public void restart() {
        score = 0;
        player.setX(DataManager.screenWidth / 2f - 100);
        player.setY(DataManager.screenHeight - 400);
        player.isAlive = true;
        gameState = DataManager.STATE_RUNNING;
        GameActivity activity = (GameActivity) context;
        activity.goRecord();
    }

    public void recycleEnemyBullet(EnemyBullet enemyBullet) {
        try {
            this.enemyBulletPool.recycle(enemyBullet);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    public void setTouching(boolean b) {
        this.isTouching = b;
    }

    public void start(Context context) {
        this.context = context;
        this.start();
    }

    public void saveScore() {
        GameActivity activity = (GameActivity) context;
        SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
        int first = preferences.getInt("first",0);
        int second = preferences.getInt("second",0);
        int third = preferences.getInt("third",0);

        SharedPreferences.Editor editor = preferences.edit();

        if (score>first){
            editor.putInt("first",score);
            editor.apply();
        }else if (score>second){
            editor.putInt("second",score);
            editor.putInt("third",second);
            editor.apply();
        }else if(score>third){
            editor.putInt("third",score);
            editor.apply();
        }
        activity.goRecord();
    }
}
