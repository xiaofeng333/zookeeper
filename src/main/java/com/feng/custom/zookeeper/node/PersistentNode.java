package com.feng.custom.zookeeper.node;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class PersistentNode implements Watcher {
    private ZooKeeper zk;
    private ZkProperties zkProperties = new ZkProperties();

    public static void main(String[] args) {
        PersistentNode persistentNode = new PersistentNode();
        try {
            persistentNode.initZookeeper();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        persistentNode.createParent("/workers");
        persistentNode.createParent("/assign");
        persistentNode.createParent("/tasks");
        persistentNode.createParent("/status");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initZookeeper() throws IOException {
        zk = new ZooKeeper(zkProperties.getAddress(), 15000, this);
    }

    private void createParent(String path) {
        String data = path.substring(1);
        zk.create(path, data.getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {

            public void processResult(int rc, String path, Object ctx, String name) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS: {
                        createParent(path);
                        break;
                    }
                    case OK: {
                        System.out.println(path + " created");
                        break;
                    }
                    default: {
                        System.out.println("create " + path + " error happen: " + rc);
                    }
                }
            }
        }, data);
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }
}
