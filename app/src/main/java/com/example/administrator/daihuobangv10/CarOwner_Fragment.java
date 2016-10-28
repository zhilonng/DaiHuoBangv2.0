package com.example.administrator.daihuobangv10;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import Dao.User;
import httpservice.Transfor_JSON;
import util.AMapUtil;
import util.ToastUtil;


/**
 * 创建时间：20/6/16
 * 项目名称：daihuobang
 *
 * @author zhilong Huang
 * @email 765608464@qq.com
 * 类说明：
 */
public class CarOwner_Fragment extends Fragment implements LocationSource, AMapLocationListener,AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, TextWatcher,
        PoiSearch.OnPoiSearchListener, View.OnClickListener, Inputtips.InputtipsListener,RouteSearch.OnRouteSearchListener,AMap.OnMarkerDragListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String string ="" ;

    private static String count = null;

    //    基础地图
    private AMap amap;
    private MapView mapview;

    //定义控件
    private Button btn_confirm;
    private FloatingActionButton fab;
    private FloatingActionButton fab_route;
    private Button btn;
    private CardView card_search;
    private ImageButton btn_back;
    private TextView edt_destination;
    private TextView edt_startpoint;
    private TextView tv_startpoint;
    private TextView tv_endpoint;
   //poikeyowrd搜索
    private ImageButton btn_startsearch;
    private AutoCompleteTextView searchText;// 输入搜索关键字
    private static String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    //路线规划
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint = new LatLonPoint(39.923271, 116.396034);//起点，
    private LatLonPoint mEndPoint = new LatLonPoint(39.984947, 116.494689);//终点，
    private boolean isChangePoint = false;
    private final int ROUTE_TYPE_DRIVE = 2;

    private LocationSource.OnLocationChangedListener mlistener;
    //声明AMapLocationClient对象
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Toolbar toolbar;

    //private static Thread thread_startpoint;
    private LatLng myDestination = null;
    private LatLng myLatlng = null;
    private static boolean checkpoint = false;//true为目的地，false为出发点
    private boolean acheck = true;
    private boolean checkmap=true;
   // private static boolean checkthread = true;
    public static double geoLat;
    public static double geoLng;
    private static String startway;
    private LatLng destination;
    private LatLng startPoint;
    private final static LatLng SYDNEY = new LatLng(-33.86759, 151.2088);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public CarOwner_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarOwner_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarOwner_Fragment newInstance(String param1, String param2) {
        CarOwner_Fragment fragment = new CarOwner_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_car_owner_, container,
                    false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        fab = (FloatingActionButton)view.findViewById(R.id.fab_map);
        btn = (Button)view.findViewById(R.id.btn_startAccept);
        final LinearLayout lyt = (LinearLayout)view.findViewById(R.id.lyt_search);
        card_search = (CardView)view.findViewById(R.id.card_search);
        btn_back = (ImageButton)view.findViewById(R.id.image_search_back);
        btn_startsearch = (ImageButton)view.findViewById(R.id.btn_startsearch);
        searchText = (AutoCompleteTextView) view.findViewById(R.id.edit_text_search);
        edt_destination = (TextView) view.findViewById(R.id.edt_destination);
        btn_confirm = (Button)view.findViewById(R.id.btn_startAccept);
        fab_route = (FloatingActionButton)view.findViewById(R.id.fab_route);
        edt_startpoint = (TextView) view.findViewById(R.id.edt_startpoint);
        tv_startpoint = (TextView)view.findViewById(R.id.tv_startpoint);
        tv_endpoint = (TextView)view.findViewById(R.id.tv_endpoint);
        btn_startsearch.setOnClickListener(this);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件

        //设置监听器
        btn_confirm.setOnClickListener(this);
        fab.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        fab_route.setOnClickListener(this);
        tv_startpoint.setOnClickListener(this);
        tv_endpoint.setOnClickListener(this);
        edt_startpoint.setOnClickListener(this);
        edt_destination.setOnClickListener(this);

        //注册广播接收器
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ljq.activity.CountService");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                card_search.setVisibility(View.VISIBLE);
                card_search.bringToFront();
//                Bundle bundle=intent.getExtras();
//                keyWord=bundle.getString("keyword");
//                if (keyWord != ""){
//                Toast.makeText(getActivity().getApplicationContext(),"搜索："+keyWord,Toast.LENGTH_SHORT).show();
//                searchButton1(keyWord);
//                    // }
//            }else {
//                    Toast.makeText(getActivity().getApplicationContext(),"唉，桑心",Toast.LENGTH_SHORT).show();
//                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver,intentFilter);

        mapview = (MapView)view.findViewById(R.id.map);
        mapview.onCreate(savedInstanceState);
        //初始化地图
        initMap();

        amap.setLocationSource(this);
        amap.getUiSettings().setMyLocationButtonEnabled(false);
        amap.setMyLocationEnabled(true);
        mRouteSearch = new RouteSearch(getActivity().getApplicationContext());
        mRouteSearch.setRouteSearchListener(this);
        return view;
    }

    private void initMap() {
        if (amap == null) {
            amap = mapview.getMap();
            setUpMap();
        }else{
            amap.setLocationSource(this);
            amap.setMyLocationEnabled(true);
            amap = mapview.getMap();
            setUpMap();
        }
    }


    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0,0,0,0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        amap.setMyLocationStyle(myLocationStyle);
        amap.setLocationSource(this);// 设置定位监听
        amap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        amap.getUiSettings().setScaleControlsEnabled(true);
        amap.getUiSettings().setZoomControlsEnabled(true);
        amap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

        amap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        amap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        amap.setOnMarkerClickListener(this);
        amap.setOnMarkerDragListener(this);
        amap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapview.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mlistener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();

        }

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mlistener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {

                if(acheck == true){
                    acheck = false;
                    edt_startpoint.setText("当前位置"+"("+amapLocation.getPoiName()+")");
                    edt_startpoint.setTextColor(Color.rgb(0,0,0));
                    geoLat = amapLocation.getLatitude();
                    geoLng = amapLocation.getLongitude();
                    mStartPoint.setLongitude(geoLng);
                    mStartPoint.setLatitude(geoLat);
                    startway = amapLocation.getPoiName();
                    //....:3000/test
                    //Toast.makeText(getActivity().getApplicationContext(),"经度："+geoLat+"纬度："+geoLng,Toast.LENGTH_SHORT).show();
                }
                mlistener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }

    }
    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = AMapUtil.checkEditText(searchText);
