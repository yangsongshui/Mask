package com.mask.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mask.R;
import com.mask.app.MyApplication;
import com.mask.base.BaseActivity;
import com.mask.utils.Toastor;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import retrofit2.Retrofit;

import static com.mask.utils.Constant.ACTION_BLE_NOTIFY_DATA;

public class StartActivity extends BaseActivity {
    private BluetoothAdapter mBluetoothAdapter;
    Toastor toastor;
    private final static int REQUECT_CODE_COARSE = 1;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    Retrofit retrofit;

    @Override
    protected int getContentView() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        MPermissions.requestPermissions(StartActivity.this, REQUECT_CODE_COARSE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        toastor = new Toastor(this);
        initBLE();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUECT_CODE_COARSE)
    public void requestSdcardSuccess() {
        startActivity(new Intent(StartActivity.this, MainActivity.class));
        finish();
    }

    @PermissionDenied(REQUECT_CODE_COARSE)
    public void requestSdcardFailed() {
        toastor.showSingletonToast("程序主要权限获取失败,程序退出");
        finish();
    }

    private void initBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toastor.showSingletonToast("设备不支持蓝牙4.0");
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            toastor.showSingletonToast("设备不支持蓝牙");
            finish();
            return;
        }
        mBluetoothAdapter.enable();
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    Log.e("定位数据", aMapLocation.toString());
                    MyApplication.newInstance().address = aMapLocation.getCity();

                    Intent intent = new Intent();
                    intent.putExtra("address",  aMapLocation.getAddress());
                    intent.setAction(ACTION_BLE_NOTIFY_DATA);
                    sendBroadcast(intent);

                } else {
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    toastor.showSingletonToast("定位失败:" + aMapLocation.getErrorInfo());


                }
            }
        }
    };
}
