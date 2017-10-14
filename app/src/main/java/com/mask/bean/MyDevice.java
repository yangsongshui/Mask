package com.mask.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by ys on 2017/10/13.
 */

public class MyDevice  extends DataSupport {
    String name;

    public MyDevice() {
    }

    public MyDevice(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
