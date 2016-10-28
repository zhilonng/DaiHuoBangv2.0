package com.example.administrator.daihuobangv10;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Dao.Host;
import Dao.User;
import util.HttpConnect;


/**
 * Created by wsj on 16/7/1.
 */

public class EditInfo_Activity extends AppCompatActivity implements View.OnClickListener{

    private Button btn;
    private Toolbar tb;
    private EditText et_username,et_pwd,et_phonenum,et_idcard;

    /**
     * 利用handler机制接收子线程发送的数据，并对之进行处理
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String s = msg.obj.toString();
                    Log.i("tag",s);

                    if (s.equals("1")){
                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"信息修改出错！",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);

        //获取页面内的控件
        final CoordinatorLayout layout = (CoordinatorLayout)findViewById(R.id.layout_coor);
        btn = (Button) findViewById(R.id.btn_edit_info);
        tb = (Toolbar) findViewById(R.id.toolbar_edit_info);
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_phonenum = (EditText)findViewById(R.id.et_phonenum);
        et_idcard = (EditText) findViewById(R.id.et_idcard);
        setSupportActionBar(tb);

        //设置标题栏的返回按钮图标
        tb.setNavigationIcon(R.drawable.ic_return);

        //设置标题栏按钮的点击响应事件——结束该页面并返回上一页面
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //为确认修改按钮设置监听响应
        btn.setOnClickListener(this);
    }

    /**
     * 实现onclick接口，响应按钮的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        final CoordinatorLayout layout = (CoordinatorLayout)findViewById(R.id.layout_coor);
        Snackbar.make(layout,"Edit Confirm!", Snackbar.LENGTH_LONG).show();

        String username = et_username.getText().toString();
        String pwd = et_pwd.getText().toString();
        String phonenum = et_phonenum.getText().toString();
        String idnum = et_idcard.getText().toString();

        final String url = "http://"+ Host.host+":3000/user/editInfo?"+"userId="+ User.id+"&newPassword="+pwd;

        //新建线程进行网络访问，并将后台返回的数据发送到主线程进行处理
        new Thread(){
            @Override
            public void run() {
                try{
                    String response = HttpConnect.doget(url);   //网络访问

                    //将后台返回的数据利用message发送给主线程的handler处理
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = response;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
