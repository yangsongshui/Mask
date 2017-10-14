package com.mask.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by ys on 2017/10/13.
 */

public class MyGroup  extends DataSupport {
    String id;
    String name;

    public MyGroup(String name) {
        this.name = name;
    }

    public MyGroup() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
