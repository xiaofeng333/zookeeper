package com.feng.custom.zookeeper.raw.master;

/**
 * 模拟多个进程同时设置临时节点
 */
public class Master {

    public static void main(String[] args) {

        // 同步调用实例
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new SyncMasterRunnable());
            thread.start();
        }

        // 异步调用实例
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new AsyncMasterRunnable());
            thread.start();
        }
    }
}


