package com.example.administrator.daihuobangv10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Dao.Host;
import Dao.Order;
import Dao.User;
import util.HttpConnect;

/**
 * Created by wsj on 16/6/28.
 */
public class OrderNearby_Activity extends Activity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar tb;

    private LayoutInflater inflater;
    private List<String> titlelist = new ArrayList<>();
    private View view1,view2;
    private List<View> viewList = new ArrayList<>();

    public final List<Map<String,Object>> listitem = new ArrayList<>();
    public final List<Map<String,Object>> listitem1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_nearby);


//      重写viewPager的适配器
        class MyPageAdapter extends PagerAdapter {
            private List<View> viewList;

            //构造方法，参数是页卡的list
            public MyPageAdapter(List<View> viewList) {
                this.viewList = viewList;
            }

            //返回页卡的数量
            @Override
            public int getCount() {
                return viewList.size();
            }

            //官方提示这样写，为什么呢？
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            //实例化页卡
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));  //添加
                return viewList.get(position);
            }

            //删除页卡
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }

            //取得页卡的标题
            @Override
            public CharSequence getPageTitle(int position) {
                return titlelist.get(position);
            }
        }


        //获取页面的布局layout
        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.layout_coor);
        inflater = getLayoutInflater();
        //通过ID获得页面控件
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tb = (Toolbar) findViewById(R.id.toolbar);

        //inflater找到页卡的页面view
        view1 = inflater.inflate(R.layout.content_order_nearby,null);
        view2 = inflater.inflate(R.layout.content_gorder_unreceive,null);


        //标题栏返回按钮的点击监听响应
        tb.setNavigationIcon(R.drawable.ic_return);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(layout,"return", Snackbar.LENGTH_LONG).setAction("ok", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                }).show();
                finish();
            }
        });


        //获取view中的list view
        final ListView lv1 = (ListView) view1.findViewById(R.id.lv);
        final ListView lv2 = (ListView) view2.findViewById(R.id.lv);

/**
 * by zhilong 7.12
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

                                for (int i=0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);

                                    if (Integer.valueOf(jo.getString("status")) == 0 || Integer.valueOf(jo.getString("status")) == 1) {
                                        //write to adapter list1
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("img", R.drawable.ic_menu_camera);
                                        map.put("tv1", "类型：" + jo.getString("goodstype"));
                                        map.put("tv2", "起点：" + jo.getString("startposname"));
                                        map.put("tv3", "目的地：" + jo.getString("endposname"));
                                        map.put("id", jo.getString("id"));
                                        listitem.add(map);
                                        Log.i("!!!!!!",map.toString());
                                    }else {
                                        //write to adapter list2
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("img", R.drawable.ic_menu_camera);
                                        map.put("tv1", "类型：" + jo.getString("goodstype"));
                                        map.put("tv2", "起点：" + jo.getString("startposname"));
                                        map.put("tv3", "目的地：" + jo  .getString("endposname"));
                                        map.put("id", jo.getString("id"));
                                        listitem1.add(map);
                                        Log.i("!!!!!!",map.toString());
                                    }
                                }
                                for (int i=0;i<ja.length();i++) {
                                    //write to local data
                                    JSONObject jo = ja.getJSONObject(i);
                                    Order.locallist.add(jo);

                                    Log.i("test", Order.locallist.toString());
                                }


                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            Log.i("list",listitem.toString());

                            //重写list view适配器simpleadapter
                     //       Toast.makeText(getApplicationContext(),listitem.toString(),Toast.LENGTH_SHORT).show();
                     //       Toast.makeText(getApplicationContext(),listitem1.toString(),Toast.LENGTH_SHORT).show();
                            SimpleAdapter simpleAdapter1 = new SimpleAdapter(getApplicationContext(),listitem, R.layout.item_nearby,
                                    new String[]{"img","tv1","tv2","tv3","tv4"},
                                    new int[]{R.id.img, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4});
                            //绑定list view的adapter
                            lv1.setAdapter(simpleAdapter1);

                            //重写list2 view适配器simpleadapter
                            SimpleAdapter simpleAdapter2 = new SimpleAdapter(getApplicationContext(),listitem1, R.layout.item_nearby,
                                    new String[]{"img","tv1","tv2","tv3","tv4"},
                                    new int[]{R.id.img, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4});
                            //绑定list view的adapter
                            lv2.setAdapter(simpleAdapter2);

                        }
                        break;
                    default:
                        break;
                }
            }
        };

        final String url = "http://"+ Host.host+":3000/client/getOrderInfo?userId="+ User.id;
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

        //给list view的item设置点击事件的监听
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClickItem();
            }
        });

        //页面列表添加页面view
        viewList.add(view1);
        viewList.add(view2);

        //添加页面的标题
        titlelist.add("正进行");
        titlelist.add("已完成");

        //设置TAB的模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //给tab添加tab栏
        tabLayout.addTab(tabLayout.newTab().setText(titlelist.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titlelist.get(1)));

        //新建viewpage的适配器adapter，传入参数是页面列表viewlist
        MyPageAdapter adapter = new MyPageAdapter(viewList);

        //将viewpage跟adapter绑定
        viewPager.setAdapter(adapter);
        //将tab跟viewpage连接
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);

    }


    //点击列表项响应事件
    private void ClickItem() {
        Intent intent = new Intent(this,ViewOrder_Activity.class);
        startActivityForResult(intent,1);
    }
}
