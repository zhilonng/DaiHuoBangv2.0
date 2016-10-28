package com.example.administrator.daihuobangv10;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
/**
 * 创建时间：18/6/16
 * 项目名称：newNaviDemo
 *
 * @author zhilong Huang
 * @email 765608464@qq.com
 * 类说明：
 */
public class SideSlip_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private GoodOwner_Fragment goodowner;
    private CarOwner_Fragment carowner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_slip_);
        //定义控件
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("带货帮");
        setSupportActionBar(toolbar);//标题栏
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItem = item.getItemId();
                switch (menuItem) {
                    case R.id.action_search:

//                        Intent intent=new Intent(SideSlip_Activity.this,Search_Activity.class);
//                        startActivity(intent);
                            Intent intent = new Intent();
                            intent.putExtra("msg",1);
                            intent.setAction("com.ljq.activity.CountService");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        carowner = new CarOwner_Fragment();
        transaction.replace(R.id.id_fragment_content, carowner);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_slip_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();

        if (id == R.id.nav_goodowner) {

            if(goodowner == null){
//                carowner.onDestroy();
                goodowner = new GoodOwner_Fragment();
            }
            transaction.replace(R.id.id_fragment_content, goodowner);
        } else if (id == R.id.nav_carowner) {
            if(carowner == null){
//                goodowner.onDestroy();
                carowner = new CarOwner_Fragment();
            }
            transaction.replace(R.id.id_fragment_content, carowner);

        } else if (id == R.id.nav_info) {
            Intent intent = new Intent(SideSlip_Activity.this,Confirm_Order_Activity.class); //从主ui跳转到车主订单ui
            startActivity(intent);
            overridePendingTransition(R.animator.fade_in,R.animator.fade_out);

        } else if (id == R.id.nav_order) {
            Intent intent = new Intent(SideSlip_Activity.this,OrderNearby_Activity.class); //从主ui跳转到货主订单ui
            startActivity(intent);
            overridePendingTransition(R.animator.fade_in,R.animator.fade_out);

        } else if (id == R.id.nav_personal) {
            Intent intent = new Intent(SideSlip_Activity.this,PersonalInfo_Activity.class); //从主ui跳转到个人中心ui
            startActivity(intent);
            overridePendingTransition(R.animator.fade_in,R.animator.fade_out);

        } else if (id == R.id.nav_signout) {
            Dialog dialog=new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.btn_dialog).setTitle("退出提醒").setMessage(
                    "请选择您的退出方式：").setPositiveButton("注销", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(SideSlip_Activity.this,Login_Activity.class); //从主ui跳转到登陆ui
                    intent.putExtra("msg",true);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"您已退出当前账号！",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).setNegativeButton("退出程序", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.show();

        }else if(id == R.id.nav_about){
            Intent intent = new Intent(SideSlip_Activity.this,About_Activity.class); //从启动动画ui跳转到主ui
            startActivity(intent);
            overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
