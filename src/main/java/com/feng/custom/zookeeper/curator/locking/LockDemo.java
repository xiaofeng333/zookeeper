package com.feng.custom.zookeeper.curator.locking;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/8/25
 */
public class LockDemo {
    private static final Logger logger = LoggerFactory.getLogger(LockDemo.class);
    private static final int QTY = 5;
    private static final int REPETITIONS = QTY * 10;

    public static void main(String[] args) throws InterruptedException {
        ZkProperties zkProperties = ZkProperties.getInstance();

        ExecutorService service = Executors.newFixedThreadPool(QTY);
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProperties.getBaseSleepTimeMs(), zkProperties.getMaxRetries(), zkProperties.getMaxRetries());
        for (int i = 0; i < QTY; i++) {
            int index = i;
            Callable<Void> callable = () -> {
                CuratorFramework client = CuratorFrameworkFactory.newClient(zkProperties.getAddress(), retryPolicy);

                try {
                    client.start();
                    InterProcessMutexExample interProcessMutexExample = new InterProcessMutexExample(client, "/curator/lock/mutex", "client#" + index);
                    for (int j = 0; j < REPETITIONS; j++) {
                        interProcessMutexExample.acquire(1, TimeUnit.MINUTES);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    logger.error("error happen", e);
                } finally {
                    CloseableUtils.closeQuietly(client);
                }
                return null;
            };
            service.submit(callable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);
    }
}
