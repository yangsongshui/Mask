package com.mask.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mask.app.MyApplication;
import com.mask.bean.Lights;
import com.mask.bean.Mesh;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightService;
import com.telink.bluetooth.light.Parameters;

/**
 * Created by Administrator on 2016/12/3.
 */
public class MyService extends LightService {
    private static MyService mThis;

    public static MyService Instance()
    {
        return mThis;
    }

    public IBinder onBind(Intent paramIntent)
    {
        if (this.mBinder == null) {
            this.mBinder = new LocalBinder();
        }
        return super.onBind(paramIntent);
    }

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
    public void Connect() {


        if (MyService.Instance() != null) {

            if (MyService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                Lights.getInstance().clear();

                if (MyApplication.newInstance().isEmptyMesh()) {
                    return;
                }

                Mesh mesh = MyApplication.newInstance().getMesh();
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                Log.e("-------", mesh.name + mesh.password);
                connectParams.setMeshName(mesh.name);
                connectParams.setPassword(mesh.password);
                connectParams.autoEnableNotification(true);

                MyService.Instance().autoConnect(connectParams);
            }

            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(2000);

            MyService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }
}
