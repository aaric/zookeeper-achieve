package com.github.aaric.zookeeper.config;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * ZkClient配置
 *
 * @author Aaric, created on 2018-04-04T11:54.
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class ZkClientConfiguration {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ZkClientConfiguration.class);

    /**
     * zk默认连接超时时间30秒
     */
    private static final int ZK_SESSION_TIMEOUT = 30000;

    /**
     * zk主机地址
     */
    @Value("${rooster.zookeeper.quorum}")
    private String zkQuorum;

    /**
     * zk端口
     */
    @Value("${rooster.zookeeper.clientPort}")
    private String zklientPort;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ZkClient zkClient() {
        // 构建zk连接地址
        String[] zkHostStrings = zkQuorum.split(",");
        if (null == zkHostStrings || 0 == zkHostStrings.length) {
            throw new IllegalArgumentException("zk quorum can't be null");
        }
        for (int i = 0; i < zkHostStrings.length; i++) {
            zkHostStrings[i] += ":" + zklientPort;
        }

        // 初始化zk客户端
        ZkClient zkClient = new ZkClient(StringUtils.join(zkHostStrings, ","), ZK_SESSION_TIMEOUT);

        // 打印日志
        logger.info("Connection Zookeeper success...");

        // 程序关闭时主动断开zk连接
        Runtime.getRuntime().addShutdownHook(new ZkClientCloseThread(zkClient));

        return zkClient;
    }

    /**
     * 关闭zk连接线程类
     */
    private static class ZkClientCloseThread extends Thread {

        private ZkClient zkClient;

        public ZkClientCloseThread(ZkClient zkClient) {
            this.zkClient = zkClient;
        }

        @Override
        public void run() {
            // 关闭zk连接
            zkClient.close();

            // 打印日志
            logger.info("Close Zookeeper success...");
        }
    }
}
