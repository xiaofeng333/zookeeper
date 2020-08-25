package com.feng.custom.zookeeper.curator;

import com.feng.custom.zookeeper.curator.leader.LeaderSelectorExample;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2020/8/24
 */
public class LeaderSelectorExampleTest extends BaseTest {

    /**
     * 只有一个节点为leader。
     * 打印错误日志属于正常现象。
     */
    @Test
    public void testLeaderSelectorExample() throws InterruptedException {
        List<CuratorFramework> clients = new ArrayList<>();
        List<LeaderSelectorExample> examples = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(testingServer.getConnectString(), new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(), zkProperties.getMaxRetries(), zkProperties.getMaxSleepMs()));
            clients.add(client);

            LeaderSelectorExample example = new LeaderSelectorExample(client, "/curator/leader", "Client #" + i);
            examples.add(example);

            client.start();
            example.start();
        }
        Thread.sleep(1000);
        boolean flag = false;
        boolean isLeader;
        for (LeaderSelectorExample example : examples) {
            isLeader = example.getLeaderSelector().hasLeadership();
            Assert.assertFalse(isLeader && flag);
            flag = isLeader;
            CloseableUtils.closeQuietly(example);
        }
        for (CuratorFramework client : clients) {
            CloseableUtils.closeQuietly(client);
        }
    }
}
