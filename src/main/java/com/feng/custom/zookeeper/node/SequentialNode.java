package com.feng.custom.zookeeper.node;

import com.feng.custom.zookeeper.Base;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class SequentialNode extends Base {
    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        SequentialNode sequentialNode = new SequentialNode();
        sequentialNode.initZookeeper();
        System.out.println(sequentialNode.registerSequentialNode());
        sequentialNode.closeZookeeper();
    }

    public String registerSequentialNode() throws KeeperException, InterruptedException {
        return zk.create("/tasks/task-01-", "task-01".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }
}
