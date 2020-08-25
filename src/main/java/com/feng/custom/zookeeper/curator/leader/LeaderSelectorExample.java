package com.feng.custom.zookeeper.curator.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @date 2020/8/24
 * {@link LeaderSelector}的使用
 * {@link LeaderSelectorListenerAdapter}推荐用于connection state处理。
 * 当该client获取到leader时, 会休眠指定时间后, 放弃leader。
 */
public class LeaderSelectorExample extends LeaderSelectorListenerAdapter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(LeaderSelectorExample.class);

    private final String name;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();

    /**
     * @param client client
     * @param path   leader路径
     * @param name   名称
     */
    public LeaderSelectorExample(CuratorFramework client, String path, String name) {
        this.name = name;

        // 使用指定的path创建leaderSelector
        // 所有的参与竞选的应使用同一路径
        // 该类同时是LeaderSelectorListener
        this.leaderSelector = new LeaderSelector(client, path, this);


        // LeaderSelectorListener#takeLeadership(CuratorFramework)方法返回后, leaderSelector不会再次排队获取领导权。
        // 但是大部分情况下, 当丢失领导权后, 会希望重新排队, 调用如下方法即可。
        leaderSelector.autoRequeue();
    }

    /**
     * 启动leaderSelector
     */
    public void start() {
        leaderSelector.start();
    }

    /**
     * 关闭leaderSelector
     */
    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    /**
     * 当该client成为leader时, 调用的方法。
     * 该方法不应该返回, 除非希望释放领导权。
     * 实际业务中, 为该类中增加stop方法, 在合适的时候调用, 自动放弃leader。
     * <p>
     * 注意判断线程是否中断, 当中断后, 即失去领导权, 当及时清理后退出该方法。
     *
     * @param client the client
     * @throws Exception any errors
     */
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {

        // 该方法中可根据业务进行预判断, 避免冲突。
        final int waitSeconds = (int) (5 * Math.random()) + 1;
        logger.info("{} is leader now, will hold {} seconds", name, waitSeconds);
        logger.info("{} has been leader {} time(s) before.", name, leaderCount.getAndIncrement());
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        } catch (InterruptedException e) {
            logger.error("{} was interrupted.", name);
            Thread.currentThread().interrupt();
        } finally {

            // 释放前可做清理操作
            logger.info("{} relinquishing leadership.", name);
        }
    }

    public LeaderSelector getLeaderSelector() {
        return leaderSelector;
    }
}
