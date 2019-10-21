package com.feng.custom.zookeeper.master;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 同步调用实例
 * 主要关注点为发生异常时，对应的处理机制, 此处使用OPEN_ACL_UNSAFE。
 * InterruptedException不进行处理，传递给调用者。
 * 发生KeeperException时，开发者需知道此时的状态，所以需要执行getData进行判断, NoNodeException表示该节点仍不存在，继续尝试创建。
 */
public class SyncMasterRunnable implements Runnable, Watcher {
    private ZkProperties zkProperties = new ZkProperties();
    private String nodePathSync = "/masterSync";

    public void run() {
        ZooKeeper zk;
        try {
            zk = new ZooKeeper(zkProperties.getAddress(), 15000, this);
        } catch (IOException e) {

            // 线程模拟多节点，此处只是打印，应抛出，交由调用者处理
            e.printStackTrace();
            return;
        }
        String serverId = Integer.toHexString(new Random().nextInt());
        System.out.println("sync run, serverId: " + serverId);

        // 对应znode节点的元数据
        Stat stat = new Stat();
        boolean isMaster = false;
        while (true) {
            try {
                zk.create(nodePathSync, serverId.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isMaster = true;
                break;
            } catch (KeeperException e) {

                // 是否节点已设置成功, 不再尝试创建该节点
                boolean isGetData = false;
                while (true) {
                    try {

                        // 不监视变更，只是获取当前数据
                        byte[] data = zk.getData(nodePathSync, false, stat);
                        isMaster = new String(data).equals(serverId);
                        isGetData = true;
                    } catch (KeeperException.NoNodeException ex) {

                        // 该节点不存在， 继续尝试创建节点
                        break;
                    } catch (InterruptedException ex) {

                        // 线程模拟多节点，此处只是打印，应抛出，交由调用者处理
                        e.printStackTrace();
                        break;
                    } catch (KeeperException ex) {

                        // 其他异常，不进行处理，继续尝试获取节点数据
                        ex.printStackTrace();
                    }
                }

                if (isGetData) {

                    // 已成功创建节点
                    break;
                }
            } catch (InterruptedException e) {

                // 线程模拟多节点，此处只是打印，应抛出，交由调用者处理
                e.printStackTrace();
                break;
            }
        }
        if (isMaster) {
            System.out.println(serverId + " is master sync");
        } else {
            System.out.println(serverId + " is not master sync");
        }
        try {

            // 防止因线程结束导致会话关闭, 临时节点被删除
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }
}