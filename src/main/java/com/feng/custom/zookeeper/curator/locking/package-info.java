/**
 * @date 2020/8/25
 * @see org.apache.curator.framework.recipes.locks.InterProcessLock
 * 顶层接口。
 * acquire(): 获取锁, 一直阻塞, 直到得到锁。
 * acquire(long time, TimeUnit unit): 阻塞直到获取锁或时间过期。
 * release(): 释放锁, 如果当前线程没有获得锁, 调用可能会抛出异常。
 * <p>
 * @see org.apache.curator.framework.recipes.locks.InterProcessMutex
 * 可重入锁, 公平锁, 所有的进程使用同一lock path。
 * 内部类LockData保存当前线程、lockPath及获取锁的次数。
 * <p>
 * 当通过acquire获取锁时, 已获取到锁时, 次数加1, 否则通过{@link org.apache.curator.framework.recipes.locks.LockInternals。attemptLock(long, TimeUnit, byte[])}获取。
 * 在该方法中, 会先在lockPath下创建临时有序节点, 然后判断是否成功获取锁, 即对应的序列号最小。
 * 其会监视其前一个序列号, 然后等待, 收到通知后唤醒或等待了指定时间后, 再次判断。
 * <p>
 * 通过release释放锁, 当前线程未获取到锁时, 抛出异常。已获取锁, lockData.lockCount次数减一。
 * 当lockCount为0时, 调用{@link org.apache.curator.framework.recipes.locks.LockInternals。releaseLock(String)}释放锁, 即删除对应node path。
 * <p>
 * @see org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex
 * 不可重入锁, 公平锁, 借助{@link org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2}实现锁的获取与释放, InterProcessSemaphoreV2内部使用了InterProcessMutex。
 * <p>
 * 获取锁的大致过程如下, 首先通过InterProcessMutex获取锁, 路径为{lock path}/locks, 获取到locks锁后,
 * 然后会在{lock path}/leases下同样建立临时有序节点, 此时只会判断当前leases下的子节点数是否小于maxLeases,
 * 小于则表示当前线程获取到了锁, 否则等待被唤醒或超时返回,
 * 得到是否获得锁的结果后, 会在finally块中调用InterProcessMutex#release(), 删除/locks下的节点, 下一个线程就可以继续在/leases下添加节点。
 * <p>
 * locks下为请求获得锁的线程
 * leases下为获得locks锁后的线程添加的节点, 当该节点子节点数小于maxLeases时, 获得锁。
 * <p>
 * 不像InterProcessMutex, 其可在其它线程中释放锁, 因其不可重入, 故连续调用acquire会导致死锁。
 * @see org.apache.curator.framework.recipes.locks.InterProcessMultiLock
 * 同时获得多个锁, 将多个锁作为一个实体。
 * 当调用acquire时, 会获取所有锁, 当失败时, 会释放已获取的锁。同时, release时, 会释放所有锁。
 * 同样, 内部使用了InterProcessMutex来实现。
 */
package com.feng.custom.zookeeper.curator.locking;
