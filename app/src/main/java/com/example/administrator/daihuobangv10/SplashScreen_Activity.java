package com.example.administrator.daihuobangv10;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_);

        // 闪屏的核心代码
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen_Activity.this,Login_Activity.class); //从启动动画ui跳转到主ui
                intent.putExtra("msg",false);
                startActivity(intent);
                SplashScreen_Activity.this.finish(); // 结束启动动画界面

                overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
            }
        }, 2000); //启动动画持续3秒钟
    }
}
