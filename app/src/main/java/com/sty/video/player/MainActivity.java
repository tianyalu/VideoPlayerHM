package com.sty.video.player;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;


public class MainActivity extends AppCompatActivity {
    //private String videoDataPath = "http://192.168.1.8/newsServiceHM/media/sandymandy.mp4";
    private String videoDataPath = Environment.getExternalStorageDirectory().getPath() + "/sty/ad.mp4";
    private SurfaceView sfv;
    private VideoView vv;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;

    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);

        initViews();

        setListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void setFullScreen(){
        getSupportActionBar().hide();
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
    }

    private void initViews(){
        sfv = findViewById(R.id.sfv);
        vv = findViewById(R.id.vv);
        surfaceHolder = sfv.getHolder();
    }

    private void setListeners(){
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            //当surfaceView初始化完成的时候执行
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //playVideo();
                playLocalVideoRequestPermission();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            //当surfaceView销毁的时候执行
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(player != null && player.isPlaying()){
                    //获取视频当前播放的位置
                    currentPosition = player.getCurrentPosition();
                    player.stop();
                    //player.release();
                }
            }
        });
    }

    private void playLocalVideoRequestPermission(){
        vv.setVisibility(View.GONE);
        sfv.setVisibility(View.VISIBLE);
        if(PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //playVideo(); //调用播放视频的方法
            playVideoWithVideoView();
        }else{
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    playVideo();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    Toast.makeText(MainActivity.this, "您拒绝了外置存储的访问权限", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }
    }

    private void playVideo(){
        //1.初始化mediaPlayer
        player = new MediaPlayer(); //MediaPlayer只支持MP4/3GP格式，其它格式不支持
        try{
            //2.设置要播放的资源路径，可以是本地路径也可以是网络路径
            player.setDataSource(videoDataPath);
            //2.1 设置播放视频内容的surfaceHolder(用来维护视频播放的内容)
            player.setDisplay(surfaceHolder);
            //3.准备播放
            player.prepareAsync();
            //设置一个准备完成的监听
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //4.准备播放
                    player.start();
                    //5.继续上次的进度继续播放
                    player.seekTo(currentPosition);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playVideoWithVideoView(){ //VideoView是对SurfaceView的继承与封装
        sfv.setVisibility(View.GONE);
        vv.setVisibility(View.VISIBLE);

        vv.setVideoPath(videoDataPath);
        vv.start();
    }
}
