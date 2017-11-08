package com.mask.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.mask.R;
import com.mask.adapter.EquipmentAdapter;
import com.mask.app.MyApplication;
import com.mask.base.BaseActivity;
import com.mask.bean.Light;
import com.mask.bean.Lights;
import com.mask.bean.Mesh;
import com.mask.bean.MyDevice;
import com.mask.service.MyService;
import com.mask.utils.Toastor;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EquipmentActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeMenuListView.OnMenuItemClickListener, EventListener<String> {

    private static final int UPDATE_LIST = 0;

    @BindView(R.id.listView)
    SwipeMenuListView listView;
    EquipmentAdapter adapter;
    List<MyDevice> mList;
    private MyApplication mApplication;
    Toastor toastor;

    @Override
    protected int getContentView() {
        return R.layout.activity_equipment;
    }

    @Override
    protected void init() {
        toastor = new Toastor(this);
        adapter = new EquipmentAdapter(this);
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(this);
        listView.setOnItemClickListener(this);
        this.mApplication = (MyApplication) this.getApplication();
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        this.autoConnect();
    }


    @OnClick({R.id.equipment_left_tv, R.id.iv_equipment_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.equipment_left_tv:
                finish();
                break;
            case R.id.iv_equipment_right:
                MyService.Instance().idleMode(true);
                autoConnect();
                break;
            default:
                break;
        }
    }

    /*添加侧滑菜单*/
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {

            // 删除菜单
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
            // set item width
            deleteItem.setWidth(dp2px(70));
            // 设置内容
            deleteItem.setTitle("删除");
            // 设置字体大小
            deleteItem.setTitleSize(14);
            // 字体颜色
            deleteItem.setTitleColor(getResources().getColor(R.color.white));
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mApplication.setLight(adapter.getItem(position));

        finish();
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        adapter.delete(index);
        adapter.notifyDataSetChanged();
        return false;
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void performed(Event<String> event) {
        //  Log.e("----", event.getType());
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                this.onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:

                this.onDeviceStatusChanged((DeviceEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                this.onServiceConnected((ServiceEvent) event);
                break;
            default:
                break;
        }
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    /**
     * ����{@link NotificationEvent#ONLINE_STATUS}�¼�
     *
     * @param event
     */
    private void onOnlineStatusNotify(NotificationEvent event) {

        TelinkLog.d("Thread ID : " + Thread.currentThread().getId());
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList;
        //noinspection unchecked
        notificationInfoList = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0) {
            return;
        }


        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {

            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;

            Light light = adapter.get(meshAddress);

            if (light == null) {
                light = new Light();
                adapter.add(light);
            }

            light.meshAddress = meshAddress;
            light.brightness = brightness;
            light.status = notificationInfo.connectStatus;
        }

        mHandler.obtainMessage(UPDATE_LIST).sendToTarget();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public void autoConnect() {


        if (MyService.Instance() != null) {

            if (MyService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                Lights.getInstance().clear();

                if (this.mApplication.isEmptyMesh()) {
                    return;
                }

                Mesh mesh = this.mApplication.getMesh();
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

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                toastor.showSingletonToast("login success");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyService.Instance().sendCommand((byte) 0xE4, 0xFFFF, new byte[]{});
                    }
                }, 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:
                toastor.showSingletonToast("login");
                break;
            case LightAdapter.STATUS_LOGOUT:
                toastor.showSingletonToast("disconnect");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mApplication.removeEventListener(this);
    }
}
