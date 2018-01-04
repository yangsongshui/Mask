package com.mask.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mask.app.MyApplication;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightService;

import static com.mask.utils.Constant.ACTION_BLE_NOTIFY_DATA;

/**
 * Created by Administrator on 2016/12/3.
 */
public class MyService extends LightService {
    private static MyService mThis;

    public static MyService Instance()
    {
        return mThis;
    }
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    @Override
    public IBinder onBind(Intent paramIntent)
    {
        if (this.mBinder == null) {
            this.mBinder = new LocalBinder();
        }
        return super.onBind(paramIntent);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mThis = this;
        if (this.mAdapter == null) {
            this.mAdapter = new LightAdapter();
        }
        Log.e("----","服务启动了。。");
        this.mAdapter.start(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(60000);
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public class LocalBinder
            extends Binder
    {
        public LocalBinder() {}

        public MyService getService()
        {
            return MyService.this;
        }
    }
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                Intent intent = new Intent();
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    Log.e("定位数据", aMapLocation.toString());
                    if (!aMapLocation.getCity().isEmpty()){
                        MyApplication.newInstance().address = aMapLocation.getCity();
                        intent.putExtra("address", aMapLocation.getAddress());
                        intent.setAction(ACTION_BLE_NOTIFY_DATA);
                        sendBroadcast(intent);
                    }
                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }

            }
        }
    };
}
