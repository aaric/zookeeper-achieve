package com.github.aaric.zookeeper.listener;

import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Zookeeper Custom child path Listener.
 *
 * @author Aaric, created on 2018-03-19T16:41.
 * @since 0.0.2-SNAPSHOT
 */
public class ZkCustomChildListener implements IZkChildListener {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ZkCustomChildListener.class);

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        logger.info("parentPath: {}, currentChilds: {}", parentPath, currentChilds);
    }
}
