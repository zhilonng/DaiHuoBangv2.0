package com.example.administrator.daihuobangv10;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by wsj on 16/7/4.
 */
public class TestActivity extends Activity implements View.OnClickListener {
    public String num = "18814122736";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        Button btn1 = (Button) findViewById(R.id.btn_test1);
        Button btn2 = (Button) findViewById(R.id.btn_test2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test1:
                Toast.makeText(getApplicationContext(), "111", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_test2:
                Toast.makeText(getApplicationContext(),"222",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+num));
                startActivityForResult(intent1,1);
        }
    }
}
