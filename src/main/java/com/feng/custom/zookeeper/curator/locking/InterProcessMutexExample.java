package com.feng.custom.zookeeper.curator.locking;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @date 2020/8/25
 */
public class InterProcessMutexExample {
    private static final Logger logger = LoggerFactory.getLogger(InterProcessMutexExample.class);

    private final InterProcessMutex interProcessMutex;
    private final String clientName;

    public InterProcessMutexExample(CuratorFramework client, String lockPath, String clientName) {
        this.interProcessMutex = new InterProcessMutex(client, lockPath);
        this.clientName = clientName;
    }

    public void acquire(long time, TimeUnit unit) throws Exception {
        if (!interProcessMutex.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the lock");
        }
        try {
            logger.info("{} has the lock", clientName);

            // 对资源进行对应操作
            Thread.sleep(3 * 1000);
        } finally {

            // 需在finally块中释放锁。
            interProcessMutex.release();
            logger.info("{} releasing the lock", clientName);
        }

    }
}
