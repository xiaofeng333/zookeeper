package com.feng.custom.zookeeper.component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ZkProperties {
    private static final Object LOCK = new Object();
    private static volatile ZkProperties zkProperties;
    /**
     * zk地址
     */
    private String address;
    /**
     * zk等待客户端通信的最长时间, 以毫秒为单位, 一般设置超时时间为5~10秒
     */
    private Integer sessionTimeout;

    private ZkProperties() {
    }

    public static ZkProperties getInstance() {
        if (zkProperties == null) {
            synchronized (LOCK) {
                if (zkProperties == null) {
                    zkProperties = new ZkProperties();
                    Properties properties = new Properties();
                    InputStream inputStream = ZkProperties.class.getClassLoader().getResourceAsStream("zk.properties");
                    try {
                        properties.load(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    zkProperties.setAddress(properties.getProperty("zk.address", "127.0.0.1:2181"));
                    zkProperties.setSessionTimeout(Integer.valueOf(properties.getProperty("zk.sessionTimeout", "15000")));
                }
            }
        }
        return zkProperties;
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
