package org.hawklithm.magneto.server;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.zookeeper.KeeperException;
import org.hawklithm.h2db.dataobject.RPCRegistInfoDO;
import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.RemoteCallCommunicationProtocol;
import org.hawklithm.magneto.global.MagnetoConstant;
import org.hawklithm.magneto.utils.HessianUtils;
import org.hawklithm.magneto.utils.Jsoner;
import org.hawklithm.magneto.zookeeper.ZookeeperConnectorImpl;
import org.hawklithm.netty.handler.impl.TcpNettyHandler;
import org.hawklithm.netty.server.TcpNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Server extends TcpNettyServer {
	
	@Autowired @Qualifier("connector")
	private ZookeeperConnectorImpl connector;
	
	public Server(int port) {
		super(port);
		setNettyHandler(new TcpNettyHandler(){

			@Override
			public String onMessageReceived(String message, SocketAddress  remoteAddress){
				RemoteCallCommunicationProtocol protocol=Jsoner.fromJson(message, RemoteCallCommunicationProtocol.class);
				protocol.countPlus();
				switch(protocol.getOperateType()){
				case MagnetoConstant.RPC_OPERATION_TYPE_REGIST:
					// 注册RPC
					//TODO 从protocol.getMessage里面检查权限
					try {
						protocol.setMessage(Jsoner.toJson(HessianUtils.serialize(connector)));
						return Jsoner.toJson(protocol);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case MagnetoConstant.RPC_OPERATION_TYPE_CALL:
					// 调用rpc
					RPCCallInfoDO callInfo=Jsoner.fromJson(protocol.getMessage(),RPCCallInfoDO.class);
					RPCRegistInfoDO registInfo=getServiceInfo(callInfo.getInterfaceName(),callInfo.getVersion());
					//TODO 检查权限
					/**
					 * 重组数据并将调用信息回传给客户端
					 */
					callInfo.setAddress(registInfo.getProviderAddress());
					callInfo.setLastVersionTime(registInfo.getTime());
					protocol.setMessage(Jsoner.toJson(callInfo));
					String result=Jsoner.toJson(protocol);
					protocol.setMessage(result);
					if (result!=null){
						return Jsoner.toJson(protocol);
					}
					break;
				}
				return null;
			}
			
		});
	}
	
	/**
	 * 
	 * @param serviceName
	 * @param version
	 * @return
	 */
	public RPCRegistInfoDO getServiceInfo(String serviceName,String version){
		try {
			return connector.getService(serviceName+"/"+version);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
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


}
