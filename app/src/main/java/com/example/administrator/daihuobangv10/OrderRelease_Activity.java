package com.example.administrator.daihuobangv10;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.URLEncoder;

import Dao.Host;
import Dao.Order;
import Dao.User;
import util.HttpConnect;

/**
 * Created by wsj on 16/7/1.
 */
public class OrderRelease_Activity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar tb;
    private Spinner sp1,sp2,sp3;
    private Button btn,btn2;
    private EditText et_start,et_end,et_starttime;
    public static String start,end,starttime,startPosLat,startPosLng,endPostLat,endPosLng,
            vol,tolerateTime,tolerateRoute;
    private ProgressDialog progDialog = null;// 进度条
    private static String url;

    /**
     * hangler机制接收子线程发送来的message，并进行处理
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String s = msg.obj.toString();
                    Log.i("tag",s);

                    if (s.equals("-1")){    //返回错误结果码！
                        dissmissProgressDialog();
                        Toast.makeText(getApplicationContext(),"unsuccessfully~QAQ",Toast.LENGTH_SHORT).show();
                    }else {
                        //返回正确结果的处理！

                        User.setmStartPoint(Double.valueOf(startPosLat),Double.valueOf(startPosLng));
                        User.setmEndPoing(Double.valueOf(endPostLat),Double.valueOf(endPosLng));
                        Toast.makeText(getApplication(),"successfully!The order id is"+s,Toast.LENGTH_SHORT).show();

                        //完成发布运力流程，跳转到主界面
                       finish();
                        dissmissProgressDialog();

                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_release);


//        获取页面里各个控件
        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.layout_order_release);
        tb = (Toolbar) findViewById(R.id.toolbar_order_release);
        sp1 = (Spinner) findViewById(R.id.spinner1);
        sp2 = (Spinner) findViewById(R.id.spinner2);
        sp3 = (Spinner) findViewById(R.id.spinner3);
        btn = (Button) findViewById(R.id.btn_order_release);
        btn2 = (Button) findViewById(R.id.btn_choosetime);
        et_starttime = (EditText) findViewById(R.id.et_starttime);
        et_start = (EditText) findViewById(R.id.et_start_OrderRelease);
        et_end = (EditText) findViewById(R.id.et_end_OrderRelease);

//        获取上一页面传递的信息
        Bundle bundle = getIntent().getExtras();
        startPosLat = bundle.getString("startPosLat");
        startPosLng = bundle.getString("startPostLng");
        endPostLat = bundle.getString("endPostLat");
        endPosLng = bundle.getString("endPosLng");
        start = bundle.getString("startPosName");
        end = bundle.getString("endPosName");
    //    Toast.makeText(getApplicationContext(),startPosLat+startPosLng,Toast.LENGTH_SHORT).show();

//        将上一页面的起点终点写入
        et_start.setText(start);
        et_end.setText(end);


//        为标题栏设置添加title及左上的返回按钮
        setSupportActionBar(tb);
        tb.setTitle("带货帮");
        tb.setNavigationIcon(R.drawable.ic_return);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(layout,"return",Snackbar.LENGTH_LONG).show();
                finish();
            }
        });


//        从array中获取下拉列表内容
        String[] volume = getResources().getStringArray(R.array.volume);
        String[] distance = getResources().getStringArray(R.array.distance);
        String[] time_extra = getResources().getStringArray(R.array.time_extra);


//        给每个下拉列表设置adapter以及列表项的选择进行监听且响应
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,volume);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] volume = getResources().getStringArray(R.array.volume);
                Snackbar.make(layout,volume[position
                        ],Snackbar.LENGTH_SHORT).setAction("right",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                vol = ""+position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,distance);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapter2);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] distance = getResources().getStringArray(R.array.distance);
                Snackbar.make(layout,distance[position
                        ],Snackbar.LENGTH_SHORT).setAction("right",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                tolerateRoute = ""+position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,time_extra);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp3.setAdapter(adapter3);
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] time_extra = getResources().getStringArray(R.array.time_extra);
                Snackbar.make(layout,time_extra[position
                        ],Snackbar.LENGTH_SHORT).setAction("right",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                tolerateTime = ""+position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        发布运力按钮的点击监听响应事件
        btn.setOnClickListener(this);

//        选择日期按钮的监听响应
        btn2.setOnClickListener(this);
    }


    //onclick接口，对页面中的按钮点击进行监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choosetime:   //点击选择日期按钮
                //弹出选择日期
                DatePickerDialog date = new DatePickerDialog(OrderRelease_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        starttime = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        et_starttime.setText(starttime);
                    }
                },2016,7,6);
                date.show();
                break;
            case R.id.btn_order_release:    //点击发布按钮
                //URL

                if (et_starttime.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"请输入完整信息= =",Toast.LENGTH_SHORT).show();
                }else {

                    showProgressDialog();
                    try {


                    url = "http://" + Host.host + ":3000/order/addRoute?" + "driverId=" + User.id
                            + "&startPosLat=" + startPosLat + "&startPosLng=" + startPosLng + "&endPosLat=" + endPostLat
                            + "&endPosLng=" + endPosLng + "&startPosName=" + URLEncoder.encode(start,"UTF-8") + "&endPosName=" + URLEncoder.encode(end ,"UTF-8")+ "&startTime=" +
                            starttime + "&volume=" + vol + "&tolerateTime=" + tolerateTime + "&tolerateRoute=" + tolerateRoute;
                    Log.i("tag", url);
                    }catch (Exception e){

                    }
//                新建线程进行网络访问，并接收到后台返回的数据发送到主线程
                    new Thread() {
                        @Override
                        public void run() {
                            try {

                                String response = HttpConnect.doget(url);   //网络访问

                                //将后台返回的数据利用message发送给主线程的handler处理
                                Message msg = Message.obtain();
                                msg.what = 1;
                                msg.obj = response;
                                handler.sendMessage(msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
        }
    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(OrderRelease_Activity.this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("请稍等");
        progDialog.show();
    }
    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}