package org.hawklithm.magneto.zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.hawklithm.h2db.dataobject.RPCRegistInfoDO;
import org.hawklithm.magneto.utils.Jsoner;

public class ZookeeperConnectorImpl implements ZookeeperConnector{
	
	public static String rpcRootGroupNode="rpcgroup";
	/**
	 * 连接zookeeper
	 * @param zooAddress  zookeeper的服务器地址
	 * @param serviceAddress 提供服务者的地址
	 * @param serviceName	服务名，用服务的接口全名做服务名
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws Exception
	 */
	@Override
	public void supplyServiceToZookeeper(String zooAddress,RPCRegistInfoDO serviceInfo) throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper(zooAddress/*"localhost:2181"*/, 5000, new Watcher() {
			public void process(WatchedEvent event) {
				// do nothing
			}
		});
		String path=createNodeRecursively(zk, (rpcRootGroupNode+"."+serviceInfo.getInterfaceName()).split("."));
		String createdPath=createNodeEphemeral(zk, path, serviceInfo.getVersion(), Jsoner.toJson(serviceInfo).getBytes("utf-8"));
		System.out.println("create: " + createdPath);
	}
	
	/**
	 * create ehpemeral node and it will be deleted when connection closed
	 * @param zk
	 * @param address
	 * @param nodeName
	 * @param data
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private String createNodeEphemeral(ZooKeeper zk,String address,String nodeName,byte[] data) throws KeeperException, InterruptedException{
			return zk.create(address + "/" + nodeName, data, Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
	}
	
	/**
	 * create node path recursively according to the service name 
	 * @param zk
	 * @param nodes
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private String createNodeRecursively(ZooKeeper zk, String[] nodes)
			throws UnsupportedEncodingException, KeeperException,
			InterruptedException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < nodes.length; i++) {
			zk.create(builder.append("/").append(nodes[i]).toString(),
					"".getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
		}
		return builder.toString();
	}

	
	
	/**
	 * server的工作逻辑写在这个方法中
	 * 此处不做任何处理, 只让server sleep
	 */
	public void holdOn() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}
	
}
