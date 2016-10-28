package com.example.administrator.daihuobangv10;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONObject;

import Dao.Host;
import Dao.User;
import util.HttpConnect;

/**
 * Created by wsj on 16/6/29.
 */
public class PersonalInfo_Activity extends AppCompatActivity {

    private Toolbar tb;
    private Button btn_modifyinfo;
    private TextView tv1,tv2,tv3,tv4;

    /**
     * 利用handler机制在主线程接收子线程发送的信息并处理相应事件
     */
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String s = msg.obj.toString();//获取发送来的消息内容
                    Log.i("tag",s);

                    if (s.equals("-1")){
//                        setDialog("用户ID不匹配");
                        Toast.makeText(getApplicationContext(),"用户ID不存在",Toast.LENGTH_SHORT).show();
                    }else {
                        try{
                            JSONArray ja = new JSONArray(s);
                            JSONObject json = ja.getJSONObject(0);//将子线程发送的string转化为JSON
                            //把JSON中内容提出存入user类中
                            User.id = json.getString("id");
                            User.username = json.getString("name");
                            User.password = json.getString("password");
                            User.phoneNum = json.getString("phonenumber");
                            User.idcardNum = json.getString("idnumber");

                            //写入textview文本
                            tv1.setText("用户ID:"+User.id);
                            tv2.setText("用户名:"+User.username);
                            tv3.setText("手机号码:"+User.phoneNum);
                            tv4.setText("身份证号码:"+User.idcardNum);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        //获取页面相关控件
        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.layout);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        tb = (Toolbar) findViewById(R.id.toolbar);
        btn_modifyinfo = (Button) findViewById(R.id.btn_modifyinfo);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);

        //设置页面布局内容view
        getview();

        collapsingToolbarLayout.setTitle("个人信息");
        setSupportActionBar(tb);

        //设置标题栏内容：title，返回按钮，右上菜单
        tb.setTitle("个人信息");
        tb.setTitleTextColor(Color.parseColor("#ffffff"));
        tb.setNavigationIcon(R.drawable.ic_return);
        tb.inflateMenu(R.menu.personal_info);

        //设置标题栏左边返回按钮的响应事件
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(layout,"return", Snackbar.LENGTH_LONG).show();
                finish();
            }
        });

        //设置标题栏上菜单的监听响应——跳转到下一页面对个人信息进行修改
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent();
                intent.setClass(PersonalInfo_Activity.this,EditInfo_Activity.class);
                startActivityForResult(intent,1);
                return true;
            }
        });

        //设置页面内信息修改按钮的监听响应——跳转到对个人信息的修改
        btn_modifyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PersonalInfo_Activity.this,EditInfo_Activity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    /**
     * 通过网络获取到用户信息，写入user类中，然后set到textview中显示在用户界面可见
     */
    private void getview() {

//        测试，写入一些用户信息
//        User.id = "1";
//        User.username = "diro";
//        User.phoneNum = "123456";
//        User.idcardNum = "12345";

//        拼接url
        final String url = "http://"+ Host.host+":3000/user/getUserInfo?"+"userId="+User.id;
        Log.i("tag",url);

//        新建线程访问网络，并获取到吼他返回的信息
        new Thread(){
            @Override
            public void run() {
                try {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /**
     * 创建菜单栏
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_info,menu);
        return true;
    }

    /**
     * 弹出对话框提示
     * @param msg
     */
    public void setDialog(String msg) {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("OK",null)
                .show();
    }
}
