package org.hawklithm.magneto.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hawklithm.magneto.buffer.BufferHandler;
import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.RPCRegistInfoDO;
import org.hawklithm.magneto.dataobject.RemoteCallCommunicationProtocol;
import org.hawklithm.magneto.global.MagnetoConstant;
import org.hawklithm.magneto.utils.Jsoner;
import org.hawklithm.netty.client.NettyClient;
import org.hawklithm.netty.exception.ChannelMustNotBeNullException;
import org.hawklithm.netty.handler.NettyHandler;
import org.hawklithm.shadowsong.dataobject.WardenMessage;
import org.hawklithm.shadowsong.pusher.IMessagePusher;
import org.hawklithm.shadowsong.register.IRegisterManager;
import org.hawklithm.shadowsong.warden.Warden;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Client extends NettyClient{
	
	private boolean aquireForce=false;
	private String callerAuthString="";
	private IRegisterManager registerManager;
	private IMessagePusher<WardenMessage> messagePusher;

	@Autowired @Qualifier("bufferHandler")
	private BufferHandler bufferHandler;

	public Client(int port) {
		super(port);
		initHandler();
	}
	public Client(int port,String address){
		super(port,address);
		initHandler();
	}
	
	public void initHandler(){
		setHandler(new NettyHandler(){

			@Override
			public void onMessageReceived(String msg, Channel ch) {
				RemoteCallCommunicationProtocol protocol=Jsoner.fromJson(msg, RemoteCallCommunicationProtocol.class);
				switch(protocol.getOperateType()){
				case MagnetoConstant.RPC_OPERATION_TYPE_CALL:
					RPCRegistInfoDO registInfo=Jsoner.fromJson(protocol.getMessage(),RPCRegistInfoDO.class);
					messagePusher.push(new WardenMessage(registInfo.getInterfaceName(),MagnetoConstant.RPC_OPERATION_TYPE_CALL+protocol.getCount(),registInfo));
					break;
				case MagnetoConstant.RPC_OPERATION_TYPE_REGIST:
					break;
				}
			}

			/* (non-Javadoc)
			 * @see org.jboss.netty.channel.SimpleChannelHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
			 */
			@Override
			public void channelConnected(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
				setChannel(e.getChannel());
			}
			
		});
	}
	
	
	
	public void registService(){
		
	}
	
	public void callService(String interfaceName,String version){
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
			sendMessage(Jsoner.toJson(protocol));
			registerManager.regist(new Warden(false, 1, infoDO.getInterfaceName(), MagnetoConstant.RPC_OPERATION_TYPE_CALL+2){

				@Override
				public void asynchronizedProcess(Object message) {
					
				}
				
			});
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ChannelMustNotBeNullException e) {
			e.printStackTrace();
		}
	}

	public void callServiceBack(RPCRegistInfoDO msg){
		bufferHandler.getValue(msg.getInterfaceName());
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
