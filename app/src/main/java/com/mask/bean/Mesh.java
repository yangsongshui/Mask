package com.mask.bean;

import com.mask.utils.FileSystem;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.util.MeshUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Mesh implements Serializable {

    private static final long serialVersionUID = 1L;

    public String name;
    public String password;
    public String factoryName;
    public String factoryPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getFactoryPassword() {
        return factoryPassword;
    }

    public void setFactoryPassword(String factoryPassword) {
        this.factoryPassword = factoryPassword;
    }

    public List<Integer> getAllocDeviceAddress() {
        return allocDeviceAddress;
    }

    public void setAllocDeviceAddress(List<Integer> allocDeviceAddress) {
        this.allocDeviceAddress = allocDeviceAddress;
    }

    public List<DeviceInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceInfo> devices) {
        this.devices = devices;
    }
//public String otaDevice;

    public List<Integer> allocDeviceAddress;
    public List<DeviceInfo> devices = new ArrayList<>();

    public int getDeviceAddress() {
        int address = MeshUtils.allocDeviceAddress(this.allocDeviceAddress);

        if (address != -1) {
            if (this.allocDeviceAddress == null)
                this.allocDeviceAddress = new ArrayList<>();
            this.allocDeviceAddress.add(address);
        }

        return address;
    }

    public DeviceInfo getDevice(int meshAddress) {
        if (this.devices == null)
            return null;

        for (DeviceInfo info : devices) {
            if (info.meshAddress == meshAddress)
                return info;
        }
        return null;
    }

    public boolean saveOrUpdate() {
        return FileSystem.writeAsObject("telink.meshs", this);
    }
}
