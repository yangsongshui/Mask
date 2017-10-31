package com.mask.bean;

import android.content.res.ColorStateList;

import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;


public final class Light {

    public String name;
    public String macAddress;
    public int meshAddress;
    public int brightness;
    public int color;
    public int temperature;
    public ConnectionStatus status;
    public DeviceInfo raw;
    public boolean selected;
    public ColorStateList textColor;

    public String getLabel() {
        return Integer.toString(this.meshAddress, 16) + ":" + this.brightness;
    }

    public String getLabel1() {
        return this.name + " : " + Integer.toString(this.meshAddress, 16);
    }

    public String getLabel2() {
        return Integer.toString(this.meshAddress, 16);
    }



}
