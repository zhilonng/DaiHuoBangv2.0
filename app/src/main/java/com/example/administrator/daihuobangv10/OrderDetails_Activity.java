package com.example.administrator.daihuobangv10;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import Dao.User;
import util.HttpConnect;

/**
 * Created by wsj on 16/6/27.
 *
 */
public class OrderDetails_Activity extends Activity implements View.OnClickListener{
    private Button btn,btn2;
    private Spinner sp1,sp2,sp3;
    private EditText et_start,et_end, et_number,et_volume,et_receiver,et_receiverphone,et_value;

    private static String startPosLat,startPostLng,endPostLat,endPosLng,
            arriveTime,reciver,reciverPhoneNum,startPosName,endPosName,goodsType,goodsAmount,goodsVolume,goodsValue;
    static String day,hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.layout_coor);

        //获取页面上的各种控件
        btn = (Button) findViewById(R.id.btn_order);     //确认订单按钮
        btn2 = (Button) findViewById(R.id.btn_chooseday);   //选择日期按钮
        sp1 = (Spinner) findViewById(R.id.spinner1);    //货物类型下拉列表
        sp2 = (Spinner) findViewById(R.id.spinner2);    //支付方式下拉列表
        sp3 = (Spinner) findViewById(R.id.spinner3);    //送达时间下拉列表
        et_start = (EditText) findViewById(R.id.et_start_OrderDetail);
        et_end = (EditText)findViewById(R.id.et_end_OrderDetail);
        et_number = (EditText) findViewById(R.id.et_number_OrderDetail);
        et_volume = (EditText) findViewById(R.id.et_volume_OrderDetail);
        et_receiver = (EditText) findViewById(R.id.et_receiver_OrderDetail);
        et_receiverphone = (EditText) findViewById(R.id.et_receiverphone_OrderDetail);
        et_value = (EditText) findViewById(R.id.et_value_OrderDetail);

//        获取上一页面intent中传来的数据,需要接入上一页面activity才能测试
        Bundle bundle = getIntent().getExtras();
        startPosLat = bundle.getString("startPosLat");
        startPostLng = bundle.getString("startPostLng");
        endPostLat = bundle.getString("endPostLat");
        endPosLng = bundle.getString("endPosLng");
        startPosName = bundle.getString("startPosName");
        endPosName = bundle.getString("endPosName");

        //将上一页面确定的起点终点写入
        et_start.setText(startPosName);
        et_end.setText(endPosName);

        //获取用户输入的信息


        //从array数组中读取到各个下拉列表项具体内容
        String[] kinds = getResources().getStringArray(R.array.kinds);
        String[] pays = getResources().getStringArray(R.array.pay);
        String[] time = getResources().getStringArray(R.array.time);
        android.support.v7.widget.Toolbar tb =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_order_details);


        //设置标题栏样式：title，返回按钮。并且设置返回按钮的监听及点击响应事件
        tb.setTitle("带货帮");
        tb.setTitleTextColor(Color.parseColor("#ffffff"));
        tb.setNavigationIcon(R.drawable.ic_return);//设置左上按钮
        //添加左上按钮的点击监听事件
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(layout,"return",Snackbar.LENGTH_LONG).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            }
        });



        //确认订单按钮的点击监听响应
        btn.setOnClickListener(this);

        //选择日期按钮的响应事件
        btn2.setOnClickListener(this);


        //为每个下拉列表spinner设置adapter，以及对列表项的选择进行监听且响应

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,kinds);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] kinds = getResources().getStringArray(R.array.kinds);
                goodsType = ""+position;
                Snackbar.make(layout,goodsType+kinds[position
                        ],Snackbar.LENGTH_SHORT).setAction("right",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                }).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,pays);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapter2);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] pays = getResources().getStringArray(R.array.pay);
                Snackbar.make(layout,pays[position
                        ],Snackbar.LENGTH_SHORT).setAction("right",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,time);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp3.setAdapter(adapter3);
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] time = getResources().getStringArray(R.array.time);
                hour = time[position];
                Snackbar.make(layout,hour,Snackbar.LENGTH_SHORT).setAction("right",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /**
     * onclick 接口的响应事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chooseday:    //选择日期按钮，弹出系统日历供用户选择日期，获取到日期信息后返回
                DatePickerDialog date = new DatePickerDialog(OrderDetails_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        day = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        Toast.makeText(getApplicationContext(),day,Toast.LENGTH_SHORT).show();
                    }
                },2016,6,8);
                date.show();
                break;
            case R.id.btn_order:    //确认订单按钮
                goodsAmount = et_number.getText().toString();
                goodsVolume = et_volume.getText().toString();
                reciver = et_receiver.getText().toString();
                reciverPhoneNum = et_receiverphone.getText().toString();
                goodsValue = et_value.getText().toString();
                if (day.isEmpty()){
                    Toast.makeText(getApplicationContext(),"choose day!",Toast.LENGTH_SHORT).show();
                }else if (hour.isEmpty()){
                    Toast.makeText(getApplicationContext(),"choose hour!",Toast.LENGTH_SHORT).show();
                }else {
                    arriveTime = day+","+hour;
                }




//                新建线程进行网络访问，并且将后台返回的数据利用message发送到主线程进行处理
                new Thread(){
                    @Override
                    public void run() {
                        try {
//                拼接URL
                            final String url = "http://"+ Host.host+":3000/order/addOrder?"+"userId="+ User.id+"&startPosLat="+startPosLat+
                                    "&startPosLng="+startPostLng+"&endPosLat="+endPostLat+"&endPosLng="+endPosLng+"&goodsType="+goodsType+
                                    "&goodsAmount="+goodsAmount+"&arriveTime="+URLEncoder.encode(arriveTime,"utf-8")+"&reciver="+URLEncoder.encode(reciver,"UTF-8")+"&reciverPhoneNum="+reciverPhoneNum+
                                    "&startPosName="+ URLEncoder.encode(startPosName,"utf-8")+"&endPosName="+URLEncoder.encode(endPosName,"utf-8")
                                    +"&goodsValue="+goodsValue;
//                Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
                            Log.i("tag",url);
                            String response = HttpConnect.doget(url);   //网络访问

                            //将后台返回的数据利用message发送给主线程的handler处理
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = response;
                            handler.sendMessage(msg);

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
        }
    }

    /**
     * 新建handler机制对子线程发送来的message接收并处理
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String s = msg.obj.toString();//获取发送来的消息内容
                    Log.i("tag",s);

                    if (s.equals("-1")){    //返回错误结果码！
                        Toast.makeText(getApplicationContext(),"unsuccessfully~QAQ",Toast.LENGTH_SHORT).show();
                    }else {
                        //返回正确结果的处理！
                        Toast.makeText(getApplication(),"successfully!The order id is"+s,Toast.LENGTH_SHORT).show();

                        //完成发布运力流程，跳转到主界面
                        finish();
                    }
                    break;
            }
        }
    };
}

