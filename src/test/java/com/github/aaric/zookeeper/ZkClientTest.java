package com.github.aaric.zookeeper;

import com.github.aaric.zookeeper.listener.ZkCustomChildListener;
import com.github.aaric.zookeeper.model.UserInfo;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ZkClientTest
 *
 * @author Aaric, created on 2018-03-19T16:13.
 * @since 0.0.2-SNAPSHOT
 */
//@SpringBootTest
//@RunWith(SpringRunner.class)
public class ZkClientTest {

    private static final String ZK_TEST_PATH = "/rooster/test";

    @Autowired
    private ZkClient zkClient;

    @Before
    public void begin() {
        zkClient = new ZkClient("192.168.56.101:2181", 5000);
    }

    @Test
    public void testConnection() {
        System.out.println(zkClient.getChildren("/"));
    }

    @Test
    public void testCreate() {
        zkClient.createPersistent(ZK_TEST_PATH, true);
        System.out.println(zkClient.getChildren(ZK_TEST_PATH.substring(0, ZK_TEST_PATH.lastIndexOf("/"))));
    }

    @Test
    public void testDelete() {
        //zkClient.delete(ZK_TEST_PATH);
        zkClient.deleteRecursive("/rooster");
    }

    @Test
    public void testSubscribe() throws InterruptedException {
        zkClient.subscribeChildChanges(ZK_TEST_PATH, new ZkCustomChildListener());
        for (int i = 1; i <= 5; i++) {
            zkClient.createPersistent(ZK_TEST_PATH + "/" + "node" + i);
            Thread.sleep(1000);
        }
    }

    @Test
    public void testGetOrUpdate() {
        // http://blog.csdn.net/u012562943/article/details/52963506
        UserInfo userInfo = zkClient.readData(ZK_TEST_PATH + "/person", true);
        System.out.println(userInfo);
    }
}