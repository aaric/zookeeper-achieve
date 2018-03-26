package com.github.aaric.zookeeper.strategy;

import org.apache.zookeeper.ZooKeeper;

/**
 * 转存策略
 *
 * @author Aaric, created on 2018-03-26T17:57.
 * @since 0.0.1-SNAPSHOT
 */
public interface TransferStrategy {

    /**
     * 执行策略
     *
     * @param zkClient zk客户端
     */
    void execute(ZooKeeper zkClient) throws Exception;
}
