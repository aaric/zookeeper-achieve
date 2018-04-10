package com.github.aaric.zookeeper.strategy;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 时间片转存策略
 *
 * @author Aaric, created on 2018-04-04T11:48.
 * @since 0.3.0-SNAPSHOT
 */
public class TimesliceTransferStrategy implements TransferStrategy {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TimesliceTransferStrategy.class);

    /**
     * transfer服务器信息
     */
    public static long SERVER_SEQ = -1;
    public static boolean SERVER_ACTIVE = false;

    /**
     * 定义zk目录，gmmc替换项目名称<br>
     * --rooster<br>
     * ----transfer<br>
     * ------gmmc<br>
     * ----------next: 执行同步点<br>
     * ----------max: 最新同步点<br>
     * ----------node_list: 转存器目录<br>
     * ------------server-0000000001<br>
     * ------------server-0000000002<br>
     */
    private static final String ZK_PATH_T_WORK = "/rooster/transfer/gmmc";
    public static final String ZK_PATH_T_NEXT = ZK_PATH_T_WORK + "/next";
    public static final String ZK_PATH_T_MAX = ZK_PATH_T_WORK + "/max";
    private static final String ZK_PATH_T_NODE_LIST = ZK_PATH_T_WORK + "/node_list";
    private static final String ZK_PATH_T_SERVER = "server-";
    private static final String ZK_PATH_T_NODE_SERVER = ZK_PATH_T_NODE_LIST + "/" + ZK_PATH_T_SERVER;

    /**
     * zk客户端
     */
    private ZkClient zkClient;

    /**
     * 构造函数
     *
     * @param zkClient zk客户端
     */
    public TimesliceTransferStrategy(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void execute() throws Exception {
        // 1.创建转存器zk工作目录
        if (!zkClient.exists(ZK_PATH_T_NODE_LIST)) {
            zkClient.createPersistent(ZK_PATH_T_NODE_LIST, true);
        }

        // 2.初始化next节点数据(记录执行同步点)
        if (!zkClient.exists(ZK_PATH_T_NEXT)) {
            zkClient.create(ZK_PATH_T_NEXT, String.valueOf(0), CreateMode.PERSISTENT);
        }

        // 3.初始化max节点数据(记录最新同步点)
        if (!zkClient.exists(ZK_PATH_T_MAX)) {
            zkClient.create(ZK_PATH_T_MAX, String.valueOf(60 * 60 * 24), CreateMode.PERSISTENT);
        }

        // 4.获得服务器节点序号
        String zkSeverPath = zkClient.createEphemeralSequential(ZK_PATH_T_NODE_SERVER, null);
        SERVER_SEQ = Long.parseLong(zkSeverPath.replace(ZK_PATH_T_NODE_SERVER, ""));
        logger.info("Server SEQ: {}", SERVER_SEQ);

        // 5.比较最小节点，判断是否拿到锁
        List<String> childPaths = zkClient.getChildren(ZK_PATH_T_NODE_LIST);
        if (null != childPaths && 0 != childPaths.size()) {
            Collections.sort(childPaths);
            String minServerName = childPaths.get(0);
            long minSEQ = Long.parseLong(minServerName.replace(ZK_PATH_T_SERVER, ""));
            if (SERVER_SEQ == minSEQ) {
                SERVER_ACTIVE = true;
            } else {
                SERVER_ACTIVE = false;
            }
        }
        logger.info("Server Active: {}", SERVER_ACTIVE);

        // 6.监控node_list节点状态
        zkClient.subscribeChildChanges(ZK_PATH_T_NODE_LIST, (parentPath, currentChilds) -> {
            if (null != currentChilds && 0 != currentChilds.size()) {
                // 6.1 节点排序
                Collections.sort(currentChilds);

                // 6.2 如果本地SEQ最小，则激活状态"active"
                String minServerName = currentChilds.get(0);
                long minSEQ = Long.parseLong(minServerName.replace(ZK_PATH_T_SERVER, ""));
                if (SERVER_SEQ == minSEQ) {
                    SERVER_ACTIVE = true;
                } else {
                    SERVER_ACTIVE = false;
                }
                logger.info("Server Active: {}", SERVER_ACTIVE);
            }
        });

        // 7.定时调度
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.err.println("#############");
            // 7.1 查询max节点和next节点数据
            long max = Long.parseLong(zkClient.readData(ZK_PATH_T_MAX));
            long next = Long.parseLong(zkClient.readData(ZK_PATH_T_NEXT));
            logger.info("Max: {}, Next: {}", max, next);

            // 7.2 模拟数据转存流程
            if (SERVER_ACTIVE && next < max) {
                // 设置转存时间片数据
                long current = next;

                // 设置下一个转存时间片数据
                zkClient.writeData(ZK_PATH_T_NEXT, String.valueOf(++next));

                // 释放锁
                String zkEphemeralPath = MessageFormat.format(ZK_PATH_T_NODE_SERVER + "{0,number,0000000000}", SERVER_SEQ);
                zkClient.delete(zkEphemeralPath);

                // 获得服务器节点序号
                zkEphemeralPath = zkClient.createEphemeralSequential(ZK_PATH_T_NODE_SERVER, null);
                SERVER_SEQ = Long.parseLong(zkEphemeralPath.replace(ZK_PATH_T_NODE_SERVER, ""));
                logger.info("Server SEQ: {}", SERVER_SEQ);

                // 模拟转存数据
                logger.info("----------------->: {}", current);
                System.err.println("-------------");
            }

        }, 0, 1, TimeUnit.SECONDS);

    }
}
