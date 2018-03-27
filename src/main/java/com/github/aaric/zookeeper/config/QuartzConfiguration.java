package com.github.aaric.zookeeper.config;

import com.github.aaric.zookeeper.App;
import com.github.aaric.zookeeper.strategy.DistributedTransferStrategy;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Quartz配置
 *
 * @author Aaric, created on 2018-03-27T13:39.
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@EnableScheduling
public class QuartzConfiguration {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * zk客户端
     */
    @Autowired
    @Qualifier("zooKeeper")
    private ZooKeeper zkClient;

    /**
     * 定时任务
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void doTask() {
        try {
            if (DistributedTransferStrategy.SERVER_ACTIVE) {
                // 获取zk初始化数据
                byte[] data = zkClient.getData(DistributedTransferStrategy.ZK_PATH_ZD_NEXT, false, new Stat());
                if (null != data) {
                    // 每次从zk获取next节点数据
                    int current = Integer.parseInt(new String(data));
                    // 模拟数据动作
                    logger.info("Server SEQ-{}: {}, active: {}", DistributedTransferStrategy.SERVER_SEQ, ++current, DistributedTransferStrategy.SERVER_ACTIVE);
                    // 设置next节点数据
                    zkClient.setData(DistributedTransferStrategy.ZK_PATH_ZD_NEXT, String.valueOf(current).getBytes(), -1);
                }
            } else {
                // 汇报一次自己的Active状态
                logger.info("Server SEQ-{}: {}", DistributedTransferStrategy.SERVER_SEQ, DistributedTransferStrategy.SERVER_ACTIVE);
            }

        } catch (Exception e) {
            logger.error("doTask Error", e);
        }
    }
}
