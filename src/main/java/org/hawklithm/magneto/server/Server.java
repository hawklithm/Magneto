package org.hawklithm.magneto.server;

import java.io.IOException;
import java.net.SocketAddress;

import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.RPCRegistInfoDO;
import org.hawklithm.magneto.dataobject.RemoteCallCommunicationProtocol;
import org.hawklithm.magneto.global.MagnetoConstant;
import org.hawklithm.magneto.serviceDAO.ServiceGetter;
import org.hawklithm.magneto.utils.HessianUtils;
import org.hawklithm.magneto.utils.Jsoner;
import org.hawklithm.netty.exception.ChannelMustNotBeNullException;
import org.hawklithm.netty.handler.impl.TcpNettyHandler;
import org.hawklithm.netty.server.TcpNettyServer;

public class Server extends TcpNettyServer {
	
	private ServiceGetter getter;

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
//					RPCRegistInfoDO registInfo=Jsoner.fromJson(protocol.getMessage(), RPCRegistInfoDO.class);
					//TODO 检查权限
					try {
						protocol.setMessage(new String(HessianUtils.serialize(getter.getConnector())));
						return Jsoner.toJson(protocol);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case MagnetoConstant.RPC_OPERATION_TYPE_CALL:
					// 调用rpc
					RPCCallInfoDO callInfo=Jsoner.fromJson(protocol.getMessage(),RPCCallInfoDO.class);
					RPCRegistInfoDO registInfo=getter.getServiceInfo(callInfo.getInterfaceName());
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
	 * @return the getter
	 */
	public ServiceGetter getGetter() {
		return getter;
	}

	/**
	 * @param getter the getter to set
	 */
	public void setGetter(ServiceGetter getter) {
		this.getter = getter;
	}
	

}
