package com.bytedance.videoplayer;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PhotosActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private Handler mHandler;
    private static final String url = "https://uploadbeta.com/api/pictures/random/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImage();
    }

    private void loadImage(){
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ani_loading)
                .fallback(R.drawable.ani_loading)
                .error(R.drawable.icon_failure)
                .transform(new CircleCrop()) // 圆形图片
                .transform(new RoundedCorners(100))
                .apply(new RequestOptions().centerCrop())
                .transition(withCrossFade())
                .skipMemoryCache(true)//跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不在disk硬盘缓存
                .into(imageView);
    }

}