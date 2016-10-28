package com.example.administrator.daihuobangv10;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import util.AMapUtil;
import util.ToastUtil;

public class Search_Activity extends AppCompatActivity implements View.OnClickListener ,PoiSearch.OnPoiSearchListener ,Inputtips.InputtipsListener, TextWatcher {

    private AutoCompleteTextView searchText;// 输入搜索关键字
    private static String keyWord = "";// 要输入的poi搜索关键字
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private ImageButton btn_back;
    private ImageButton btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);
        init();
    }

    private void init() {
        searchText = (AutoCompleteTextView) findViewById(R.id.edit_text_search);
        btn_back = (ImageButton)findViewById(R.id.image_search_back);
        btn_search = (ImageButton)findViewById(R.id.btn_startsearch);
        btn_back.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件
    }
    /**
     * 点击搜索按钮
     */
    public String searchButton() {
        keyWord = AMapUtil.checkEditText(searchText);
        if ("".equals(keyWord)) {
            ToastUtil.show(Search_Activity.this, "请输入搜索关键字");
            return keyWord;
        } else {
            doSearchQuery();
        }
        return null;
    }

    private void doSearchQuery() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startsearch:
                if (AMapUtil.checkEditText(searchText)!=null){
                keyWord = AMapUtil.checkEditText(searchText);
                Intent intent = new Intent();
                intent.putExtra("keyword",keyWord);
                    Toast.makeText(getApplicationContext(),"当前搜索为："+keyWord,Toast.LENGTH_SHORT).show();
                intent.setAction("com.ljq.activity.CountService");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
               finish();
                }else {
                    Toast.makeText(getApplicationContext(),"请输入关键字1",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.image_search_back:
                finish();
                break;
        }


    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, "");
            Inputtips inputTips = new Inputtips(Search_Activity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
