package com.feng.custom.zookeeper.curator;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.Closeable;

/**
 * @date 2020/8/25
 */
public class CuratorBase {
    protected CuratorFramework client;
    protected ZkProperties zkProperties;

    /**
     * @return client未启动。不再使用时, 调用{@link #closeQuietly(Closeable)}关闭
     */
    protected CuratorFramework initClient() {
        zkProperties = ZkProperties.getInstance();
        client = CuratorFrameworkFactory.newClient(zkProperties.getAddress(), new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(), zkProperties.getMaxRetries(), zkProperties.getMaxSleepMs()));
        return client;
    }

    protected void closeQuietly(Closeable closeable) {
        CloseableUtils.closeQuietly(closeable);
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }
}
