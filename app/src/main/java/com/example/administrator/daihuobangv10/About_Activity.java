package com.example.administrator.daihuobangv10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

/**
 * 创建时间：2/7/16
 * 项目名称：daihuobang
 *
 * @author zhilong Huang
 * @email 765608464@qq.com
 * 类说明：
 */
public class About_Activity extends AppCompatActivity {

    ImageView WhoAmI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_);

        WhoAmI = (ImageView)findViewById(R.id.logo);

    }
}
