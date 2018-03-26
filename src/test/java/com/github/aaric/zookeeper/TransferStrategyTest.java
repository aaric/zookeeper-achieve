package com.github.aaric.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TransferStrategyTest
 *
 * @author Aaric, created on 2018-03-26T15:23.
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TransferStrategyTest {

    /**
     * 定义zk目录
     */
    private static final String ZK_PATH_ZD = "/rooster/transfer/zd";

    @Autowired
    @Qualifier("zooKeeper")
    private ZooKeeper zkClient;

    @Test
    public void testStrategy() throws Exception {
        System.err.println(zkClient);
        zkClient.exists(ZK_PATH_ZD, false);
    }
}
