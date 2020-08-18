package com.feng.custom.zookeeper.expire;

import com.feng.custom.zookeeper.Base;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * @date 2020/8/18
 * zk会话expire时, 客户端不会主动尝试重新连接, 此时因会话过期会导致临时节点失效。
 * <p>
 * 当因主机过载或进程延迟影响了与zk交互的及时性导致会话超时, 临时节点被删除, 然后其它客户端申请成为主节点, 进行外部资源的更新。
 * 当该客户端负载下降时, 继续执行外部资源的更新, 并没有意识到其会话已过期且丢失了管理权, 这导致了系统状态的损坏。
 * 时钟偏移也会导致类似的问题, 因系统超载而导致时钟终结。有时候时钟偏移会导致时间变慢甚至落后, 使得客户端认为自己还安全地处于超时周期之内,
 * 因此其认为仍具有管理权, 但其会话已被zk置为过期。
 * <p>
 * 解决办法:
 * 1. 确保应用不会在超载或时钟偏移的环境中运行, 小心监控系统负载, 避免超载, 时钟同步程序可以保证系统时钟的同步。
 * 2. 使用一种名为隔离(fencing)的技巧, 只有持有最新符号的客户端, 才可以访问资源。如czxid, 表示创建节点时的zxid,
 * zxid为唯一的单调递增的序列号, 因此可以使用czxid作为一个隔离的符号, 当外部资源已经接收到更高版本的隔离符号的
 * 请求或连接时, 该请求或连接就会被拒绝。
 */
public class ConnectionExpire extends Base {
    public static void main(String[] args) throws IOException, InterruptedException {
        ConnectionExpire connectionExpire = new ConnectionExpire();
        connectionExpire.initZookeeper();
        ZooKeeper zk = connectionExpire.getZk();

        // 创建临时节点, 会话过期后, 其会被删除
        zk.create("/test", "test".getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
            public void processResult(int rc, String path, Object ctx, String name) {
                System.out.println(rc);
            }
        }, null);
        connectionExpire.setWatcher();
        Thread.sleep(100000);
        connectionExpire.closeZookeeper();
    }

    /**
     * 设置监视点
     */
    public void setWatcher() {
        try {
            getZk().getData("/multi", new Watcher() {
                public void process(WatchedEvent event) {

                    // 可在此处添加断点, 然后修改数据, 触发监视点, 模拟主机过载或因垃圾回收导致进程暂停。
                    // 因无法及时的与zk服务器发送心跳消息, 导致会话过期。
                    System.out.println("watch work multi");
                    System.out.println(event);
                    setWatcher();
                }
            }, new Stat());
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
