package com.dk.coolweather.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2016/8/23.
 * 开启线程发送网络请求
 * 根据address从网络上获取相应数据
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address ,
                                       final HttpCallbackListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null ;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    if(listener != null) {
//                        回调onFinish方法
//                        接口做参数，接口实例调用接口方法，参数可以回调类实现的接口方法
                        listener.onFinish(response.toString());
                    }
                    }catch (Exception e){
                        if (listener != null){
//                        回调onError方法
                            listener.onError(e);
                    }
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
