package com.github.aaric.zookeeper.strategy;

/**
 * 转存策略
 *
 * @author Aaric, created on 2018-03-26T17:57.
 * @since 0.0.1-SNAPSHOT
 */
public interface TransferStrategy {

    /**
     * 执行策略
     */
    void execute() throws Exception;
}
