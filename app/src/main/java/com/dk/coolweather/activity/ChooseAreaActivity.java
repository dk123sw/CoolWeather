package com.dk.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dk.coolweather.R;
import com.dk.coolweather.model.City;
import com.dk.coolweather.model.CoolWeatherDB;
import com.dk.coolweather.model.County;
import com.dk.coolweather.model.Province;
import com.dk.coolweather.util.HttpCallbackListener;
import com.dk.coolweather.util.HttpUtil;
import com.dk.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/23.
 * 将省市县的列表显示出来
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY =2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> datalist = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     *县列表
     */
    private List<County> countyList;


    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     *选中的城市
     */
    private City selectedCity;

    /**
     *当前选中的级别
     */
    private int currentLevel;
    /**
     * 是否从WeatherActivity中传递过来
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity" ,false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        已经选择了城市而不是从WeatherActivity跳转过来，才会直接跳转WeatherActivity
        if(prefs.getBoolean("city_selected" ,false)&& !isFromWeatherActivity){
            Intent intent = new Intent(this , WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1
        ,datalist);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//              i参数判断出用户点击了哪个子项
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
//                    获得想要的省
                    queryCities();
                }else if(currentLevel ==LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(i).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this ,
                            WeatherActivity.class);
                    intent.putExtra("county_code" , countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /**
     *查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces(){
        provinceList = coolWeatherDB.loadProvinces();
        if(provinceList.size() > 0){
            datalist.clear();
            for(Province province  : provinceList){
                datalist.add(province.getProvinceName());
            }
//            更新adapter
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromSever(null , "province");
        }
    }

    /**
     *查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities(){
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size() > 0){
            datalist.clear();
            for(City city  : cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromSever(selectedProvince.getProvinceCode() , "city");
        }
    }

    /**
     *查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties(){
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size() > 0){
            datalist.clear();
            for(County county  : countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromSever(selectedCity.getCityCode() , "county");
        }
    }

    /**
     *根据传入的代号和类型从服务器上查询省市县数据
     */
    private void queryFromSever(final String code , final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code +
                    ".xml";
        }else{
            address  ="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(coolWeatherDB,
                            response);
                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB,
                            response, selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(coolWeatherDB,
                            response, selectedCity.getId());
                }
                if(result){
//                    通过runOnUiThread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals((type))){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
//                    通过runOnUiThread方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this ,
                                "加载失败" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     *显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载... ...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     *关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     *捕获back键，根据当前级别来判断，此时应该返回市列表，省列表，还是直接退出
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(this , WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