//        edt_destination.setText(keyWord);
        if ("".equals(keyWord)) {
            ToastUtil.show(getActivity().getApplicationContext(), "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }
    public void searchButton1(String keyword) {
        keyWord = keyword;
//        edt_destination.setText(keyWord);
        if ("".equals(keyWord)) {
            ToastUtil.show(getActivity().getApplicationContext(), "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
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


    private void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", string);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(getActivity().getApplicationContext(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }



    @Override
    public View getInfoWindow(final Marker marker) {
        Toast.makeText(getActivity().getApplicationContext(),"点击蓝色按钮，设定目的地",Toast.LENGTH_SHORT).show();
        View view = getActivity().getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        // 调起高德地图app
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkpoint){
                  //  checkthread = false;
                    //thread_startpoint.stop();
                    startPoint = marker.getPosition();
                    edt_startpoint.setText(marker.getTitle());
                    edt_startpoint.setTextColor(Color.rgb(0,0,0));
                    mStartPoint = new LatLonPoint(startPoint.latitude,startPoint.longitude);

                }else {
                    edt_destination.setText(marker.getTitle());
                    edt_destination.setTextColor(Color.rgb(0,0,0));
                    destination = marker.getPosition();
                    mEndPoint = new LatLonPoint(destination.latitude, destination.longitude);
                    isChangePoint = true;
           //         Toast.makeText(getActivity().getApplicationContext(), "目的地坐标为：" + destination.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(getActivity().getApplicationContext(), infomation);

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getActivity().getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    searchButton();
                }
            });
            searchText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(getActivity().getApplicationContext(), rCode);
        }

    }

    /**
     * Button点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击搜索按钮
             */
            case R.id.btn_startsearch:
                searchButton();
                break;

            case R.id.btn_startAccept:
                if(mStartPoint != null) {
                    if(isChangePoint){
                        Intent intent = new Intent(getActivity(),OrderRelease_Activity.class);
                        intent.putExtra("startPosLat",String.valueOf(mStartPoint.getLatitude()));
                        intent.putExtra("startPostLng",String.valueOf(mStartPoint.getLongitude()));
                        intent.putExtra("endPostLat",String.valueOf(mEndPoint.getLatitude()));
                        intent.putExtra("endPosLng",String.valueOf(mEndPoint.getLongitude()));
                        intent.putExtra("startPosName",startway);
                        intent.putExtra("endPosName",edt_destination.getText().toString());
                          startActivity(intent);
                        getActivity().overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
                    }else {
                        Toast.makeText(getActivity().getApplication(),"请输入目的地",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity().getApplication(),"请输入出发点",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.fab_map:
                acheck = true;
               // amap.clear();
                if (checkmap) {
                    amap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

                    checkmap = false;
                } else {
                    amap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                    checkmap = true;
                }
                break;
            case R.id.image_search_back:
                card_search.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_route:

                if(mStartPoint != null) {
                    if(isChangePoint){
                        isChangePoint = false ;
                        edt_destination.setText("点击此处，搜索目的地");
                        edt_destination.setTextColor(Color.rgb(158,158,158));
                        Intent intent1=new Intent(getActivity(),BaseNavi_Activity.class);
                        intent1.putExtra("mStartpointLat",mStartPoint.getLatitude());
                        intent1.putExtra("mSatrtpointLng",mStartPoint.getLongitude());
                        intent1.putExtra("mEndpointLat",mEndPoint.getLatitude());
                        intent1.putExtra("mEndpointLng",mEndPoint.getLongitude());
                        User.setmStartPoint(mStartPoint.getLatitude(),mStartPoint.getLongitude());
                        User.setmEndPoing(mEndPoint.getLatitude(),mEndPoint.getLongitude());
                        Toast.makeText(getActivity().getApplicationContext(),"出发点坐标为："+User.mStartPoint+"目的地坐标为："+User.mEndPoing,Toast.LENGTH_SHORT).show();

                        startActivity(intent1);

                        getActivity().overridePendingTransition(R.animator.fade_out,R.animator.fade_in);
                    }else {
                        Toast.makeText(getActivity().getApplication(),"请输入目的地",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity().getApplication(),"请输入出发点",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_startpoint:
                checkpoint = true;
                CoordinatorLayout lyt_coordlyt = (CoordinatorLayout)getView().findViewById(R.id.lyt_coordlytmain);
                Snackbar.make(lyt_coordlyt,
                        "已选定出发点，请在上方搜索框搜索您的出发点",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                break;
            case R.id.tv_endpoint:
                checkpoint=false;
                CoordinatorLayout lyt_coordlyt1 = (CoordinatorLayout)getView().findViewById(R.id.lyt_coordlytmain);
                Snackbar.make(lyt_coordlyt1,
                        "已选定目的地，请在上方搜索框搜索您的目的地",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                break;
            case R.id.edt_startpoint:
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInputFromInputMethod(searchText.getWindowToken(), 0);
                imm.toggleSoftInputFromWindow(searchText.getWindowToken(), 0, InputMethodManager.SHOW_FORCED);
                checkpoint = true;
                CoordinatorLayout lyt_coordlyt2 = (CoordinatorLayout)getView().findViewById(R.id.lyt_coordlytmain);
                Snackbar.make(lyt_coordlyt2,
                        "已选定出发点，请在上方搜索框搜索您的出发点",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                break;
            case R.id.edt_destination:
                checkpoint=false;
                InputMethodManager imm1 = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInputFromInputMethod(searchText.getWindowToken(), 0);
                imm1.toggleSoftInputFromWindow(searchText.getWindowToken(), 0, InputMethodManager.SHOW_FORCED);
                CoordinatorLayout lyt_coordlyt3 = (CoordinatorLayout)getView().findViewById(R.id.lyt_coordlytmain);
                Snackbar.make(lyt_coordlyt3,
                        "已选定目的地，请在上方搜索框搜索您的目的地",Snackbar.LENGTH_LONG).setAction("close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                break;


        }

    }

    private void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(getActivity().getApplicationContext(), "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(getActivity().getApplicationContext(), "终点未设置");
        }
        showProgressDialogDrive();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
         if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    private void showProgressDialogDrive() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity().getApplicationContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    private void acceptData() {
        String[] string = new String[8];
        string[0] = "user";
        string[1]="zhilonng";
        string[2]="passwd";
        string[3]="huangzhilong...0";
        string[4]="tele";
        string[5]="15602383152";
        string[6]="idcard";
        string[7]="445221199406024118";
        JSONObject jsonObject = new JSONObject();
        Transfor_JSON test = new Transfor_JSON();
        jsonObject = test.toJSON(string);
        Toast.makeText(getActivity().getApplicationContext(),jsonObject.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();

        return false;
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialogDrive();// 隐藏搜索对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        amap.clear();// 清理m之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(amap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(getActivity().getApplicationContext(),
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(getActivity().getApplicationContext(),
                        R.string.no_result);

            }
        } else {
            ToastUtil.showerror(getActivity().getApplicationContext(), rCode);
        }

    }

    private void dissmissProgressDialogDrive() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, string);
            Inputtips inputTips = new Inputtips(getActivity().getApplicationContext(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        amap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            getActivity().getApplicationContext(), amap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(getActivity().getApplicationContext(), R.string.no_result);
                }

            } else {
                ToastUtil.show(getActivity().getApplicationContext(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getActivity().getApplicationContext(), errorCode);
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        String curDes = marker.getTitle() + "拖动时当前位置:(lat,lng)\n(" +
                marker.getPosition().latitude + "," + marker.getPosition().longitude + ")";
        BreakIterator markerText = null;
        markerText.setText(curDes);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}


