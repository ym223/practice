package com.example.sample;

import android.graphics.Canvas;
import android.view.View;
import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.graphics.Paint;

import android.view.MotionEvent;

import android.graphics.Paint.Align;

public class GameView extends View {
    //ゲームの状態
    public final static int GAME_START = 0;
    public final static int GAME_PLAY = 1;
    public final static int GAME_OVER = 2;

    //ゲームの状態を保持する変数
    private int gameState;

    //プレイ時間
    private final static long TIME = 15;
    //ゲーム開始時間
    private long gameStarted;
    //残り時間
    private long remainedTime;

    private Bitmap[] player = new Bitmap[3]; //プレイヤー画像を格納
    private int playerX; //プレイヤーのX座標
    private int playerY; //プレイヤーのY座標

    private int playerVY; //プレイヤーの上昇量

    private int canvasCX; //画面の中央のX座標
    private int canvasCY; //画面の中央のY座標

    private Bitmap bgImage; //背景画像
    private Bitmap startImage; //スタート画面の背景画像
   // private Bitmap startButton; //スタートボタン

    private int energyX; //アイテムのX座標
    private int energyY; //アイテムのY座標
    private int energyVX = -20; //アイテムの水平方向の移動量

    //アイテム用の描画オブジェクトを生成
    Paint energyPaint = new Paint();

    //スコアのラベルテキスト
    private final String scoreLabel = "SCORE:";
    //スコアを保持する変数
    private int score;

    //タイトル描画用のペイントオブジェクト
    Paint titlePaint = new Paint();

    //スタートテキスト描画用のオブジェクト
    Paint startPaint = new Paint();

    //スコア描画用のペイントオブジェクトを作成
    Paint scorePaint = new Paint();

    //ゲームオーバー描画用のペイントオブジェクト
    Paint endPaint = new Paint();

    //リトライ描画用のペイントオブジェクト
    Paint retryPaint = new Paint();

    //残り時間用のペイントオブジェクト
    Paint timePaint = new Paint();

    //コンストラクタ
    public GameView(Context context){
        super(context);

        //リソースオブジェクトを作成
        Resources res = this.getContext().getResources();

        //背景画像をビットマップに変換して変数「bgImage」に入れる
        bgImage = BitmapFactory.decodeResource(res, R.drawable.bg);

        //プレイヤー画像アマビエをビットマップに変換して変数「player」に入れる
        player[0] = BitmapFactory.decodeResource(res, R.drawable.amabie);
        player[1] = BitmapFactory.decodeResource(res, R.drawable.amabie2);
        player[2] = BitmapFactory.decodeResource(res, R.drawable.amabie3);

        //アイテムの描画色を設定
        energyPaint.setColor(Color.RED);
        //アンチエリアスを有効にする
        energyPaint.setAntiAlias(true);

        //スコアの描画色を設定
        scorePaint.setColor(Color.BLACK);
        //スコアのテキストサイズを設定
        scorePaint.setTextSize(64);
        //アンチエリアスを有効
        scorePaint.setAntiAlias(true);

        //タイムの描画色を設定
        timePaint.setColor(Color.BLACK);
        //タイムのテキストサイズを設定
        timePaint.setTextSize(64);
        //アンチエリアスを有効
        timePaint.setAntiAlias(true);

        //ゲームの状態をスタートにする
        gameState = GAME_START;
    }

    //スパークラス（継承元）の「onDraw」メソッドをオーバーライドする
    @Override
    public void onDraw(Canvas canvas){
        //画面（Canvas）中央のX座標を取得
        canvasCX = canvas.getWidth()/2;

        //画面（Canvas）中央のY座標を取得
        canvasCY = canvas.getHeight()/2;

        //画面（Canvas）に応じて背景画像を拡大する
        //bgImage = Bitmap.createScaledBitmap(bgImage, canvas.getWidth()*2, canvas.getHeight(), true);

        //「startScene」を実行
        //startScene(canvas);

        //「playScene」メソッドを実行
        //playScene(canvas);

        //「endScene」メソッドを実行
        //endScene(canvas);

        switch(gameState){
            case GAME_START:
                //画面（Canvas）に応じて背景画像を拡大する
                bgImage = Bitmap.createScaledBitmap(bgImage, canvas.getWidth()*2, canvas.getHeight(), true);

                startScene(canvas);
                break;
            case GAME_PLAY:
                playScene(canvas);
                break;
            case GAME_OVER:
                endScene(canvas);
                break;
        }
    }

