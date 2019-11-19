package com.feng.custom.zookeeper.slave;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

public class Worker implements Watcher {
    private ZooKeeper zk;
    private ZkProperties zkProperties = new ZkProperties();
    private String serverId = Integer.toHexString(new Random().nextInt());

    // 创建的临时节点路径
    private String createdNodeName = "";

    // 创建成功
    private boolean createdSuccess = false;

    public Worker() throws IOException {

        // 初始化zk
        zk = new ZooKeeper(zkProperties.getAddress(), 15000, this);
    }

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker();
        worker.register();
        while (!worker.createdSuccess) {
        }
        worker.setData("hello");
        Thread.sleep(30000);
    }

    public void register() {
        zk.create("/workers/worker-" + serverId, "Idle".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
            public void processResult(int rc, String path, Object ctx, String name) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS:
                        register();
                        break;
                    case OK:
                        System.out.println("registered successfully: " + serverId);
                        createdNodeName = name;
                        createdSuccess = true;
                        break;
                    case NODEEXISTS:
                        System.err.println("Already registered: " + serverId);
                        break;
                    default:
                        System.err.println("something went wrong: " + KeeperException.create(KeeperException.Code.get(rc), path));

                }
            }
        }, null);
    }

    public void setData(String status) {
        zk.setData(createdNodeName, status.getBytes(), -1, new AsyncCallback.StatCallback() {

            public void processResult(int rc, String path, Object ctx, Stat stat) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS:
                        setData((String) ctx);
                        break;
                    case OK:
                        System.out.println("set data ok");
                        break;
                    default:
                        System.err.println("something went wrong: " + KeeperException.create(KeeperException.Code.get(rc), path));
                }
            }
        }, status);
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }
}
