package com.marktony.zhuanlan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.marktony.zhuanlan.R;

/**
 * Created by anzhuo on 2016/11/18.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler x=new Handler();
        x.postDelayed(new Splashhandler(),3500);

    }
    class Splashhandler implements Runnable{

        @Override
        public void run() {
            Intent intent=new Intent(SplashActivity.this,TitleActivity.class);
            startActivity(intent);
            finish();

        }
    }
}