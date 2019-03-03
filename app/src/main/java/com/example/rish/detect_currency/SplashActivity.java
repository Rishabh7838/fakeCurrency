package com.example.rish.detect_currency;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {
    LinearLayout l1,l2;
    Animation uptodown,downtoup;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        l1 =  findViewById(R.id.l1);
        l2 =  findViewById(R.id.l2);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);





//                setAnimationListener(new Animation.AnimationListener(){
//            @Override
//            public void onAnimationStart(Animation arg0) {
//            }
//            @Override
//            public void onAnimationRepeat(Animation arg0) {
//            }
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                Intent i = new Intent(SplashActivity.this,MainActivity.class);
//                startActivity(i);
//            }
//        });
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(signInIntent);
                // buttons[inew][jnew].setBackgroundColor(Color.BLACK);
            }
        }, 2000);
    }

}
