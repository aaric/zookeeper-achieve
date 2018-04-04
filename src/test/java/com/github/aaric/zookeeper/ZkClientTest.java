package com.github.aaric.zookeeper;

import com.github.aaric.zookeeper.listener.ZkCustomChildListener;
import com.github.aaric.zookeeper.model.UserInfo;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ZkClientTest
 *
 * @author Aaric, created on 2018-03-19T16:13.
 * @since 0.0.2-SNAPSHOT
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZkClientTest {

    private static final String ZK_PATH_TEST = "/rooster/test";

    @Autowired
    private ZkClient zkClient;

    @Test
    public void testConnection() {
        System.out.println(zkClient.getChildren("/"));
    }

    @Test
    public void testCreate() {
        zkClient.createPersistent(ZK_PATH_TEST, true);
        System.out.println(zkClient.getChildren(ZK_PATH_TEST.substring(0, ZK_PATH_TEST.lastIndexOf("/"))));
    }

    @Test
    public void testDelete() {
        //zkClient.delete(ZK_PATH_TEST);
        zkClient.deleteRecursive("/rooster");
    }

    @Test
    public void testSubscribe() throws InterruptedException {
        zkClient.subscribeChildChanges(ZK_PATH_TEST, new ZkCustomChildListener());
        for (int i = 1; i <= 5; i++) {
            zkClient.createPersistent(ZK_PATH_TEST + "/" + "node" + i);
            Thread.sleep(1000);
        }
    }

    @Test
    public void testReadData() {
        // path
        String path = ZK_PATH_TEST + "/person";

        // create
        zkClient.create(path, new UserInfo("zhangshan", "111111"), CreateMode.EPHEMERAL);

        // readData
        UserInfo userInfo = zkClient.readData(path, true);
        System.out.println(userInfo);

        // update
        zkClient.writeData(path, new UserInfo("lisi", "222222"));

        // readData
        userInfo = zkClient.readData(path, true);
        System.out.println(userInfo);
    }
}
