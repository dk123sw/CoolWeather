package com.dk.coolweather.util;

/**
 * Created by lenovo on 2016/8/23.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
