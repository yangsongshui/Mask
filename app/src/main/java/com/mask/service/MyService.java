package com.mask.service;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mask.app.MyApplication;
import com.mask.bean.Light;
import com.mask.bean.Lights;
import com.mask.bean.Mesh;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightService;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.List;

/**
 * Created by Administrator on 2016/12/3.
 */
public class MyService extends LightService implements EventListener<String> {
    private static MyService mThis;
    private MyApplication mApplication;
    private int connectMeshAddress;

    public static MyService Instance() {
        return mThis;
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        if (this.mBinder == null) {
            this.mBinder = new LocalBinder();
        }
        return super.onBind(paramIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
        if (this.mAdapter == null) {
            this.mAdapter = new LightAdapter();
        }
        this.mApplication = (MyApplication) this.getApplication();
        this.mApplication.doInit();
        Log.e("----", "服务启动了。。");
        this.mAdapter.start(this);
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        this.mApplication.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        this.mApplication.addEventListener(MeshEvent.OFFLINE, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                this.onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                this.onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                this.onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
                this.onMeshError((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                this.onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                this.onServiceDisconnected((ServiceEvent) event);
                break;
            default:
                break;
        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public MyService getService() {
            return MyService.this;
        }
    }

    /*搜索设备*/
    public void Connect() {

        if (MyService.Instance() != null) {

            if (MyService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {

                Lights.getInstance().clear();
                // this.deviceFragment.notifyDataSetChanged();

                if (this.mApplication.isEmptyMesh()) {
                    return;
                }
                Mesh mesh = this.mApplication.getMesh();
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                // Log.e("-------", mesh.name + mesh.password);
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        //蓝牙打开
                        MyService.Instance().idleMode(true);
                        Connect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        //蓝牙关闭

                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void onOnlineStatusNotify(NotificationEvent event) {

        TelinkLog.d("Thread ID : " + Thread.currentThread().getId());
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList;
        //noinspection unchecked
        notificationInfoList = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0) {
            return;
        }

       /* if (this.deviceFragment != null) {
            this.deviceFragment.onNotify(notificationInfoList);
        }
*/
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {

            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            //判断设备是否已存在
             Light light = mApplication.get(meshAddress);
            if (light == null) {
                light = new Light();
                mApplication.add(light);
            }

            light.meshAddress = meshAddress;
            light.brightness = brightness;
            light.status = notificationInfo.connectStatus;
            //搜索到的设备


        }

        //mHandler.obtainMessage(UPDATE_LIST).sendToTarget();
    }

    private void onServiceConnected(ServiceEvent event) {
        this.Connect();
    }

    private void onServiceDisconnected(ServiceEvent event) {

    }

    private void onMeshOffline(MeshEvent event) {

        List<Light> lights = Lights.getInstance().get();
        for (Light light : lights) {
            light.status = ConnectionStatus.OFFLINE;

        }

    }

    private void onMeshError(MeshEvent event) {
        //new AlertDialog.Builder(this).setMessage("蓝牙出问题了，重启蓝牙试试!!").show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        this.mApplication.doDestroy();
        Lights.getInstance().clear();
    }
    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyService.Instance().sendCommandNoResponse((byte) 0xE4, 0xFFFF, new byte[]{});
                    }
                }, 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:

                break;
            case LightAdapter.STATUS_LOGOUT:

                break;
            default:
                break;
        }
    }
}
