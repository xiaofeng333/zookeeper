package com.feng.custom.zookeeper.watcher;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * zk的所有读操作均可设置监视点, getData, getChildren, exists。
 * 一旦设置监视点, 想要移除时, 一是触发这个监视点, 二是使会话过期或关闭。
 * 设置监视点监听变化, 来进行对应的操作。
 */
public class ExistWatcher implements Watcher {
    private ZkProperties zkProperties;
    private ZooKeeper zooKeeper;

    public ExistWatcher() throws IOException {
        zkProperties = new ZkProperties();
        zooKeeper = new ZooKeeper(zkProperties.getAddress(), zkProperties.getSessionTimeout(), this);
    }

    public static void main(String[] args) throws Exception {
        ExistWatcher existWatcher = new ExistWatcher();
        existWatcher.zooKeeper.getData("/workers", true, new AsyncCallback.DataCallback() {

            public void processResult(int rc, String path, Object ctx, byte[] bytes, Stat stat) {
                System.out.println(rc);
            }
        }, null);
        Thread.sleep(1000000000);
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }
}
