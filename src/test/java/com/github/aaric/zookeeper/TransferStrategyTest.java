package com.github.aaric.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TransferStrategyTest.class);

    /**
     * 定义zk目录
     */
    private static final String ZK_PATH_ZD = "/rooster/transfer/zd";
    private static final String ZK_PATH_ZD_NEXT = ZK_PATH_ZD + "/next";
    private static final String ZK_PATH_ZD_NODE_LIST = ZK_PATH_ZD + "/node_list";
    private static final String ZK_PATH_NODE_SERVER = "server";
    private static final String ZK_PATH_FULL_NODE_SERVER = ZK_PATH_ZD_NODE_LIST + "/" + ZK_PATH_NODE_SERVER;

    @Autowired
    @Qualifier("zooKeeper")
    private ZooKeeper zkClient;

    private Integer serverSEQ = null;
    private Boolean serverActive = false;

    @Test
    public void testStrategy() throws Exception {
        // 1.创建转存器zk工作目录
        String[] zkWorkPaths = ZK_PATH_ZD_NODE_LIST.split("/");
        for (int i = 2; i <= zkWorkPaths.length; i++) {
            String zkWorkPath = StringUtils.join(Arrays.copyOf(zkWorkPaths, i), "/");
            if (null == zkClient.exists(zkWorkPath, false)) {
                zkClient.create(zkWorkPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }

        // 2.获得服务器节点序号
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String msg = "Last Operate Time: " + dateFormat.format(Calendar.getInstance().getTime());

        String zkServerPath = zkClient.create(ZK_PATH_FULL_NODE_SERVER, msg.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        serverSEQ = Integer.valueOf(zkServerPath.replace(ZK_PATH_FULL_NODE_SERVER, ""));
        logger.info("My Server SEQ: {}", serverSEQ);

        // 3.查询最小节点，如果是自己，则设置状态为"active"
        Integer zkServerSEQ = null;
        List<String> zkNodePaths = zkClient.getChildren(ZK_PATH_ZD_NODE_LIST, false);
        Collections.sort(zkNodePaths); //排序
        int minServerSEQ = Integer.parseInt(zkNodePaths.get(0).replace(ZK_PATH_NODE_SERVER, ""));
        logger.info("Min Server SEQ: {}", minServerSEQ);
        if (serverSEQ <= minServerSEQ) {
            // 如果本地SEQ最小，则激活状态"active"
            serverActive = true;
        }

        // x.打印结果
        System.err.println("-----result-----");
        System.err.println(serverSEQ);
        System.err.println(serverActive);
    }
}
