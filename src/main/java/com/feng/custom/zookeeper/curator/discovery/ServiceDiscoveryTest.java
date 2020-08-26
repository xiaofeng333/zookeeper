package com.feng.custom.zookeeper.curator.discovery;

import com.feng.custom.zookeeper.curator.CuratorBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @date 2020/8/27
 */
public class ServiceDiscoveryTest extends CuratorBase {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryTest.class);

    public static void main(String[] args) throws Exception {
        ServiceDiscoveryTest serviceDiscoveryTest = new ServiceDiscoveryTest();
        CuratorFramework client = serviceDiscoveryTest.initClient();
        client.start();

        ServiceInstance<InstanceDetails> serviceInstance = ServiceInstance.<InstanceDetails>builder()
                .id("unique")
                .name("first")
                .payload(new InstanceDetails("first details"))
                .port(123)
                .build();

        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<>(InstanceDetails.class);
        ServiceDiscovery<InstanceDetails> serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(client)
                .basePath("/curator/discovery")
                .serializer(serializer)
                .thisInstance(serviceInstance)
                .build();
        serviceDiscovery.start();

        ServiceProvider<InstanceDetails> first = serviceDiscovery.serviceProviderBuilder().serviceName("first").build();
        first.start();
        logger.info("first: {}", first.getInstance());

        // 发现其它注册在/curator/discovery下的服务
        ServiceProvider<InstanceDetails> second = serviceDiscovery.serviceProviderBuilder().serviceName("second").build();
        second.start();
        logger.info("second: {}", second.getInstance());

        logger.info("Press enter/return to quit.");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        serviceDiscovery.close();
        first.close();
        second.close();
    }
}
