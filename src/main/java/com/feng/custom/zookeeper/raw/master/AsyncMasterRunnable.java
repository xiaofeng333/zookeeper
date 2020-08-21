package com.feng.custom.zookeeper.raw.master;

import com.feng.custom.zookeeper.raw.Base;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * 异步调用实例
 * 模拟多个进程同时设置临时节点, 异步调用
 * 为了保持顺序, 只会有一个单独的线程按照接受顺序处理响应包
 * 所以如果回调函数阻塞, 所有后续回调都会被阻塞, 故应避免这么做, 以便回调调用可以快速被处理
 */
public class AsyncMasterRunnable extends Base implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncMasterRunnable.class);
    private static final String nodePathAsync = "/masterAsync";
    private final String serverId = Integer.toHexString(new Random().nextInt());

    public void run() {
        try {
            initZookeeper();
        } catch (IOException e) {

            // 线程模拟多节点，此处只是打印，应抛出，交由调用者处理
            e.printStackTrace();
            return;
        }

        // 异步调用, create方法不会抛出异常, 调用返回前不会等待create命令完成, 通常在create请求发送到服务端之前就会立即返回, 新增参数为提供回调方法的对象和用户指定的上下文信息
        runForMaster();
        try {

            // 防止因线程结束导致会话关闭, 临时节点被删除
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            closeZookeeper();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建临时节点
     */
    private void runForMaster() {
        logger.info("async run, serverId: {}", serverId);
        getZk().create(nodePathAsync, serverId.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {

            /**
             * 异步调用创建的回调函数
             *
             * @param rc   调用的结果
             * @param path create方法的path值
             * @param ctx  create传递的上下文参数
             * @param name 创建的znode节点名称
             */
            public void processResult(int rc, String path, Object ctx, String name) {
                switch (KeeperException.Code.get(rc)) {
                    case OK: {
                        logger.info("{} is Master async", serverId);
                        return;
                    }
                    case CONNECTIONLOSS: {
                        checkMaster();
                        return;
                    }
                    default: {
                        logger.info("{} is not Master async", serverId);
                    }

                }
            }
        }, null);
    }

    /**
     * 检查临时节点的数据
     */
    private void checkMaster() {
        getZk().getData(nodePathAsync, false, new AsyncCallback.DataCallback() {
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                switch (KeeperException.Code.get(rc)) {
                    case NONODE:
                        runForMaster();
                        return;
                    case CONNECTIONLOSS:
                        checkMaster();
                        return;
                    case NODEEXISTS:
                        if (serverId.equals(new String(data))) {
                            logger.info("{} is Master async", serverId);
                        } else {
                            logger.info("{} is not Master async", serverId);
                        }
                }
            }
        }, null);
    }
}


