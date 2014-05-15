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

public class ZookeeperConnectorImpl implements ZookeeperConnector {

	public static String rpcRootGroupNode = "rpcgroup";
	private ZooKeeper zk;
	private String ZookeeperAddress;

	/**
	 * 连接zookeeper
	 * 
	 * @param zooAddress
	 *            zookeeper的服务器地址
	 * @param serviceAddress
	 *            提供服务者的地址
	 * @param serviceName
	 *            服务名，用服务的接口全名做服务名
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws Exception
	 */
	@Override
	public ZooKeeper supplyServiceToZookeeper(RPCRegistInfoDO serviceInfo) throws IOException, KeeperException,
			InterruptedException {
		zk = new ZooKeeper(ZookeeperAddress/* "localhost:2181" */, 5000,
				new Watcher() {
					public void process(WatchedEvent event) {
						// do nothing
					}
				});
		String path = createNodePersistent(zk, "/" + rpcRootGroupNode + "/"
				+ serviceInfo.getInterfaceName());
		String createdPath = createNodeEphemeral(zk, path,
				serviceInfo.getVersion(),
				Jsoner.toJson(serviceInfo).getBytes("utf-8"));
		System.out.println("create: " + createdPath);
		return zk;
	}

	/**
	 * create ehpemeral node and it will be deleted when connection closed
	 * 
	 * @param zk
	 * @param address
	 * @param nodeName
	 * @param data
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private String createNodeEphemeral(ZooKeeper zk, String address,
			String nodeName, byte[] data) throws KeeperException,
			InterruptedException {
		String path=address+"/"+nodeName;
		if (zk.exists(path, true)==null){
		return zk.create(path, data, Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		}
		return null;
	}

	/**
	 * create node persistent
	 * 
	 * @param zk
	 * @param nodes
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private String createNodePersistent(ZooKeeper zk, String nodePath)
			throws UnsupportedEncodingException, KeeperException,
			InterruptedException {
		if (zk.exists(nodePath, true)==null){
		return zk.create(nodePath, "".getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		}
		return nodePath;
	}

	/**
	 * server的工作逻辑写在这个方法中 此处不做任何处理, 只让server sleep
	 */
	public void holdOn() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}

	/* (non-Javadoc)
	 * @see org.hawklithm.magneto.zookeeper.ZookeeperConnector#getService(java.lang.String)
	 */
	@Override
	public RPCRegistInfoDO getService(String servicePath) throws KeeperException, InterruptedException {
		byte[] data=zk.getData(servicePath, true, null);
		try {
			return Jsoner.fromJson(new String(data,"utf-8"),RPCRegistInfoDO.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the zookeeperAddress
	 */
	public String getZookeeperAddress() {
		return ZookeeperAddress;
	}

	/**
	 * @param zookeeperAddress the zookeeperAddress to set
	 */
	public void setZookeeperAddress(String zookeeperAddress) {
		ZookeeperAddress = zookeeperAddress;
	}

}
