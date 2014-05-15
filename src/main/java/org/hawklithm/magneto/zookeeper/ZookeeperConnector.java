package org.hawklithm.magneto.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.hawklithm.h2db.dataobject.RPCRegistInfoDO;

public interface ZookeeperConnector {
	public ZooKeeper supplyServiceToZookeeper(RPCRegistInfoDO serviceInfo) throws Exception;
	public RPCRegistInfoDO getService(String servicePath) throws KeeperException, InterruptedException;
}
