package com.dk.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dk.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/22.
 * 封装常用的数据库操作
 * 存储和读取，构造方法的私有化
 */
public class CoolWeatherDB {
    /*
    * 数据库名
    * */
    public static final String DB_NAME = "cool_weather";

    /*
    * 数据库版本
    * */
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    /*
    * 将构造方法私有化
    * */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context ,
                DB_NAME , null ,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /*
    * 获得CoolWeatherDB的实例
    * */
//    synchronized
//    当它用来修饰一个方法或者一个代码块的时候，能够保证在同一时刻最多只有一个线程执行该段代码
    public synchronized  static CoolWeatherDB getInstance(Context context ){
        if(coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /*
    *Province将实例储存到数据库
    * */
    public void saveProvince(Province province ){
        if(province != null ){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code" , province.getProvinceCode());
            db.insert("Province" , null ,values);
        }
    }

    /*
    * 从数据库读取全国所有省份的信息
    * */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db
                .query("Province" , null ,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex(
                        "province_code"
                )));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex(
                        "province_name"
                )));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }

    /*
    * 将City实例储存到数据库
    * */
    public void saveCity(City city ){
        if(city != null ){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code" , city.getCityCode());
            values.put("province_id" , city.getProvincedId());
            db.insert("City" , null ,values);
        }
    }

    /*
    *从数据库读取某省下所有的城市信息
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db
                .query("City" , null ,"province_id = ?",
                        new String[]{String.valueOf(provinceId)}, null ,null , null);
//        三、四参数指定了某个具体的城市
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex(
                        "city_code"
                )));
                city.setCityName(cursor.getString(cursor.getColumnIndex(
                        "city_name"
                )));
                city.setProvincedId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
//            先执行语句在判断是否循环
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }

    /*
    * 将County实例储存到数据库
    * */
    public void saveCounty(County county ){
        if(county != null ){
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code" , county.getCountyCode());
            values.put("city_id" , county.getCityId());
            db.insert("County" , null ,values);
        }
    }

    /*
    *从数据库读取某城市下所有的县信息
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db
                .query("County" , null ,"city_id = ?",
                        new String[]{String.valueOf(cityId)}, null ,null , null);
        if(cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex(
                        "county_code"
                )));
                county.setCountyName(cursor.getString(cursor.getColumnIndex(
                        "county_name"
                )));
                county.setCityId(cityId);
                list.add(county);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
