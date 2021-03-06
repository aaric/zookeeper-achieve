package com.github.aaric.zookeeper;

import com.github.aaric.zookeeper.strategy.TimesliceTransferStrategy;
import com.github.aaric.zookeeper.strategy.TransferStrategy;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Launcher.
 *
 * @author Aaric, created on 2018-03-15T10:12.
 * @since 0.0.2-SNAPSHOT
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * zk客户端
     */
    @Autowired
    /*private ZooKeeper zooKeeper;*/
    private ZkClient zkClient;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动日志
        logger.info("App start...");

        // 执行分布式策略
        /*TransferStrategy transferStrategy = new DistributedTransferStrategy(zooKeeper);
        transferStrategy.execute();*/

        // 执行时间片策略
        TransferStrategy transferStrategy = new TimesliceTransferStrategy(zkClient);
        transferStrategy.execute();

        // 关闭日志
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("App stopped...");
        }));
    }
}
