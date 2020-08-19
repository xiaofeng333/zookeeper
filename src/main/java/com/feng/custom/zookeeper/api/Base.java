package com.feng.custom.zookeeper.api;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @date 2020/8/13
 */
public class Base implements Watcher {
    private static final Logger logger = LoggerFactory.getLogger(Base.class);
    private ZooKeeper zk;

    /**
     * 初始化zk连接
     */
    protected void initZookeeper() throws IOException {
        ZkProperties zkProperties = ZkProperties.getInstance();
        zk = new ZooKeeper(zkProperties.getAddress(), zkProperties.getSessionTimeout(), this);
    }

    /**
     * 主动关闭zk连接, 立即结束会话, 避免zk服务器到会话超时时间后才意识到会话过期
     */
    protected void closeZookeeper() throws InterruptedException {
        zk.close();
    }

    /**
     * 接受会话事件
     * 监控与zk之间会话的健康情况
     * 监控zk数据的变化
     */
    public void process(WatchedEvent watchedEvent) {
        logger.info("process, watchedEvent: {}", watchedEvent);
    }

    protected ZooKeeper getZk() {
        return zk;
    }

    protected void setZk(ZooKeeper zk) {
        this.zk = zk;
    }
}
