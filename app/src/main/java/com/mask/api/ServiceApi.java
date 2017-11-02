package com.mask.api;


import com.mask.bean.Weather;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 描述：retrofit的接口service定义
 */
public interface ServiceApi {
    //天气查询接口
    @POST("9-2?showapi_appid=40725&showapi_sign=af0b4f5fee3e41169842eb6093b693f4")
    Call<Weather> getWeather(@Query("area") String address, @Query("needIndex") String needMoreDay);
}
