package com.mask.utils;


import java.util.UUID;

/**
 * <br />
 * created by CxiaoX at 2017/1/12 11:48.
 */

public class Constant {

    public static final String SP_KEY_BLE_IS_BIND = "isBind";
    public static final String SP_KEY_BLE_BIND_MAC = "bindMac";
    //设备连接成功广播
    public static final String SUCCESSFUL_DEVICE_CONNECTION = "com.doorlock.SUCCESSFUL_DEVICE_CONNECTION";
    //断开
    public static final String EQUIPMENT_DISCONNECTED = "com.doorlock.EQUIPMENT_DISCONNECTED";
    //发送设备数据广播
    public static final String ACTION_BLE_NOTIFY_DATA = "com.doorlock.ACTION_BLE_NOTIFY_DATA";

    /**
     * 协议服务的UUID
     */
    public final static UUID UUID_SERVICE = UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb7");

    /**
     * 控制功能, write 属性<br />
     */
    public final static UUID UUID_CHARACTERISTIC_CONTROL = UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cba");

    /**
     * 实时数据，notify属性<br />
     */
    public final static UUID UUID_CHARACTERISTIC_NOTIFY_DATA = UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8");
    public final static int BASESTX=0xFE;
    public final static int BASEDATA=0x01;


}
