package com.github.aaric.zookeeper.config;

import com.github.aaric.zookeeper.App;
import com.github.aaric.zookeeper.strategy.DistributedTransferStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;

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
     * 定时任务
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void doTask() {
        logger.info("Server SEQ-{}: {}, active: {}", DistributedTransferStrategy.SERVER_SEQ, Calendar.getInstance().getTimeInMillis(), DistributedTransferStrategy.SERVER_ACTIVE);
    }
}
