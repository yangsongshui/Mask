package com.mask.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.kyleduo.switchbutton.SwitchButton;
import com.mask.R;
import com.mask.api.ServiceApi;
import com.mask.app.MyApplication;
import com.mask.base.BaseActivity;
import com.mask.bean.Light;
import com.mask.bean.Lights;
import com.mask.bean.Mesh;
import com.mask.bean.Weather;
import com.mask.service.MyService;
import com.mask.utils.SpUtils;
import com.mask.utils.Toastor;
import com.mask.zxing.encoding.EncodingHandler;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
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
import de.hdodenhof.circleimageview.CircleImageView;
import me.codeboy.android.aligntextview.AlignTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mask.utils.Constant.ACTION_BLE_NOTIFY_DATA;
import static com.mask.utils.Constant.HEAD_PORTRAIT;
import static com.mask.utils.Constant.WEATHER_URL;
import static com.mask.utils.Constant.id;

public class MainActivity extends BaseActivity implements EventListener<String> {


    @BindView(R.id.main_aqi)
    TextView mainAqi;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.guangyun)
    ImageView guangyun;
    @BindView(R.id.main_fenxiang)
    ImageView mainFenxiang;
    @BindView(R.id.main_cehua)
    ImageView mainCehua;
    @BindView(R.id.main_address)
    AlignTextView mainAddress;
    @BindView(R.id.main_hint)
    AlignTextView mainHint;
    @BindView(R.id.main_tianqi)
    ImageView mainTianqi;
    @BindView(R.id.main_you)
    TextView mainYou;
    @BindView(R.id.main_jibie)
    TextView mainJibie;
    @BindView(R.id.aqi)
    TextView aqi;
    @BindView(R.id.main_pm)
    TextView mainPm;
    @BindView(R.id.main_aqi2)
    TextView mainAqi2;
    @BindView(R.id.kongqi)
    TextView kongQi;
    @BindView(R.id.main_guolv)
    TextView mainGuolv;
    @BindView(R.id.main_yuji)
    TextView mainYuji;
    @BindView(R.id.main_zhuangtai)
    TextView mainZhuangtai;
    @BindView(R.id.main_name)
    TextView mainName;
    @BindView(R.id.main_dianliang)
    TextView mainDianliang;
    @BindView(R.id.main_dianchi)
    ImageView mainDianchi;
    @BindView(R.id.seekbar_self)
    SeekBar seekbarSelf;
    @BindView(R.id.main_tiem)
    TextView mainTiem;
    @BindView(R.id.set_dingshi)
    SwitchButton setDingshi;
    @BindView(R.id.me_pic_iv)
    CircleImageView mePicIv;
    @BindView(R.id.activity_main)
    DrawerLayout activityMain;
    @BindView(R.id.QrCode)
    ImageView qrCode;
    @BindView(R.id.main_kongzhi)
    LinearLayout mainKongZhi;
    Toastor toastor;
    private MyApplication mApplication;
    Retrofit retrofit;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        TelinkLog.ENABLE = true;
        toastor = new Toastor(this);
        this.mApplication = (MyApplication) this.getApplication();
        this.mApplication.doInit();
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        this.mApplication.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        this.mApplication.addEventListener(MeshEvent.OFFLINE, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);
        this.autoConnect();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(ACTION_BLE_NOTIFY_DATA);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mReceiver, filter);
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WEATHER_URL)
                .build();
    }

    @OnClick({R.id.main_cehua, R.id.main_fenxiang, R.id.me_pic_iv, R.id.main_equipment_tv, R.id.main_add_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_cehua:
                //侧滑
                activityMain.openDrawer(GravityCompat.START);

                break;
            case R.id.main_fenxiang:
                //分享
                mainKongZhi.setVisibility(View.GONE);
                qrCode.setVisibility(View.VISIBLE);
                try {
                    //获取输入的文本信息
                    String str = "123456";
                    if (str != null && !"".equals(str.trim())) {
                        //根据输入的文本生成对应的二维码并且显示出来
                        Bitmap mBitmap = EncodingHandler.createQRCode(str, 500);
                        if (mBitmap != null) {
                            Toast.makeText(this, "二维码生成成功！", Toast.LENGTH_SHORT).show();
                            qrCode.setImageBitmap(mBitmap);
                        }
                    } else {
                        Toast.makeText(this, "文本信息不能为空！", Toast.LENGTH_SHORT).show();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.me_pic_iv:
                //头像
                startActivity(new Intent(this, PortraitActivity.class));
                break;
            case R.id.main_equipment_tv:
                //设备列表
                startActivity(new Intent(this, EquipmentActivity.class));

                break;
            case R.id.main_add_tv:
                //添加组
                startActivity(new Intent(this, GroupActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityMain.closeDrawer(Gravity.LEFT);
        this.mApplication.removeEventListener(this);
        MyService.Instance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        this.mApplication.doDestroy();
        Lights.getInstance().clear();
    }

    //截取屏幕分享
 /*   private void UMShare(SHARE_MEDIA platform) {

        View viewScreen = getWindow().getDecorView();
        viewScreen.setDrawingCacheEnabled(true);
        viewScreen.buildDrawingCache();
        //获取当前屏幕的大小
        int width = getWindow().getDecorView().getRootView().getWidth();
        int height = getWindow().getDecorView().getRootView().getHeight();
        Bitmap bitmap = Bitmap.createBitmap(viewScreen.getDrawingCache(), 0, 0, width, height);
        viewScreen.destroyDrawingCache();
        UMImage image = new UMImage(HistoryActivity.this, bitmap);//bitmap文件
        image.compressStyle = UMImage.CompressStyle.QUALITY;
        new ShareAction(HistoryActivity.this).setPlatform(platform)
                .withText("飘爱检测仪")
                .withMedia(image)
                .setCallback(umShareListener)
                .share();
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        mePicIv.setImageResource(id[SpUtils.getInt(HEAD_PORTRAIT, 0)]);
        if (!LeBluetooth.getInstance().isSupport(getApplicationContext())) {
            Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("123");
            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //LeBluetooth.getInstance().enable(getApplicationContext());
                }
            });
            builder.show();
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
                        autoConnect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        toastor.showSingletonToast("蓝牙关闭");
                        break;

                    default:
                        break;
                }
            } else if (ACTION_BLE_NOTIFY_DATA.equals(action)) {
                mainAddress.setText(intent.getStringExtra("address"));
                ServiceApi service = retrofit.create(ServiceApi.class);
                Call<Weather> call = service.getWeather(MyApplication.newInstance().address, "1");
                call.enqueue(new Callback<Weather>() {
                    @Override
                    public void onResponse(Call<Weather> call, Response<Weather> response) {
                        //请求成功操作
                        Weather weather = response.body();
                        Log.e("weather", weather.toString());
                        if (weather.getShowapi_res_code() == 0) {
                            initWeather(weather);

                        } else {
                            toastor.showSingletonToast("天气查询失败");

                        }

                    }

                    @Override
                    public void onFailure(Call<Weather> call, Throwable t) {
                        //请求失败操作

                        toastor.showSingletonToast("天气查询失败");
                    }
                });
            }
        }
    };

    private void initWeather(Weather weather) {
        String  aqi=weather.getShowapi_res_body().getNow().getAqi();
        mainAqi.setText(aqi);
        mainHint.setText("温馨提示：" + weather.getShowapi_res_body().getF1().getIndex().getTravel().getDesc());
        setView(Integer.valueOf(aqi));
        setDianchi(50, Integer.valueOf(aqi));
    }

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
                        MyService.Instance().sendCommandNoResponse((byte) 0xE4, 0xFFFF, new byte[]{});
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

            Light light = mApplication.get(meshAddress);

            if (light == null) {
                light = new Light();
                this.mApplication.add(light);
            }

            light.meshAddress = meshAddress;
            light.brightness = brightness;
            light.status = notificationInfo.connectStatus;
        }

        //mHandler.obtainMessage(UPDATE_LIST).sendToTarget();
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
            case MeshEvent.OFFLINE:
                //this.onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
                this.onMeshError((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                this.onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                // this.onServiceDisconnected((ServiceEvent) event);
                break;
            default:
                break;
        }
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    private void onMeshError(MeshEvent event) {
        new AlertDialog.Builder(this).setMessage("蓝牙出问题了，重启蓝牙试试!!").show();
    }

    private void setView(int AQI) {
        if (AQI < 50) {
            guangyun.setImageResource(R.drawable.guangyun);
            activityMain.setBackgroundResource(R.drawable.bg);
            mainYou.setText("优");
            mainYou.setBackgroundResource(R.drawable.pm_you);
            mainAqi.setBackgroundResource(R.drawable.yuanbai);
        } else if (AQI < 100) {
            guangyun.setImageResource(R.drawable.guangyun2);
            activityMain.setBackgroundResource(R.drawable.bg2);
            mainYou.setText("良");
            mainTianqi.setImageResource(R.drawable.tianqi);
            mainYou.setBackgroundResource(R.drawable.pm_liang);
        } else if (AQI < 150) {
            guangyun.setImageResource(R.drawable.guangyun3);
            activityMain.setBackgroundResource(R.drawable.bg3);
            mainTianqi.setImageResource(R.drawable.tianqi);
            mainYou.setText("轻度污染");
            mainYou.setBackgroundResource(R.drawable.pm_zhong);
        } else if (AQI < 200) {
            guangyun.setImageResource(R.drawable.guangyun4);
            activityMain.setBackgroundResource(R.drawable.bg4);
            mainTianqi.setImageResource(R.drawable.tianqi2);
            mainYou.setText("中度污染");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong);
        } else if (AQI < 300) {
            guangyun.setImageResource(R.drawable.guangyun5);
            mainTianqi.setImageResource(R.drawable.tianqi2);
            activityMain.setBackgroundResource(R.drawable.bg4);
            mainYou.setText("重度污染");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong2);
        } else if (AQI < 500) {
            guangyun.setImageResource(R.drawable.guangyun6);
            activityMain.setBackgroundResource(R.drawable.bg5);
            mainYou.setText("严重污染");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong3);
        } else {
            guangyun.setImageResource(R.drawable.guangyun6);
            activityMain.setBackgroundResource(R.drawable.bg5);

            mainYou.setText("严重污染");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong3);
        }
        imageView.setImageResource(AQI > 150 ? R.drawable.dingwei2 : R.drawable.dingwei);
        mainTianqi.setImageResource(AQI > 150 ? R.drawable.tianqi2 : R.drawable.tianqi);
        mainAqi.setBackgroundResource(AQI > 150 ? R.drawable.yuanhei : R.drawable.yuanbai);
        mainFenxiang.setImageResource(AQI > 150 ? R.drawable.fenxiang2 : R.drawable.fenxiang);
        mainCehua.setImageResource(AQI > 150 ? R.drawable.gengduo2 : R.drawable.gengduo);
        mainAqi.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainJibie.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainAddress.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        kongQi.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainHint.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainYuji.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainDianliang.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainZhuangtai.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        mainName.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
        aqi.setTextColor(AQI > 150 ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));

    }

    private void setDianchi(int dianliang, int AQI) {
        if (AQI > 150) {
            mainDianchi.setImageResource(R.drawable.dianchi2);
        } else {
            mainDianchi.setImageResource(R.drawable.dianchi);
        }
    }
}
