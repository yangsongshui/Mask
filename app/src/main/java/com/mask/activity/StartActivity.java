package com.mask.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.mask.R;
import com.mask.base.BaseActivity;
import com.mask.utils.Toastor;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

public class StartActivity extends BaseActivity {
    private BluetoothAdapter mBluetoothAdapter;
    Toastor toastor;
    private final static int REQUECT_CODE_COARSE = 1;

    @Override
    protected int getContentView() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
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
}
