package com.feng.custom.zookeeper.raw.watcher;

import com.feng.custom.zookeeper.raw.Base;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * zk的所有读操作均可设置监视点, getData, getChildren, exists。
 * 一旦设置监视点, 想要移除时, 一是触发这个监视点, 二是使会话过期或关闭。
 * 设置监视点监听变化, 来进行对应的操作。
 */
public class ExistWatcher extends Base {
    private static final Logger logger = LoggerFactory.getLogger(ExistWatcher.class);

    public static void main(String[] args) throws Exception {
        ExistWatcher existWatcher = new ExistWatcher();
        existWatcher.initZookeeper();
        existWatcher.getZk().getData("/workers", true, new AsyncCallback.DataCallback() {

            public void processResult(int rc, String path, Object ctx, byte[] bytes, Stat stat) {
                logger.info("processResult, rc: {}", rc);
            }
        }, null);
        Thread.sleep(1000000000);
        existWatcher.closeZookeeper();
    }
}
