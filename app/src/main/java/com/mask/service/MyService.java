package com.mask.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightService;

/**
 * Created by Administrator on 2016/12/3.
 */
public class MyService extends LightService {
    private static MyService mThis;

    public static MyService Instance()
    {
        return mThis;
    }

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
}
