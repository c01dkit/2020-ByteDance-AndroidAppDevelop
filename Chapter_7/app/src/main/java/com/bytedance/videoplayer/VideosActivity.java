package com.bytedance.videoplayer;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideosActivity extends AppCompatActivity{
    private Button btnStart, btnPause, btnCommit;
    private SeekBar seekBar;
    private EditText editText;
    private TextView now, total;
    private VideoView videoView;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        btnStart = findViewById(R.id.btn_play);
        btnPause = findViewById(R.id.btn_pause);
        btnCommit = findViewById(R.id.btn_commit);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        now = findViewById(R.id.now_time);
        total = findViewById(R.id.total_time);
        videoView = findViewById(R.id.videoView);
        editText = findViewById(R.id.edit_url);
        load("");
        now.setText(R.string.videostart);
        total.setText(R.string.videostart);
        bind();
    }

    private void load(String path){
        if (null == path) return;
        if (path.isEmpty()){
            videoView.setVideoPath(getVideoPath(R.raw.tokyo_university));
        } else {
            videoView.setVideoURI(Uri.parse(path));
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //隐藏顶部自带的标题栏
            ActionBar mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.hide();
            }
            //隐藏手机顶部状态栏
            Window mWindow = getWindow();
            if (mWindow!=null) {
                mWindow.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActionBar mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.show();
            }
            Window mWindow = getWindow();
            if (mWindow!=null) {
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    private void relocate() {
        String timeConverted = getConvertedTime(videoView.getDuration());
        total.setText(timeConverted);
        if (videoView.isPlaying()) {
            seekBar.setProgress(videoView.getCurrentPosition());
        }
        timeConverted = getConvertedTime(seekBar.getProgress());
        now.setText(timeConverted);
    }

    private String getConvertedTime(int time) {
        int seconds = (time - time % 1000) / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    private void timerStart() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView.isPlaying()) {
                    seekBar.setMax(videoView.getDuration());
                    relocate();
                }
                timerStart();
            }
        },1000);

    }

    private void bind() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(seekBar.getMax());
            }
        });
        videoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                seekBar.setEnabled(true);
                if (videoView.isPlaying()) videoView.pause();
                else videoView.start();
                return false;
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
                seekBar.setEnabled(true);
                seekBar.setMax(videoView.getDuration());
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editText.getText().toString();
                load(path);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                relocate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoView.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
                videoView.start();
            }
        });


    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }
}