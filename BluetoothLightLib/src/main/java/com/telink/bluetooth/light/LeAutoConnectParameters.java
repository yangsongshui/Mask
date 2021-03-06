/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.light;

/**
 * 自动重连参数
 * <p>{@link LeAutoConnectParameters}定义了{@link LightService#autoConnect(Parameters)} 方法的必须要设置的几项参数.
 *
 * @see LightService#autoConnect(Parameters)
 */
public final class LeAutoConnectParameters extends Parameters {

    /**
     * 创建{@link LeAutoConnectParameters}实例
     *
     * @return
     */
    public static LeAutoConnectParameters create() {
        return new LeAutoConnectParameters();
    }

    /**
     * 网络名
     *
     * @param value
     * @return
     */
    public LeAutoConnectParameters setMeshName(String value) {
        this.set(PARAM_MESH_NAME, value);
        return this;
    }

    /**
     * 密码
     *
     * @param value
     * @return
     */
    public LeAutoConnectParameters setPassword(String value) {
        this.set(PARAM_MESH_PASSWORD, value);
        return this;
    }

    /**
     * 是否在登录后开启打开Notification
     *
     * @param value
     * @return
     */
    public LeAutoConnectParameters autoEnableNotification(boolean value) {
        this.set(PARAM_AUTO_ENABLE_NOTIFICATION, value);
        return this;
    }

    /**
     * 连接超时时间,单位秒.
     *
     * @param timeoutSeconds
     * @return
     */
    public LeAutoConnectParameters setTimeoutSeconds(int timeoutSeconds) {
        this.set(PARAM_TIMEOUT_SECONDS, timeoutSeconds);
        return this;
    }
}
