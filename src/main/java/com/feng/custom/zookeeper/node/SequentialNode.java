package com.feng.custom.zookeeper.node;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class SequentialNode implements Watcher {
    private ZkProperties zkProperties;
    private ZooKeeper zk;

    public SequentialNode() throws IOException {
        zkProperties = new ZkProperties();
        zk = new ZooKeeper(zkProperties.getAddress(), zkProperties.getSessionTimeout(), this);

    }

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        SequentialNode sequentialNode = new SequentialNode();
        System.out.println(sequentialNode.registerSequentialNode());
    }

    public String registerSequentialNode() throws KeeperException, InterruptedException {
        return zk.create("/tasks/task-01-", "task-01".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }


    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }
}
