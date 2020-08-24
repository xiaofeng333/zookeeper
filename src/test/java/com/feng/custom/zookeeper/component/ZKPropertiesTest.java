package com.feng.custom.zookeeper.component;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @date 2020/8/24
 */
public class ZKPropertiesTest {

    /**
     * 测试ZkProperties单例
     *
     * @throws InterruptedException 中断
     */
    @Test
    public void getInstanceTest() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final List<ZkProperties> instanceList = new ArrayList<ZkProperties>();
        Runnable runnable = new Runnable() {
            public void run() {
                instanceList.add(ZkProperties.getInstance());
                countDownLatch.countDown();
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(runnable);
        executorService.execute(runnable);
        countDownLatch.await();
        assert instanceList.get(0) == instanceList.get(1);
    }
}
