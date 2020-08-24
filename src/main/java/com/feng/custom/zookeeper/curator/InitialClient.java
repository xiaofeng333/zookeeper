package com.feng.custom.zookeeper.curator;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @date 2020/8/19
 * <p>
 * curator管理zk connection, 当遇到连接问题时, 其将重试操作。
 */
public class InitialClient {
    public static void main(String[] args) throws Exception {
        ZkProperties zkProperties = ZkProperties.getInstance();

        // 重试策略, 包含重试间隔和次数。
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(), zkProperties.getMaxRetries());
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkProperties.getAddress(), retryPolicy);

        // client必须调用start
        client.start();

        // 创建节点, 节点默认为PERSISTENT
        client.create().forPath("/curator", "curator".getBytes());

        // 关闭client
        client.close();
    }
}
