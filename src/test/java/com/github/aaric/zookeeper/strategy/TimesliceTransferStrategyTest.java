package com.github.aaric.zookeeper.strategy;

import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TimesliceTransferStrategyTest
 *
 * @author Aaric, created on 2018-04-04T14:43.
 * @since 0.0.1-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TimesliceTransferStrategyTest {

    @Autowired
    private ZkClient zkClient;

    @Test
    public void testStrategy() throws Exception {
        TransferStrategy transferStrategy = new TimesliceTransferStrategy(zkClient);
        transferStrategy.execute();
    }
}
