package com.example.administrator.daihuobangv10;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;

import Dao.Host;
import Dao.User;
import util.HttpConnect;


/**
 * Created by wsj on 16/6/23.
 */
public class Regist_Activity extends Activity implements View.OnClickListener{

    private EditText et_username;
    private EditText et_password;
    private EditText et_phone;
    private EditText et_idnum;
    private EditText et_repwd;
    private Button regist;

    private ProgressDialog progDialog = null;// 进度条

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);

//获取页面内的控件
        regist = (Button) findViewById(R.id.btn_regist);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_pwd);
        et_repwd = (EditText) findViewById(R.id.et_repwd);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_idnum = (EditText)findViewById(R.id.et_idcard);

//给注册按钮设置监听器
        regist.setOnClickListener(this);

//                TextInputLayout input = (TextInputLayout)findViewById(R.id.textinput);
//                if (repwd.equalsIgnoreCase(pwd)){
//                    input.setError("right!");
//                }
//                else
//                    input.setErrorEnabled(false);
//                    input.setError("not the same password!");

    }

    //    用handler接收子线程返回的信息并做处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String s = (String)msg.obj;
                    Log.i("handler",s);

                    if (s.equals("0")) {
                        dissmissProgressDialog();
//                        setDialog("该手机号码已经被使用，请使用其他号码注册"); //弹出对话框提示
                        Toast.makeText(getApplicationContext(),"手机号码已经被使用，请使用其他号码注册",Toast.LENGTH_LONG).show();
                    }
                    else {

                        User.id = s;    //将后台返回的用户ID存到user类中


                        //完成注册流程，跳转到主界面
                        Intent intent = new Intent();
                        intent.setClass(Regist_Activity.this,SideSlip_Activity.class);
                        Regist_Activity.this.startActivity(intent);
                        overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
                        dissmissProgressDialog();
                        finish();

                    }
                    break;
            }
        }
    };

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    /**
     * 弹出对话框提示信息
     * @param msg
     */
    private void setDialog(String msg) {
        final String str = msg;
        new Thread(){
            @Override
            public void run() {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("提示").setMessage(str)
                        .setPositiveButton("确定",null)
                        .show();
                Message msg = Message.obtain();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }.start();

    }

    /**
     * 实现onclick接口，处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        showProgressDialog();
//        获取到edittext中用户写入的信息
        final String username = et_username.getText().toString();
        final String pwd = et_password.getText().toString().trim();
        final String repwd = et_repwd.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();
        final String idnum = et_idnum.getText().toString().trim();



//        新建子线程进行网络访问
        new Thread() {
            @Override
            public void run() {
                try {
                    //        拼接URL
                    final String url = "http://"+ Host.host+":3000/user/addUser?"+"userName="+ URLEncoder.encode(username,"utf-8")+"&userPassword="+pwd+
                            "&phoneNumber="+phone+"&idNumber="+idnum;

                    Log.i("url",url);
                    String response = HttpConnect.doget(url);   //使用URLConnection的get连接访问网络

//                    利用handler发送message，返回信息给主线程
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

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(Regist_Activity.this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("请稍等");
        progDialog.show();
    }
}

