package com.mask.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.mask.R;
import com.mask.api.ServiceApi;
import com.mask.app.MyApplication;
import com.mask.base.BaseActivity;
import com.mask.bean.Light;
import com.mask.bean.Lights;
import com.mask.bean.Weather;
import com.mask.monitor.OnKeyDownListener;
import com.mask.service.MyService;
import com.mask.utils.ListenerKeyBackEditText;
import com.mask.utils.SpUtils;
import com.mask.utils.Toastor;
import com.telink.TelinkApplication;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.util.Arrays;

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
import static com.mask.utils.ToHex.StringToHex;

public class MainActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {


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
    @BindView(R.id.time_ed)
    ListenerKeyBackEditText timeEd;
    Toastor toastor;
    private MyApplication mApplication;
    Retrofit retrofit;
    Handler handler;
    Runnable myRunnable;
    public static final byte OPCODE = (byte) 0xEA;
    private ProgressDialog pd;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        TelinkLog.ENABLE = true;
        toastor = new Toastor(this);
        this.mApplication = MyApplication.newInstance();
        this.mApplication.doInit();
        handler = new Handler();
        pd = new ProgressDialog(this);
        pd.setMessage("数据加载中...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(ACTION_BLE_NOTIFY_DATA);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mReceiver, filter);
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WEATHER_URL)
                .build();
        pd.show();
        seekbarSelf.setOnSeekBarChangeListener(this);
        timeEd.setOnKeyDownListener(new OnKeyDownListener() {
            @Override
            public void OnKeyDown(int keyCode, KeyEvent event) {
                timeEd.setVisibility(View.GONE);
                mainTiem.setVisibility(View.VISIBLE);
                if (!timeEd.getText().toString().equals("")) {
                    mainTiem.setText(timeEd.getText().toString());
                }
            }
        });
    }

    @OnClick({R.id.main_cehua, R.id.main_tiem, R.id.main_fenxiang, R.id.me_pic_iv, R.id.main_equipment_tv, R.id.main_add_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_cehua:
                //侧滑
                activityMain.openDrawer(GravityCompat.START);
                break;
            case R.id.main_fenxiang:
                MyService.Instance().sendCommandNoResponse(OPCODE, mApplication.getLight().meshAddress, Arrays.hexToBytes("AF0100000E"));

             /*   //分享
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
                }*/

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
            case R.id.main_tiem:
                timeEd.setVisibility(View.VISIBLE);
                mainTiem.setVisibility(View.GONE);
                timeEd.requestFocus();
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityMain.closeDrawer(Gravity.LEFT);

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

        mApplication.setOnDeviceNotifyData(new TelinkApplication.onDeviceNotifyData() {
            @Override
            public void onNotifyData(int opcode, int src, byte[] params, DeviceInfo deviceInfo) {
                Log.e("ColorFragmen", src + "  " + Arrays.bytesToHexString(params, ""));
                if (mApplication.getLight() != null) {
                    if (mApplication.getLight().meshAddress == src) {

                        String data = Arrays.bytesToHexString(params, "");
                        mainYuji.setText(data);
                        if (params[0] == (byte)0xAF) {
                            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                           // setData(params);


                        }


                    }
                }
            }
        });
        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否打开蓝牙");
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeBluetooth.getInstance().enable(getApplicationContext());
                }
            });
            builder.show();
        }
        if (mApplication.getLight() != null) {
            Log.e("ColorFragmen", mApplication.getConnectDevice().deviceName + " " + mApplication.getConnectDevice().meshAddress);
            final Light light = mApplication.getLight();
            mainName.setText("口罩" + light.getLabel2() + ":");
            if (light.status == ConnectionStatus.ON) {
                mainZhuangtai.setText("已开启");
            } else {
                mainZhuangtai.setText("未开启");
            }

            //PM2.5
            MyService.Instance().sendCommandNoResponse(OPCODE, light.meshAddress, Arrays.hexToBytes("AF0100000E"));
            myRunnable = new Runnable() {
                @Override
                public void run() {
                    //电量
                    MyService.Instance().sendCommandNoResponse(OPCODE, light.meshAddress, Arrays.hexToBytes("AF0300000E"));
                }
            };
            handler.postDelayed(myRunnable, 1000);
        }


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            String action = intent.getAction();
            if (ACTION_BLE_NOTIFY_DATA.equals(action)) {
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
        String aqi = weather.getShowapi_res_body().getNow().getAqi();
        mainPm.setText(String.format(getString(R.string.pm25), weather.getShowapi_res_body().getNow().getAqiDetail().getPm2_5()));
        mainAqi.setText(aqi);
        mainAqi2.setText(aqi);
        mainHint.setText("温馨提示：" + weather.getShowapi_res_body().getF1().getIndex().getTravel().getDesc());
        setView(Integer.valueOf(aqi));
        setDianchi(50, Integer.valueOf(aqi));
        //下发PM2.5的值
        if (mApplication.getLight() != null) {
            String msg = "AF08" + StringToHex(weather.getShowapi_res_body().getNow().getAqiDetail().getPm2_5()) + "0E";
            MyService.Instance().sendCommandNoResponse(OPCODE, mApplication.getLight().meshAddress, Arrays.hexToBytes(msg));
        }
    }

    private void setView(int AQI) {
        if (AQI < 50) {
            guangyun.setImageResource(R.drawable.guangyun);
            activityMain.setBackgroundResource(R.drawable.bg);
            mainYou.setText("优");
            mainJibie.setText("一级");
            mainYou.setBackgroundResource(R.drawable.pm_you);
            mainAqi.setBackgroundResource(R.drawable.yuanbai);
        } else if (AQI < 100) {
            guangyun.setImageResource(R.drawable.guangyun2);
            activityMain.setBackgroundResource(R.drawable.bg2);
            mainYou.setText("良");
            mainJibie.setText("二级");
            mainTianqi.setImageResource(R.drawable.tianqi);
            mainYou.setBackgroundResource(R.drawable.pm_liang);
        } else if (AQI < 150) {
            guangyun.setImageResource(R.drawable.guangyun3);
            activityMain.setBackgroundResource(R.drawable.bg3);
            mainTianqi.setImageResource(R.drawable.tianqi);
            mainYou.setText("轻度污染");
            mainJibie.setText("三级");
            mainYou.setBackgroundResource(R.drawable.pm_zhong);
        } else if (AQI < 200) {
            guangyun.setImageResource(R.drawable.guangyun4);
            activityMain.setBackgroundResource(R.drawable.bg4);
            mainTianqi.setImageResource(R.drawable.tianqi2);
            mainYou.setText("中度污染");
            mainJibie.setText("四级");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong);
        } else if (AQI < 300) {
            guangyun.setImageResource(R.drawable.guangyun5);
            mainTianqi.setImageResource(R.drawable.tianqi2);
            activityMain.setBackgroundResource(R.drawable.bg4);
            mainYou.setText("重度污染");
            mainJibie.setText("五级");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong2);
        } else if (AQI < 500) {
            guangyun.setImageResource(R.drawable.guangyun6);
            activityMain.setBackgroundResource(R.drawable.bg5);
            mainYou.setText("严重污染");
            mainJibie.setText("六级");
            mainYou.setBackgroundResource(R.drawable.pm_yanzhong3);
        } else {
            guangyun.setImageResource(R.drawable.guangyun6);
            activityMain.setBackgroundResource(R.drawable.bg5);
            mainJibie.setText("六级");
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //档位
        if (mApplication.getLight() != null) {
            String msg = "AF06" + StringToHex(seekBar.getProgress() + "") + "0E";
            MyService.Instance().sendCommandNoResponse(OPCODE, mApplication.getLight().meshAddress, Arrays.hexToBytes(msg));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                timeEd.setVisibility(View.GONE);
                mainTiem.setVisibility(View.VISIBLE);
                if (!timeEd.getText().toString().equals("")) {
                    mainTiem.setText(timeEd.getText().toString());
                }
                if (hideInputMethod(this, v)) {
                    return false; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件

                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    public static Boolean hideInputMethod(Context context, View v) {

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    private void setData(byte[] data) {
        //设备数据
        switch (data[1]) {
            case 0x01:
            case 0x02:
                mainPm.setText(String.format(getString(R.string.pm25), "20"));
                break;
            case 0x03:
                //电量
                mainYuji.setText(String.format(getString(R.string.time), "2"));
                mainDianliang.setText("50%");
            case 0x04:
                //电量
                mainYuji.setText(String.format(getString(R.string.time), "2"));
                mainDianliang.setText("50%");
                break;
            case 0x06:
                break;
            case 0x07:
                break;
            default:
                //过滤膜
                mainGuolv.setText(String.format(getString(R.string.guolv), "10"));
                break;
        }
    }
}
