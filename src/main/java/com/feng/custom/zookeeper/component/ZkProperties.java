package com.feng.custom.zookeeper.component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ZkProperties {
    private String address;

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
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
