package com.feng.custom.zookeeper.raw.slave;

import com.feng.custom.zookeeper.raw.Base;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Worker extends Base {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private static final String serverId = Integer.toHexString(new Random().nextInt());
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        Worker worker = new Worker();
        worker.initZookeeper();
        worker.register();
        worker.countDownLatch.await();
        worker.setData("hello");
        Thread.sleep(30000);
        worker.closeZookeeper();
    }

    public void register() {
        getZk().create("/workers/worker-" + serverId, "Idle".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
            public void processResult(int rc, String path, Object ctx, String name) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS:
                        register();
                        break;
                    case OK:
                        logger.info("registered successfully: {}", serverId);
                        countDownLatch.countDown();
                        break;
                    case NODEEXISTS:
                        logger.error("Already registered: {}", serverId);
                        break;
                    default:
                        logger.error("something went wrong", KeeperException.create(KeeperException.Code.get(rc), path));

                }
            }
        }, null);
    }

    public void setData(String status) {
        getZk().setData("/workers/worker-" + serverId, status.getBytes(), -1, new AsyncCallback.StatCallback() {

            public void processResult(int rc, String path, Object ctx, Stat stat) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS:
                        setData((String) ctx);
                        break;
                    case OK:
                        logger.info("set data ok");
                        break;
                    default:
                        logger.error("something went wrong", KeeperException.create(KeeperException.Code.get(rc), path));
                }
            }
        }, status);
    }
}
