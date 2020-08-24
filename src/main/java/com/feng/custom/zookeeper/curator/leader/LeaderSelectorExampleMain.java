package com.feng.custom.zookeeper.curator.leader;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2020/8/24
 */
public class LeaderSelectorExampleMain {
    private static final Logger logger = LoggerFactory.getLogger(LeaderSelectorExampleMain.class);
    private static final int CLIENT_QTY = 10;
    private static final String PATH = "/curator/leader";

    public static void main(String[] args) throws IOException {
        List<CuratorFramework> clients = new ArrayList<>();
        List<LeaderSelectorExample> examples = new ArrayList<>();
        try {
            ZkProperties zkProperties = ZkProperties.getInstance();
            for (int i = 0; i < CLIENT_QTY; i++) {
                CuratorFramework client = CuratorFrameworkFactory.newClient(zkProperties.getAddress(), new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(), zkProperties.getMaxRetries()));
                clients.add(client);

                LeaderSelectorExample example = new LeaderSelectorExample(client, PATH, "Client #" + i);
                examples.add(example);

                client.start();
                example.start();
            }
            logger.info("Press enter/return to quit.");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            for (LeaderSelectorExample example : examples) {
                CloseableUtils.closeQuietly(example);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
