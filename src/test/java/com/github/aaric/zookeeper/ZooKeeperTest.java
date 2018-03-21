package com.github.aaric.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ZooKeeperTest
 *
 * @author Aaric, created on 2018-03-21T21:12.
 * @since 0.0.1-SNAPSHOT
 */
public class ZooKeeperTest {

    private static final int ZK_SESSION_TIMEOUT = 30000;

    private static final String ZK_TEST_PATH = "/rooster/test";

    private ZooKeeper connectZooKeeper() {
        ZooKeeper zooKeeper = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zooKeeper = new ZooKeeper("192.168.56.101:2181", ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState()) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await(ZK_SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }

    @Test
    public void testConnect() {
        ZooKeeper zk = connectZooKeeper();
        System.out.println(zk);
    }

    @Test
    public void testGetChildren() throws KeeperException, InterruptedException {
        ZooKeeper zk = connectZooKeeper();
        List<String> paths = zk.getChildren(ZK_TEST_PATH, true);
        System.out.println(paths);
    }
}
