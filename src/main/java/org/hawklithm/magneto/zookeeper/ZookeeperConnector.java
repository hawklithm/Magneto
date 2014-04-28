package org.hawklithm.magneto.zookeeper;

public interface ZookeeperConnector {
	public void supplyServiceToZookeeper(String zooAddress,String serviceAddress,String serviceName) throws Exception;

}
