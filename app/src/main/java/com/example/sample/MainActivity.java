package com.example.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class MainActivity extends Activity {
    //view([view]オブジェクトを格納する変数)の宣言
    private View view;

    //ハンドラを作成
    private Handler handler = new Handler();
    //ビューの再描画感覚（ミリ秒）
    private final static long MSEC = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //「GameView」オブジェクト（ビュー）の作成
        view = new GameView(this);

        //アクティビティにビューを組み込む
        setContentView(view);

        //ビュー再描画タイマー
        //タイマーを作成
        Timer timer = new Timer(false);
        //「MSEC」ミリ秒ごとにタスク（TimerTask）を実行
        timer.schedule(new TimerTask(){
            public void run(){
                handler.post(new Runnable(){
                    public void run(){
                        //ビューを再描写
                        view.invalidate();
                    }});
            }
        },0, MSEC);
    }
}
