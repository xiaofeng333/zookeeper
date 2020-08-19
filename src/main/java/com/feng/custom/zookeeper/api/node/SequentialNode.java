package com.feng.custom.zookeeper.api.node;

import com.feng.custom.zookeeper.api.Base;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class SequentialNode extends Base {
    private static final Logger logger = LoggerFactory.getLogger(SequentialNode.class);

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        SequentialNode sequentialNode = new SequentialNode();
        sequentialNode.initZookeeper();
        String actualPath = sequentialNode.registerSequentialNode();
        logger.info("actualPath: {}", actualPath);
        sequentialNode.closeZookeeper();
    }

    public String registerSequentialNode() throws KeeperException, InterruptedException {
        return getZk().create("/tasks/task-01-", "task-01".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }
}
