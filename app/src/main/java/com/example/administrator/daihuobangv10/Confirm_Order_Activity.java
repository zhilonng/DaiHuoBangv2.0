package com.example.administrator.daihuobangv10;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.amap.api.services.core.LatLonPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Dao.Host;
import Dao.Order;
import Dao.User;
import util.HttpConnect;

/**
 * Created by wsj on 16/7/6.
 */
public class Confirm_Order_Activity extends Activity implements View.OnClickListener{

    private ListView lv;
    private Button btn;
    private Toolbar tb;
    public final ArrayList<HashMap<String,String>> listitem = new ArrayList<>();
    public final ArrayList<String> rec = new ArrayList<>();
    public final ArrayList<String> acc = new ArrayList<>();

    private LatLonPoint mStartPoint = new LatLonPoint(23.049677, 113.400074);//起点，
    private LatLonPoint mEndPoint = new LatLonPoint(23.171, 113.3436);//终点，
    SimpleAdapter adapter;

    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    //接收传来的信息
                    String str = msg.obj.toString();
                    Log.i("tag",str);

                    if (str.equals("1")){
                        Intent intent = new Intent(Confirm_Order_Activity.this,BaseNavi_Activity.class);
                        intent.putExtra("startPosLat",Double.valueOf(mStartPoint.getLatitude()));
                        intent.putExtra("startPostLng",Double.valueOf(mStartPoint.getLongitude()));
                        intent.putExtra("endPostLat",Double.valueOf(mEndPoint.getLatitude()));
                        intent.putExtra("endPosLng",Double.valueOf(mEndPoint.getLongitude()));
                        intent.putExtra("startPosName","华南理工大学大学城小区");
                        intent.putExtra("endPosName","天河客运站");
                        startActivity(intent);
                        overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
                        Toast.makeText(getApplicationContext(),"successfully!!!\nYou can click return button to return.",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_order);

        //获取页面上的控件
        tb = (Toolbar) findViewById(R.id.toolbar_confirm_order);
        lv = (ListView) findViewById(R.id.lv_confirm_order);
        btn = (Button)findViewById(R.id.btn_confirm_order);

        // 为标题栏设置添加title及左上的返回按钮
        tb.setTitle("带货帮");
        tb.setTitleTextColor(Color.parseColor("#ffffff"));
        tb.setNavigationIcon(R.drawable.ic_return);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   //结束该activity，返回到上个页面
            }
        });

/**
 * handler机制处理子线程发送来的message，将服务器返回的数据写入到adapter所需的listitem中，同时存到本地locallist
 */
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String s = msg.obj.toString();
                        Log.i("MessageReturn",s);

                        if (s.isEmpty()){
                            Toast.makeText(getApplicationContext(),"unsucessfully!QAQ",Toast.LENGTH_LONG).show();
                        }else {
                            try{
                                //transfer the return string to jsonarray
                                JSONArray ja = new JSONArray(s);
                                //write to adapter list
                                for (int i=0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("tv_start","起点："+jo.getString("startPosName"));
                                    map.put("tv_end","终点："+jo.getString("endPosName"));
                                    map.put("id",jo.getString("id"));
                                    listitem.add(map);

                                    Log.i("!!!!!!",map.toString());
                                }
                                //write to local store
                                for (int i=0;i<ja.length();i++) {

                                    JSONObject jo = ja.getJSONObject(i);
                                    Order.locallist.add(jo);

                                    Log.i("test", Order.locallist.toString());
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            //设置listview的adapter，并与之绑定
                            adapter = new SimpleAdapter(getApplicationContext(),listitem,R.layout.item_confirm_order,
                                    new String[]{"tv_start","tv_end"},
                                    new int[]{R.id.tv_start,R.id.tv_end});
                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        final String url = "http://"+ Host.host+":3000/driver/getOrder?userId="+ User.id;
        Log.i("tag",url);
//连接网络，获取order数据
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

                    Log.i("tag","send");

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //接单按钮点击响应事件
        btn.setOnClickListener(this);

        //设置list view的item项的点击监听事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int p = position;

                //根据点击项的position，从order存储中找到对应项的object内容
                JSONObject json = Order.locallist.get(p);

                //将信息详情取出存到intent中，发送给跳转的下一页面
                try{
                    String[] type = getResources().getStringArray(R.array.kinds);
                    String reciver = json.getString("reciver");
                    String reciverphonenumber = json.getString("reciverphonenumber");
                    String goodstype = type[Integer.parseInt(json.getString("goodstype"))];
                    String goodsamount = json.getString("goodsamount");
                    String startPosName = json.getString("startPosName");
                    String endPosName = json.getString("endPosName");
                    String username = json.getString("name");
                    String phonenumber = json.getString("phonenumber");
                    String goodsValue = json.getString("goodsvalue");

                    Intent intent = new Intent(Confirm_Order_Activity.this,ViewOrder_Activity.class);
                    intent.putExtra("reciver",reciver);
                    intent.putExtra("reciverphonenumber",reciverphonenumber);
                    intent.putExtra("goodstype",goodstype);
                    intent.putExtra("goodsamount",goodsamount);
                    intent.putExtra("startPosName",startPosName);
                    intent.putExtra("endPosName",endPosName);
                    intent.putExtra("username",username);
                    intent.putExtra("phonenumber",phonenumber);
                    intent.putExtra("goodsValue",goodsValue);

                    startActivityForResult(intent,1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //设置list view的item项的长按delete事件
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //从adapter的匹配项中删除list项，同时通知listview更新
                final int p = position;
                listitem.remove(p);
                adapter.notifyDataSetChanged();

                //从存储的order的list中删除选中项
                try{

                    rec.add(Order.locallist.get(p).getString("id"));
                    Order.locallist.remove(p);
                    Log.i("@@@@@@@@",rec.toString());


                    //界面提示用户已经删除某一项
                    Toast.makeText(getApplicationContext(),"删除第"+(p+1)+"项货物",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }

                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(Confirm_Order_Activity.this,ViewOrder_Activity.class);
//        startActivityForResult(intent,1);
        try {
            for (int i = 0; i < Order.locallist.size(); i++) {
                acc.add(Order.locallist.get(i).getString("id"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        new Thread(){
            @Override
            public void run() {
                try{
                    String url = "http://"+Host.host+":3000/driver/confirmOrder?";
                    if (acc.size()==0)
                        url += "idAcc=0&";
                    else {
                        for (int i=0;i<acc.size();i++)
                            url=url+"idAcc="+acc.get(i)+"&";
                    }
                    if (rec.size()==0)
                        url += "idRec=0&";
                    else {
                        for (int i=0;i<rec.size();i++)
                            url=url+"idRec="+rec.get(i)+"&";
                    }
                    url = url+"driverId="+User.id;

                    Log.i("url",url);
                    String response = HttpConnect.doget(url);   //网络访问


                    //将后台返回的数据利用message发送给主线程的handler处理
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.obj = response;
                    handler2.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}