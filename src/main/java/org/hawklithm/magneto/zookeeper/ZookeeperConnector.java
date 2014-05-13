package org.hawklithm.magneto.zookeeper;

import org.hawklithm.h2db.dataobject.RPCRegistInfoDO;

public interface ZookeeperConnector {
	public void supplyServiceToZookeeper(String zooAddress,RPCRegistInfoDO serviceInfo) throws Exception;
}
