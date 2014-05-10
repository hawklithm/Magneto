package org.hawklithm.magneto.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.hawklithm.magneto.callback.InstanceCallBack;
import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.RPCInstanceInfoDO;
import org.hawklithm.magneto.dataobject.RemoteCallCommunicationProtocol;
import org.hawklithm.magneto.global.MagnetoConstant;
import org.hawklithm.magneto.serviceDAO.ServiceGetter;
import org.hawklithm.magneto.utils.HessianUtils;
import org.hawklithm.magneto.utils.Jsoner;
import org.hawklithm.magneto.utils.RemoteCallCommunicationProtocolFactory;
import org.hawklithm.netty.client.TcpNettyClient;
import org.hawklithm.netty.client.UdpNettyClient;
import org.hawklithm.netty.exception.ChannelMustNotBeNullException;
import org.hawklithm.netty.handler.impl.TcpNettyHandler;
import org.hawklithm.netty.handler.impl.UdpNettyHandler;
import org.hawklithm.netty.server.UdpNettyServer;
import org.hawklithm.shadowsong.dataobject.WardenMessage;
import org.hawklithm.shadowsong.pusher.IMessagePusher;
import org.hawklithm.shadowsong.register.IRegisterManager;
import org.hawklithm.shadowsong.warden.Warden;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

public class Client {
	
	private static String CLIENT_RPC_CALL_MODULE="client_rpc_call_module";
	
	private boolean aquireForce=false;
	private String callerAuthString="";
	private IRegisterManager registerManager;
	private ServiceGetter serviceGetter;
	private IMessagePusher<WardenMessage> messagePusher;
	private TcpNettyClient tcpClient;
	private UdpNettyClient udpClient;
	private UdpNettyServer udpServer;
	private RemoteCallCommunicationProtocolFactory protocolFactory=new RemoteCallCommunicationProtocolFactory();

	

	public Client(int tcpPort,int udpPort) {
		tcpClient=new TcpNettyClient(tcpPort);
		udpClient=new UdpNettyClient(udpPort);
		udpServer=new UdpNettyServer(udpPort);
	}
	public Client(int tcpPort,int udpPort,String address){
		tcpClient=new TcpNettyClient(tcpPort,address);
		udpClient=new UdpNettyClient(udpPort,address);
		udpServer=new UdpNettyServer(udpPort);
	}
	
