package com.mask.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.mask.app.MyApplication;
import com.mask.base.BaseActivity;
import com.mask.utils.SpUtils;
import com.mask.utils.Toastor;
import com.mask.zxing.encoding.EncodingHandler;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.codeboy.android.aligntextview.AlignTextView;

import static com.mask.utils.Constant.HEAD_PORTRAIT;
import static com.mask.utils.Constant.id;

public class MainActivity extends BaseActivity {


    @BindView(R.id.main_aqi)
    TextView mainAqi;
    @BindView(R.id.imageView)
    ImageView imageView;
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
    @BindView(R.id.main_pm)
    TextView mainPm;
    @BindView(R.id.main_aqi2)
    TextView mainAqi2;
    @BindView(R.id.main_guolv)
    TextView mainGuolv;
    @BindView(R.id.main_yuji)
    TextView mainYuji;
    @BindView(R.id.main_zhuangtai)
    TextView mainZhuangtai;
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
    private BluetoothAdapter mBluetoothAdapter;
    Toastor toastor;
    private final static int REQUECT_CODE_COARSE = 1;
    private MyApplication mApplication;
    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        toastor = new Toastor(this);
        initBLE();
        this.mApplication = (MyApplication) this.getApplication();
        this.mApplication.doInit();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mePicIv.setImageResource(id[SpUtils.getInt(HEAD_PORTRAIT, 0)]);
        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_COARSE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

    @PermissionGrant(REQUECT_CODE_COARSE)
    public void requestSdcardSuccess() {


    }

    @PermissionDenied(REQUECT_CODE_COARSE)
    public void requestSdcardFailed() {
        toastor.showSingletonToast("程序主要权限获取失败,程序退出");
        finish();
    }

}
