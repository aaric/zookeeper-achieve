package com.github.aaric.zookeeper.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ZooKeeper配置
 *
 * @author Aaric, created on 2018-03-26T13:55.
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
public class ZooKeeperConfiguration {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperConfiguration.class);

    /**
     * zk默认连接超时时间30秒
     */
    private static final int ZK_SESSION_TIMEOUT = 30000;

    /**
     * zk主机
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
    public ZooKeeper zooKeeper() {
        ZooKeeper zooKeeper = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            // 构建地址
            String[] zkHostStrings = zkQuorum.split(",");
            if (null == zkHostStrings || 0 == zkHostStrings.length) {
                throw new IllegalArgumentException("zk quorum can't be null");
            }
            for (int i = 0; i < zkHostStrings.length; i++) {
                zkHostStrings[i] += ":" + zklientPort;
            }

            // 初始化zk客户端
            zooKeeper = new ZooKeeper(StringUtils.join(zkHostStrings, ","), ZK_SESSION_TIMEOUT, (event) -> {
                if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await(ZK_SESSION_TIMEOUT, TimeUnit.MILLISECONDS);

            // 打印日志
            logger.info("Connection Zookeeper success...");

        } catch (IOException | InterruptedException e) {
            // 打印日志
            logger.info("Connection Zookeeper failure...", e);
        }

        // 关闭zk连接
        Runtime.getRuntime().addShutdownHook(new ZooKeeperShutdownThread(zooKeeper));

        return zooKeeper;
    }

    /**
     * 关闭zk线程类型
     */
    private static class ZooKeeperShutdownThread extends Thread {

        private ZooKeeper zooKeeper;

        public ZooKeeperShutdownThread(ZooKeeper zooKeeper) {
            this.zooKeeper = zooKeeper;
        }

        @Override
        public void run() {
            try {
                // 关闭zk连接
                zooKeeper.close();

                // 打印日志
                logger.info("Close Zookeeper success...");

            } catch (InterruptedException e) {
                // 打印日志
                logger.info("Close Zookeeper failure...", e);
            }
        }
    }
}
