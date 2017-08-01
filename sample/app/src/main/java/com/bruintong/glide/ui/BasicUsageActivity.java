package com.bruintong.glide.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bruintong.glide.GlideApp;
import com.bruintong.glide.R;
import com.bumptech.glide.Glide;

/**
 * Created by tangweixiong on 2017/8/1.
 */

public class BasicUsageActivity extends AppCompatActivity {

    private ImageView mMoviePic;
    private String MOVIE_PIC_URL = "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2485983612.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.basic_usage);
        mMoviePic = (ImageView) findViewById(R.id.movie_pic);
        GlideApp.with(this)
                .asGif_1()
                .load("")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .miniThumb(600)
                .into(mMoviePic);
//        或者使用标准写法
//        Glide.with(this).load(MOVIE_PIC_URL).into(mMoviePic);
    }
}
