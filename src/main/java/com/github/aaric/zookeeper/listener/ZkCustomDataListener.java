package com.github.aaric.zookeeper.listener;

import org.I0Itec.zkclient.IZkDataListener;

/**
 * Zookeeper Custom child path Listener.
 *
 * @author Aaric, created on 2018-03-19T17:21.
 * @since 0.0.2-SNAPSHOT
 */
public class ZkCustomDataListener implements IZkDataListener {

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {

    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {

    }
}
