package com.marktony.zhuanlan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.marktony.zhuanlan.R;
import com.marktony.zhuanlan.view.CountDownView;

/**
 * Created by anzhuo on 2016/11/18.
 */

public class TitleActivity extends Activity {
    private CountDownView count_down_view;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_banner);
        initView();
        Glide.with(TitleActivity.this).load("http://img1.126.net/channel6/2016/025452/1.jpg").into(imageView);
        count_down_view.start();
        count_down_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TitleActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initView() {
        imageView= (ImageView) findViewById(R.id.iv_banner);
        count_down_view = (CountDownView) findViewById(R.id.countDownView);
        count_down_view.setCountDownTimerListener(new CountDownView.CountDownTimerListener() {
            @Override
            public void onStartCount() {

            }

            @Override
            public void onFinishCount() {
                Intent intent=new Intent(TitleActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
