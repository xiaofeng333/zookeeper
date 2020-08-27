package com.feng.custom.zookeeper.curator.cache;

import com.feng.custom.zookeeper.curator.CuratorBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.watch.PersistentWatcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * @date 2020/8/25
 * <p>
 * {@link CuratorCache}的使用, 监听事件并输出。
 * 保存指定node path下的数据, 响应update/create/delete事件, 拉取数据。
 * 注册listeners后, 当发生改变时, 将获得通知。
 * 当start后, 对指定node下已存在的节点会生成{@link CuratorCacheListener.Type#NODE_CREATED}事件。
 * 可以通过{@link CuratorCacheBuilder#withStorage(CuratorCacheStorage)} 指定存储, 默认为StandardCuratorCacheStorage#standard()。
 * 可以通过{@link CuratorCacheBuilder#withExceptionHandler(Consumer)} 添加异常处理, 默认情况下只是打印错误日志, 其不是必须的。
 * <p>
 * 用CuratorCache替换{@link PathChildrenCache}及{@link TreeCache}。
 * <p>
 * @see CuratorCacheStorage
 * 用于为CuratorCache保存数据。
 * {@link CuratorCacheStorage#standard()} 会保存对应节点的data数据。
 * {@link CuratorCacheStorage#dataNotCached()} 不会保存对应节点的data数据, 在{@link ChildData}中只包含path和{@link Stat}, data为null。
 * <p>
 * @see PersistentWatcher
 * 在CuratorCache内部的提供watch的作用, 当连接state为CONNECTED或RECONNECTED时, 会设置监视点。
 * <p>
 * @see CuratorCacheBridge
 * @see CuratorCacheListener
 *
 * <IMPORTANT> zk是最终一致系统, 当更新znode时, 需使用znode version, 可参见CuratorCache的文档。
 */
public class CuratorCacheExample extends CuratorBase {
    private static final Logger logger = LoggerFactory.getLogger(CuratorCacheExample.class);
    private static final String PATH = "/curator/cache";

    public static void main(String[] args) throws IOException {
        CuratorCacheExample curatorCacheExample = new CuratorCacheExample();
        CuratorFramework client = curatorCacheExample.initClient();
        client.start();

        CuratorCache cache = CuratorCache.build(client, PATH);

        // 可以监听单独的events, 亦或一次监听所有的events。
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forCreates(node -> logger.info("node created: {}", node))
                .forChanges(((oldNode, node) -> logger.info("node changed, old: {}, new: {}", oldNode, node)))
                .forDeletes(oldNode -> logger.info("node deleted, old: {}", oldNode))
                .forInitialized(() -> logger.info("cache initialized"))
                .build();

        // 注册listener
        cache.listenable().addListener(listener);

        // cache也是必须start的
        cache.start();

        logger.info("Press enter/return to quit.");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        curatorCacheExample.closeQuietly(cache);
        curatorCacheExample.closeQuietly(client);
    }

}
