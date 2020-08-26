/**
 * 参见 <a href="http://curator.apache.org/curator-x-discovery/index.html">curator service discovery</a>
 * <p>
 * {@link org.apache.curator.x.discovery.ServiceInstance}
 * 代表service实例, 通过{@link org.apache.curator.x.discovery.ServiceInstanceBuilder}构建。
 * 其id和name不能为空, 且id必须是唯一的。
 * payload是用户自定义的类型。
 * serviceType指定了创建的节点类型。
 * </p>
 * <p>
 * {@link org.apache.curator.x.discovery.ServiceDiscovery}
 * 与zk交互, 注册和查询service instances, 服务注册路径为{basePath/ServiceInstance.name/ServiceInstance.id}, 节点类型通过ServiceInstance.serviceType确定。
 * 使用{@link org.apache.curator.x.discovery.ServiceDiscoveryBuilder}创建ServiceDiscovery, 默认使用{@link org.apache.curator.x.discovery.details.JsonInstanceSerializer}来序列化。
 * ServiceDiscovery必须start, 当不再使用后调用close。
 * start后会添加connectionStateListener, 当连接状态发生改变(RECONNECTED或CONNECTED)时, 注册服务。close时会移除该listener。
 * <b>IMPORTANT</b>但需注意, 使用JsonInstanceSerializer, 当反序列化时, 发现不存在的属性时, 其会失败报错。
 * </p>
 * <p>
 * {@link org.apache.curator.x.discovery.ServiceCache}
 * 缓存实例列表, 使用Wathcer来更新缓存。
 * 通过{@link org.apache.curator.x.discovery.ServiceCacheBuilder}创建, service name必须设置, ServiceCacheBuilder实例通过ServiceDiscovery获得。
 * ServiceCache必须start, 同时结束后调用close。
 * 其可添加{@link org.apache.curator.x.discovery.details.ServiceCacheListener}, 当cache成功更新instances后被通知。
 * </p>
 * <p>
 * {@link org.apache.curator.x.discovery.ServiceProvider}
 * 通过{@link org.apache.curator.x.discovery.ServiceProviderBuilder}创建, 通过其设置service name和其它可选的值。ServiceProviderBuilder实例通过ServiceDiscovery获得。
 * ServiceProvider必须start, 当完成后调用close。
 * <p>
 * 在ServiceProvider中, InstanceProvider提供了该ServiceName的实例, ProviderStrategy指定了获取策略。
 * ServiceCache是InstanceProvider的子接口, 通过ServiceCache获取该ServiceName的所有实例, 再通过{org.apache.curator.x.discovery.details.FilteredInstanceProvider}根据ServiceInstance#enabled和DownInstancePolicy过滤instances。
 * 之后通过ProviderStrategy指定的策略获取一个ServiceInstance返回。
 * 当与某个ServiceInstance交互失败时, 可通过ServiceProvider.noteError(ServiceInstance)标记错误, 当标记错误超过DownInstancePolicy#errorThreshold次数时, 该ServiceInstance会down掉1.5倍的DownInstancePolicy#timeoutMs时间, 参见{org.apache.curator.x.discovery.details.DownInstanceManager}。
 *
 * <IMPORTANT>ServiceProvider对象应重复使用, 因为{org.apache.curator.framework.imps.NamespaceWatcher}不能在zk3.4.x中移除, 故对相同的service, 每次创建新的ServiceProvider最终将耗尽jvm内存。
 * <IMPORTANT>ServiceProvider#getInstance()和ServiceProvider#getAllInstances()获得的service instance不应保存, 每次获得一个新的实例。
 * </p>
 * <p>
 * ServiceProvider和ServiceCache基于ServiceDiscovery的basePath发现服务的。
 *
 * @see org.apache.curator.x.discovery.ServiceType
 * @see org.apache.curator.x.discovery.details.InstanceProvider
 * @see org.apache.curator.x.discovery.ProviderStrategy
 * @see org.apache.curator.x.discovery.DownInstancePolicy
 */
package com.feng.custom.zookeeper.curator.discovery;