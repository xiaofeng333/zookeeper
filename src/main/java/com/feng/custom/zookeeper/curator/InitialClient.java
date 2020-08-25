package com.feng.custom.zookeeper.curator;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @date 2020/8/19
 * <p>
 * curator管理zk connection, 当遇到连接问题时, 其将重试操作。
 * <p>
 * curator中状态的转换为connected->suspended<->reconnected<-lost;suspended->-lost。{@link org.apache.curator.framework.state.ConnectionState}
 * 对于服务节点的注册信息可在连接状态为connected和reconnected时, 进行检查和注册, 以免临时节点丢失。
 * <p>
 * {@link ExponentialBackoffRetry}: 指定了操作重试策略。
 */
public class InitialClient {
    private static final Logger logger = LoggerFactory.getLogger(InitialClient.class);

    public static void main(String[] args) throws Exception {
        ZkProperties zkProperties = ZkProperties.getInstance();

        // 重试策略, 包含初始重试间隔和次数。
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(), zkProperties.getMaxRetries(), zkProperties.getMaxSleepMs());
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkProperties.getAddress(), retryPolicy);

        // 添加状态变化listener
        client.getConnectionStateListenable().addListener((c, newState) -> logger.info("newState: {}", newState));

        // client必须调用start
        client.start();

        // 创建节点, 节点默认为PERSISTENT
        client.create().forPath("/curator", "curator".getBytes());

        // 关闭client
        client.close();
    }
}
