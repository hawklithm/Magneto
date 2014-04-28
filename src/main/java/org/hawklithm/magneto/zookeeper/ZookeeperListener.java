package org.hawklithm.magneto.zookeeper;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.hawklithm.magneto.buffer.BufferHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ZookeeperListener {
	public static String rpcRootGroupNode="rpcgroup";
	private ZooKeeper zk;
	private Stat stat = new Stat();
	@Autowired @Qualifier("bufferHandler")
	private BufferHandler bufferHandler;

	/**
	 * 将监听器连至zookeeper
	 * @param zooAddress zookeeper地址,格式 ip_address:port
	 * @throws Exception
	 */
	public void connectZookeeper(String zooAddress,Watcher watcher) throws Exception {
		zk = new ZooKeeper(zooAddress/*"localhost:2181"*/, 5000, new Watcher() {
			public void process(WatchedEvent event) {
				// 如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听
				String path=event.getPath();
				if (path.startsWith(rpcRootGroupNode)){
					//TODO the detail operator will be add then
					switch(event.getType()){
					case NodeDeleted:
						System.out.println(path+" was deleted");
						break;
					case NodeDataChanged:
						System.out.println(path+" node data changed");
						break;
					case NodeChildrenChanged:
						System.out.println(path+"'s children was changed");
						break;
					}
				}
			}
		});
		travelAllNodes("/"+rpcRootGroupNode,bufferHandler);
	}
	
	private void travelAllNodes(String rootAddress,BufferHandler handler){
		try {
			List<String> subNodes=zk.getChildren("/"+rootAddress, true);
			if (subNodes.size()==0){
				System.out.println("[zookeeper get the leaf node]"+rootAddress);
				byte[] data = zk.getData("/" + rootAddress , false, stat);
				handler.store(rootAddress, data);
				return;
			}
			for (String node:subNodes){
				travelAllNodes(rootAddress+node,handler);
			}
		} catch (KeeperException e) {
			if (e.code().compareTo(KeeperException.Code.NONODE)==0){
				System.out.println("noNode");
				return;
			}
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the bufferHandler
	 */
	public BufferHandler getBufferHandler() {
		return bufferHandler;
	}

	/**
	 * @param bufferHandler the bufferHandler to set
	 */
	public void setBufferHandler(BufferHandler bufferHandler) {
		this.bufferHandler = bufferHandler;
	}

}
