package com.feng.custom.zookeeper.component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ZkProperties {
    /**
     * zk地址
     */
    private String address;
    /**
     * zk等待客户端通信的最长时间, 以毫秒为单位, 一般设置超时时间为5~10秒
     */
    private Integer sessionTimeout;

    public ZkProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("zk.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        setAddress(properties.getProperty("zk.address"));
        setSessionTimeout(Integer.valueOf(properties.getProperty("zk.sessionTimeout", "15000")));
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}
