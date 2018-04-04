package com.github.aaric.zookeeper.strategy;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间片转存策略
 *
 * @author Aaric, created on 2018-04-04T11:48.
 * @since 0.3.0-SNAPSHOT
 */
public class TimesliceTransferStrategy implements TransferStrategy {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TimesliceTransferStrategy.class);

    /**
     * zk客户端
     */
    private ZkClient zkClient;

    /**
     * 构造函数
     *
     * @param zkClient zk客户端
     */
    public TimesliceTransferStrategy(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void execute() throws Exception {
        System.out.println("TODO");
    }
}
