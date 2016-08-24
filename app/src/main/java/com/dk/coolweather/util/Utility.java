package com.dk.coolweather.util;

import android.text.TextUtils;

import com.dk.coolweather.model.City;
import com.dk.coolweather.model.CoolWeatherDB;
import com.dk.coolweather.model.County;
import com.dk.coolweather.model.Province;

/**
 * Created by lenovo on 2016/8/23.
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
}