    public void startScene(Canvas canvas){
        //スコア初期化
        score = 0;

        //背景画像を描画
        canvas.drawBitmap(bgImage, 0, 0, null);

        //アンチエリアスを有効
        titlePaint.setAntiAlias(true);
        //タイトルの描画色
        titlePaint.setColor(Color.BLACK);
        //タイトルのテキストサイズ
        titlePaint.setTextSize(130);
        //タイトルのテキスト配置
        titlePaint.setTextAlign((Align.CENTER));
        //タイトルテキストを描画
        canvas.drawText("負けるな", canvasCX, canvasCY - 400, titlePaint);
        canvas.drawText("\n\nアマビエちゃん！", canvasCX, canvasCY - 228, titlePaint);

        //アンチエリアスを有効
        startPaint.setAntiAlias(true);
        //スタートボタンの描画色
        startPaint.setColor(Color.WHITE);
        //スタートのテキストサイズ
        startPaint.setTextSize(86);
        //スタートのテキスト配置
        startPaint.setTextAlign(Align.CENTER);
        //スタートテキストを描画
        canvas.drawText("Touch to Start", canvasCX, canvasCY - 10, startPaint);

    }

        public void playScene(Canvas canvas){
        //残り時間を取得
        remainedTime =
                TIME - (System.currentTimeMillis() - gameStarted) / 1000;
        //残り時間が0より少なくなったら
        if(remainedTime < 0){
            //ゲームオーバー
            gameState = GAME_OVER;
            return;
        }

        //画面（Canvas）に背景画像を描写
        canvas.drawBitmap(bgImage, 0, 0, null);

        //アイテムをスクロール表示するための処理
        //アイテムをenergyVXずつ。右から左へ移動する
        energyX += energyVX;
        //画面から消えたら、またはプレイヤーに当たっていたら
        if(energyX < 0 || hitCheck()){
            //また右（x座標がCanvas幅+20の位置)から、
            energyX = canvas.getWidth() + 20;
            //高さ（y座標）は画面上半分のランダム位置から表示されるようにする
            energyY = (int)Math.floor(Math.random() * canvasCY);// + canvasCY;
        }

        //画面（Canvas）にアイテムを描写
        canvas.drawCircle(energyX, energyY, 15, energyPaint);

        //スコアを描画
        canvas.drawText(scoreLabel + score, 10, 100, scorePaint);

        //「player」の初期表示X座標を設定
        playerX = canvasCX - player[0].getWidth()/2;

        //「player」の初期表示Y座標を設定
        //playerY = canvasCY - player.getHeight()/2;

        //プレイヤーをplayerVYずつ上昇する
        playerY += playerVY;
        //画面上端よりはみ出さないようにする
        if(playerY < 0) playerY = 0;

        //プレイヤーを下降する
        playerVY += 4;
        //元の位置より下に行かないようにする
        if(playerY > canvasCY) playerY = canvasCY;

        //画面（Canvas）にプレイヤーを表示
        if(hitCheck()) {
            canvas.drawBitmap(player[1], playerX, playerY, null);
        } else {
            canvas.drawBitmap(player[0], playerX, playerY, null);
        }

        //残り時間を描画
        canvas.drawText(String.valueOf(remainedTime), 10, 200, timePaint);
    }

    //ゲームオーバー画面
    public void endScene(Canvas canvas){
        //背景画像を描画
        canvas.drawBitmap(bgImage, 0, 0, null);

        //アンチエリアスを有効
        endPaint.setAntiAlias(true);
        //テキストカラー
        endPaint.setColor(Color.BLACK);
        //テキストサイズ
        endPaint.setTextSize(60);
        //テキスト配置
        endPaint.setTextAlign(Align.CENTER);
        //テキスト描画
        canvas.drawText("アマビエさんは", canvasCX, canvasCY-200, endPaint);
        canvas.drawText(score + "人守りました", canvasCX, canvasCY-130, endPaint);

        //アンチエリアスを有効
        retryPaint.setAntiAlias(true);
        //テキストカラー
        retryPaint.setColor(Color.WHITE);
        //テキストサイズ
        retryPaint.setTextSize(86);
        //テキスト配置
        retryPaint.setTextAlign(Align.CENTER);
        //テキスト描画
        canvas.drawText("Touch to Retry", canvasCX, canvasCY+50, retryPaint);

    }

    //タッチイベント時に実行されるメソッド
    public boolean onTouchEvent(MotionEvent me) {
        //タッチされたら
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            switch(gameState){
                case GAME_START:
                    gameState = GAME_PLAY;
                    //ゲームの開始時間を取得
                    gameStarted = System.currentTimeMillis();
                    break;
                case GAME_PLAY:
                    //プレイヤーの上昇値を設定
                    playerVY = -20;
                    break;
                case GAME_OVER:
                    gameState = GAME_START;
                    break;
            }

        }
        //呼び出し元に戻る
        return true;
    }

        //当たり判定
    public boolean hitCheck() {
        if (playerX < energyX &&
                (playerX + player[0].getWidth() - 30) > energyX &&
                playerY < energyY - 15 &&
                (playerY + player[0].getHeight()) > energyY + 15) {
            //スコアを加算
            score += 10;
            //アイテムの中心座標が、プレイヤーの矩形領域の中ならtrue
            return true;
        } else {
            //そうでなければfalse
            return false;
        }
    }

}
