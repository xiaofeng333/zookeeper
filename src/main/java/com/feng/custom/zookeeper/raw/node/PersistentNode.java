package com.feng.custom.zookeeper.raw.node;

import com.feng.custom.zookeeper.raw.Base;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class PersistentNode extends Base {
    private static final Logger logger = LoggerFactory.getLogger(PersistentNode.class);

    public static void main(String[] args) {
        PersistentNode persistentNode = new PersistentNode();
        try {
            persistentNode.initZookeeper();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        persistentNode.createParent("/workers");
        persistentNode.createParent("/assign");
        persistentNode.createParent("/tasks");
        persistentNode.createParent("/status");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            persistentNode.closeZookeeper();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createParent(String path) {
        String data = path.substring(1);
        getZk().create(path, data.getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {

            public void processResult(int rc, String path, Object ctx, String name) {
                switch (KeeperException.Code.get(rc)) {
                    case CONNECTIONLOSS: {
                        createParent(path);
                        break;
                    }
                    case OK: {
                        logger.info("{} created", path);
                        break;
                    }
                    default: {
                        logger.error("create {} happen error, rc: {}", path, rc);
                    }
                }
            }
        }, data);
    }
}
