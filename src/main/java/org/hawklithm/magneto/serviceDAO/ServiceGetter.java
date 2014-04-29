package org.hawklithm.magneto.serviceDAO;

import org.hawklithm.magneto.buffer.BufferHandler;
import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.RPCInstanceInfoDO;
import org.hawklithm.magneto.dataobject.RPCRegistInfoDO;
import org.hawklithm.magneto.dataobject.ZooNodeInfoDO;
import org.hawklithm.magneto.exception.ServiceDataBrokenException;
import org.hawklithm.magneto.zookeeper.ZookeeperConnectorImpl;
import org.hawklithm.netty.handler.NettyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ServiceGetter {
	@Autowired @Qualifier("connector")
	private ZookeeperConnectorImpl connector;
	@Autowired @Qualifier("rpcInfoBufferHandler")
	private BufferHandler<String,RPCRegistInfoDO> rpcInfoBufferHandler;
	@Autowired @Qualifier("rpcInstanceBufferHandler")
	private BufferHandler<String,RPCInstanceInfoDO> rpcInstanceBufferHandler;
	
	private NettyHandler nettyHandler;
	
	public RPCRegistInfoDO getServiceInfo(String serviceName){
		return rpcInfoBufferHandler.getValue(serviceName);
	}
	public RPCInstanceInfoDO getServiceInstance(String serviceName){
		return rpcInstanceBufferHandler.getValue(serviceName);
	}
	
	public RPCRegistInfoDO getService(RPCCallInfoDO info) throws ServiceDataBrokenException{
		RPCRegistInfoDO rpcRegistInfo = getServiceInfo(info.getInterfaceName());
		if (rpcRegistInfo.getTime().before(info.getLastVersionTime())){
			/**
			 * 缓存中的时间点小于接收到的信息的版本时间说明需要更新缓存
			 */
		}else {
			/**
			 * 缓存中的信息还是最新的，直接取用即可
			 */
			RPCInstanceInfoDO instanceInfo=getServiceInstance(info.getInterfaceName());
		}
		RPCRegistInfoDO registInfo=new RPCRegistInfoDO(zooNodeInfo);
		registInfo.setInterfaceName(info.getInterfaceName());
		return registInfo;
	}

	/**
	 * @return the connector
	 */
	public ZookeeperConnectorImpl getConnector() {
		return connector;
	}

	/**
	 * @param connector the connector to set
	 */
	public void setConnector(ZookeeperConnectorImpl connector) {
		this.connector = connector;
	}
	/**
	 * @return the nettyHandler
	 */
	public NettyHandler getNettyHandler() {
		return nettyHandler;
	}
	/**
	 * @param nettyHandler the nettyHandler to set
	 */
	public void setNettyHandler(NettyHandler nettyHandler) {
		this.nettyHandler = nettyHandler;
	}


}
