package com.feng.custom.zookeeper.multi;

import com.feng.custom.zookeeper.Base;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * @date 2020/8/17
 * <p>
 * multiop原子性的执行多个zk操作, 另一个功能是检查一个znode节点的版本是否匹配{@link Transaction#check}
 * 使用Transaction来进行演示, 其封装了multi方法, 提供了简单的接口
 */
public class MultiOpNode extends Base {

    public static void main(String[] args) throws IOException, InterruptedException {
        MultiOpNode multiOpNode = new MultiOpNode();
        multiOpNode.initZookeeper();
        Transaction transaction = multiOpNode.getZk().transaction();
        transaction.create("/multi", "parent".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        // 如下znode无法创建, 故/multi也不会创建成功。
        // transaction.create("/wrong/child", "child".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        transaction.create("/multi/child", "child".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        transaction.commit(new AsyncCallback.MultiCallback() {

            /**
             *  rc表示第一个出错的multi操作的状态码, 如果为0, 则所有操作均成功。
             *  multi出错后, 后续的操作不会执行, 返回的err均为-2 {@link KeeperException.Code#RUNTIMEINCONSISTENCY}
             *  具体每个操作的结果保存在opResults中。
             */
            public void processResult(int rc, String path, Object ctx, List<OpResult> opResults) {
                System.out.println(rc);
                System.out.println(path);
                for (OpResult opResult : opResults) {
                    System.out.println(opResult);
                }
            }
        }, null);
        Thread.sleep(10000);
        multiOpNode.closeZookeeper();
    }
}
