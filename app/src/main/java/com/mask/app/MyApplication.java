package com.mask.app;

import android.app.Activity;
import android.util.Log;

import com.mask.bean.Light;
import com.mask.bean.Mesh;
import com.mask.service.MyService;
import com.mask.utils.FileSystem;
import com.mask.utils.MySampleAdvanceStrategy;
import com.mask.utils.SpUtils;
import com.telink.TelinkApplication;
import com.telink.bluetooth.light.AdvanceStrategy;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by omni20170501 on 2017/6/8.
 */

public class MyApplication extends TelinkApplication {
    private static final String TAG = "MyApplication";
    private static MyApplication instance;
    public static List<Activity> activitiesList = new ArrayList<Activity>(); // 活动管理集合
    private Mesh mesh;
    public String address;
    public Light light;
    /**
     * 获取单例
     *
     * @return MyApplication
     */
    public static MyApplication newInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LitePal.initialize(this);
        SpUtils.init(this);
        AdvanceStrategy.setDefault(new MySampleAdvanceStrategy());
        this.startLightService(MyService.class);
        mesh = new Mesh();
        mesh.name = "AT-mesh";
        mesh.password = "123456";
        mesh.factoryName = "AT-mesh";
        mesh.factoryPassword = "123456";

    }

    /**
     * 把活动添加到活动管理集合
     *
     * @param activity
     */
    public void addActyToList(Activity activity) {
        if (!activitiesList.contains(activity)) {
            activitiesList.add(activity);
        }
    }

    /**
     * 把活动从活动管理集合移除
     *
     * @param activity
     */
    public void removeActyFromList(Activity activity) {
        if (activitiesList.contains(activity)) {
            activitiesList.remove(activity);
        }
    }

    /**
     * 程序退出
     */
    public void clearAllActies() {
        for (Activity acty : activitiesList) {
            if (acty != null) {
                acty.finish();
            }
        }

    }

    @Override
    public void doInit() {
        super.doInit();
        if (mesh.saveOrUpdate()) {
            setMesh(mesh);
            Log.e("-------", "Save Mesh Success1");
        } else {
            Log.e("-------", "Save Mesh Success2");
        }
        //AES.Security = true;
        if (FileSystem.exists("telink.meshs")) {
            this.mesh = (Mesh) FileSystem.readAsObject("telink.meshs");
        }
        //启动LightService
        this.startLightService(MyService.class);
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public boolean isEmptyMesh() {
        return this.mesh == null;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }
}
