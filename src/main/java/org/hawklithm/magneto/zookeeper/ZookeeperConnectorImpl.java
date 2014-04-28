package org.hawklithm.magneto.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperConnectorImpl implements ZookeeperConnector{
	
	public static String rpcRootGroupNode="rpcgroup";
	/**
	 * 连接zookeeper
	 * @param zooAddress  zookeeper的服务器地址
	 * @param serviceAddress 提供服务者的地址
	 * @param serviceName	服务名，用服务的接口全名做服务名
	 * @throws Exception
	 */
	public void supplyServiceToZookeeper(String zooAddress,String serviceAddress,String serviceName) throws Exception {
		ZooKeeper zk = new ZooKeeper(zooAddress/*"localhost:2181"*/, 5000, new Watcher() {
			public void process(WatchedEvent event) {
				// do nothing
			}
		});
		String subNode=dotToSlash(serviceName);
		
		// 在 rpcRootGroupNode下创建子节点
		// 子节点的类型设置为EPHEMERAL_SEQUENTIAL, 表明这是一个临时节点, 且在子节点的名称后面加上一串数字后缀
		// 将server的地址数据关联到新创建的子节点上
		String createdPath = zk.create("/" + rpcRootGroupNode + "/" + subNode, serviceAddress.getBytes("utf-8"), 
			Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("create: " + createdPath);
	}
	
	/**
	 * 将类名中的点替换为斜杠
	 * @param old
	 * @return
	 */
	public String dotToSlash(String old){
		StringBuilder builder=new StringBuilder(old.length());
		String[] tmp=old.split(".");
		builder.append(tmp[0]);
		for (int i=1;i<tmp.length;i++){
			builder.append('/').append(tmp);
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
