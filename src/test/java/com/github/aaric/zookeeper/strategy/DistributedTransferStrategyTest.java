package com.github.aaric.zookeeper.strategy;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * DistributedTransferStrategyTest
 *
 * @author Aaric, created on 2018-04-04T14:40.
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DistributedTransferStrategyTest {

    @Autowired
    private ZooKeeper zooKeeper;

    @Test
    public void testStrategy() throws Exception {
        TransferStrategy transferStrategy = new DistributedTransferStrategy(zooKeeper);
        transferStrategy.execute();
    }
}
