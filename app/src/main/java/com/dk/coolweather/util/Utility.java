package com.dk.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.dk.coolweather.model.City;
import com.dk.coolweather.model.CoolWeatherDB;
import com.dk.coolweather.model.County;
import com.dk.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lenovo on 2016/8/23.
 * 解析从网上获得的数据
 *将从服务器返回的数据分别存进province,city,county表中
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB
           coolWeatherDB , String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for(String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province  = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
//                    解析出来的数据储存到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public  static boolean handleCitiesResponse(CoolWeatherDB
                                 coolWeatherDB , String response , int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String p : allCities){
                    String[] array = p.split("\\|");
                    City city  = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvincedId(provinceId);
//                    解析出来的数据储存到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public  static boolean handleCountyResponse(CoolWeatherDB
                   coolWeatherDB , String response , int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String p : allCounties){
                    String[] array = p.split("\\|");
                    County county  = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
//                    解析出来的数据储存到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *解析服务器返回的JSON数据，并将解析的数据储存到本地
     */
    public static void handleWeatherResponse(Context context , String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context , cityName , weatherCode ,temp1 , temp2,
                    weatherDesp ,publishTime );
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    /**
     *将服务器返回的所有天气数据储存到SharedPreferences
     */
    public static void saveWeatherInfo(Context context , String cityName,
      String weatherCode , String temp1 ,String temp2 , String weatherDesp ,
                                       String publishTime){
        SimpleDateFormat adf = new SimpleDateFormat("yyyy年M月d日" , Locale.CHINA);
        SharedPreferences.Editor  editor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        editor.putBoolean("city_selected" , true);
        editor.putString("city_name" , cityName);
        editor.putString("weather_code" ,weatherCode);
        editor.putString("temp1" , temp1);
        editor.putString("temp2" , temp2);
        editor.putString("weather_desp" ,weatherDesp);
        editor.putString("publish_time" , publishTime);
        editor.putString("current_date" , adf.format(new Date()));
        editor.commit();
    }
}
