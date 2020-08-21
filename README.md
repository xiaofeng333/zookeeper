# zookeeper
## 项目结构介绍
[component](src/main/java/com/feng/custom/zookeeper/component)主要提供zk服务器的信息。  
[raw](src/main/java/com/feng/custom/zookeeper/raw)包下为使用zk的原生api进行的调用操作。  
[curator](src/main/java/com/feng/custom/zookeeper/curator)包下为使用的是[curator](http://curator.apache.org/index.html)进行操作, 其是zk api的高级封装库。