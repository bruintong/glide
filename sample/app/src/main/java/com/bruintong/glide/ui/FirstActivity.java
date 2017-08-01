package com.bruintong.glide.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bruintong.glide.GlideApp;
import com.bruintong.glide.R;

/**
 * Created by tangweixiong on 2017/8/1.
 */

public class FirstActivity extends AppCompatActivity {

    private ImageView mMoviePic;
    private String MOVIE_PIC_URL = "https://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2485983612.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.first_movic_pic);
        mMoviePic = (ImageView) findViewById(R.id.movie_pic);
        GlideApp.with(this).load(MOVIE_PIC_URL).into(mMoviePic);
    }
}