	public void init(){
		tcpClient.setNettyHandler(new TcpNettyHandler(){

			@Override
			public String onMessageReceived(String message, SocketAddress  remoteAddress){
				RemoteCallCommunicationProtocol protocol=Jsoner.fromJson(message, RemoteCallCommunicationProtocol.class);
				switch(protocol.getOperateType()){
				case MagnetoConstant.RPC_OPERATION_TYPE_CALL:
					RPCCallInfoDO rpcCallInfo=Jsoner.fromJson(protocol.getMessage(),RPCCallInfoDO.class);
					messagePusher.push(new WardenMessage(rpcCallInfo.getInterfaceName(),MagnetoConstant.RPC_OPERATION_TYPE_CALL+protocol.getCount(),rpcCallInfo));
					break;
				case MagnetoConstant.RPC_OPERATION_TYPE_REGIST:
					break;
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see org.jboss.netty.channel.SimpleChannelHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
			 */
			@Override
			public void channelConnected(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
				tcpClient.setChannel(e.getChannel());
			}
			
		});
		udpClient.setNettyHandler(new UdpNettyHandler() {
			
			@Override
			public String onMessageReceived(String message, SocketAddress remoteAddress) {
				RemoteCallCommunicationProtocol protocol=Jsoner.fromJson(message, RemoteCallCommunicationProtocol.class);
				switch(protocol.getOperateType()){
				case MagnetoConstant.RPC_OPERATION_TYPE_CALL:
					RPCInstanceInfoDO instanceInfo=Jsoner.fromJson(protocol.getMessage(), RPCInstanceInfoDO.class);
					messagePusher.push(new WardenMessage(instanceInfo.getInterfaceName(), MagnetoConstant.RPC_OPERATION_TYPE_CALL+RemoteCallCommunicationProtocolFactory.REQUEST_RPC_INSTANCE_ANSWER, instanceInfo));
					break;
				case MagnetoConstant.RPC_OPERATION_TYPE_REGIST:
					break;
				}
				return null;
			}
		});
		serviceGetter.setNettyHandler(tcpClient.getNettyHandler());
		udpServer.setNettyHandler(new UdpNettyHandler() {
			
			@Override
			public String onMessageReceived(String message, SocketAddress remoteAddress) {
				RemoteCallCommunicationProtocol protocol=Jsoner.fromJson(message, RemoteCallCommunicationProtocol.class);
				if (protocol.getOperateType().equals(MagnetoConstant.RPC_OPERATION_TYPE_CALL)){
					RPCCallInfoDO rpcCallInfo=Jsoner.fromJson(protocol.getMessage(), RPCCallInfoDO.class);
					RPCInstanceInfoDO instanceInfo=serviceGetter.getServiceInstance(rpcCallInfo.getInterfaceName()+rpcCallInfo.getVersion());
					return Jsoner.toJson(instanceInfo);
				}
				return null;
			}
		});
		/**
		 * 初始化netty
		 */
		tcpClient.initNetty();
		udpClient.initNetty();
		udpServer.initNetty();
	}
	
	
	
	public void registService(){
		
	}
	
	public void callService(String interfaceName,String version,final InstanceCallBack callBack){
		RPCCallInfoDO infoDO=new RPCCallInfoDO();
		RemoteCallCommunicationProtocol protocol=new RemoteCallCommunicationProtocol();
		try {
			infoDO.setAddress(InetAddress.getLocalHost().toString());
			infoDO.setAquireForce(aquireForce);
			infoDO.setCallerAuth(callerAuthString);
			infoDO.setInterfaceName(interfaceName);
			infoDO.setVersion(version);
			protocol.setMessage(Jsoner.toJson(infoDO));
			protocol.setOperateType(MagnetoConstant.RPC_OPERATION_TYPE_CALL);
			registerManager.regist(new Warden(false, 1, infoDO.getInterfaceName(), MagnetoConstant.RPC_OPERATION_TYPE_CALL+RemoteCallCommunicationProtocolFactory.REQUEST_RPC_INFO_ANSWER){

				@Override
				public void asynchronizedProcess(Object message) {
					callServiceBack((RPCCallInfoDO)message);
				}
				
			});
			registerManager.regist(new Warden(false,1,infoDO.getInterfaceName(),MagnetoConstant.RPC_OPERATION_TYPE_CALL+RemoteCallCommunicationProtocolFactory.REQUEST_RPC_INSTANCE_ANSWER){

				@Override
				public void asynchronizedProcess(Object message) {
					RPCInstanceInfoDO instanceInfo=(RPCInstanceInfoDO) message;
					try {
						callBack.callBack(HessianUtils.deserialize(instanceInfo.getInstanceByte()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			});
			tcpClient.sendMessage(Jsoner.toJson(protocol));
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ChannelMustNotBeNullException e) {
			e.printStackTrace();
		}
	}

	public void callServiceBack(RPCCallInfoDO msg){
		protocolFactory.setRequestRpcInstanceCount();
		protocolFactory.setOperateRpcCall();
		RemoteCallCommunicationProtocol protocol=protocolFactory.getProtocol(Jsoner.toJson(msg));
		try {
			udpClient.sendMessage(Jsoner.toJson(protocol));
		} catch (ChannelMustNotBeNullException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return the registerManager
	 */
	public IRegisterManager getRegisterManager() {
		return registerManager;
	}
	/**
	 * @param registerManager the registerManager to set
	 */
	public void setRegisterManager(IRegisterManager registerManager) {
		this.registerManager = registerManager;
	}
	/**
	 * @return the messagePusher
	 */
	public IMessagePusher<WardenMessage> getMessagePusher() {
		return messagePusher;
	}
	/**
	 * @param messagePusher the messagePusher to set
	 */
	public void setMessagePusher(IMessagePusher<WardenMessage> messagePusher) {
		this.messagePusher = messagePusher;
	}

}
