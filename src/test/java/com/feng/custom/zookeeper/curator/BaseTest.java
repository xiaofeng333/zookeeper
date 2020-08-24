package com.feng.custom.zookeeper.curator;

import com.feng.custom.zookeeper.component.ZkProperties;
import org.apache.curator.test.TestingServer;
import org.junit.Before;

/**
 * @date 2020/8/24
 */
public class BaseTest {
    protected TestingServer testingServer;
    protected ZkProperties zkProperties;

    @Before
    public void init() throws Exception {
        testingServer = new TestingServer();
        zkProperties = ZkProperties.getInstance();
    }
}
