package com.github.aaric.zookeeper.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 分布式转存策略
 *
 * @author Aaric, created on 2018-03-26T17:59.
 * @since 0.0.1-SNAPSHOT
 */
public class DistributedTransferStrategy implements TransferStrategy {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DistributedTransferStrategy.class);

    /**
     * transfer服务器信息
     */
    public static int SERVER_SEQ = -1;
    public static boolean SERVER_ACTIVE = false;

    /**
     * 定义zk目录
     */
    private static final String ZK_PATH_ZD = "/rooster/transfer/zd";
    public static final String ZK_PATH_ZD_NEXT = ZK_PATH_ZD + "/next";
    private static final String ZK_PATH_ZD_NODE_LIST = ZK_PATH_ZD + "/node_list";
    private static final String ZK_PATH_NODE_SERVER = "server";
    private static final String ZK_PATH_FULL_NODE_SERVER = ZK_PATH_ZD_NODE_LIST + "/" + ZK_PATH_NODE_SERVER;

    /**
     * zk客户端
     */
    private ZooKeeper zkClient;

    /**
     * 默认构造函数
     *
     * @param zkClient zk客户端
     * @throws Exception
     */
    public DistributedTransferStrategy(ZooKeeper zkClient) throws Exception {
        this.zkClient = zkClient;
    }

    @Override
    public void execute() throws Exception {
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
        SERVER_SEQ = Integer.parseInt(zkServerPath.replace(ZK_PATH_FULL_NODE_SERVER, ""));
        logger.info("My Server SEQ: {}", SERVER_SEQ);

        // 3.查询最小节点，如果是自己，则设置状态为"active"
        queryAndCheckActive();

        // 4.初始化next节点数据
        if (null == zkClient.exists(ZK_PATH_ZD_NEXT, false)) {
            String content = "0";
            zkClient.create(ZK_PATH_ZD_NEXT, content.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("Init zk Node('next') Data: {}", content);
        } else {
            String content = new String(zkClient.getData(ZK_PATH_ZD_NEXT, false, new Stat()));
            logger.info("Usage zk Node('next') Prev Data: {}", content);
        }

        // 5.监控node_list节点状态
        zkClient.getChildren(ZK_PATH_ZD_NODE_LIST, (event) -> {
            try {
                // 查询最小节点，如果是自己，则设置状态为"active"
                queryAndCheckActive();

                // 打印结果
                System.err.println("-----result-----");
                System.err.println(SERVER_SEQ);
                System.err.println(SERVER_ACTIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 6.打印结果
        System.err.println("-----result-----");
        System.err.println(SERVER_SEQ);
        System.err.println(SERVER_ACTIVE);
    }

    /**
     * 查询最小节点，如果是自己，则设置状态为"active"
     *
     * @throws Exception
     */
    public void queryAndCheckActive() throws Exception {
        List<String> zkNodePaths = zkClient.getChildren(ZK_PATH_ZD_NODE_LIST, false);
        Collections.sort(zkNodePaths); //排序
        int minServerSEQ = Integer.parseInt(zkNodePaths.get(0).replace(ZK_PATH_NODE_SERVER, ""));
        logger.info("Min Server SEQ: {}", minServerSEQ);
        if (SERVER_SEQ <= minServerSEQ) {
            // 如果本地SEQ最小，则激活状态"active"
            SERVER_ACTIVE = true;
        } else {
            SERVER_ACTIVE = false;
        }
    }
}
