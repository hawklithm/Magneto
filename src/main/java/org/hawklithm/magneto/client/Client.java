package org.hawklithm.magneto.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hawklithm.magneto.buffer.BufferHandler;
import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.RemoteCallCommunicationProtocol;
import org.hawklithm.magneto.global.MagnetoConstant;
import org.hawklithm.magneto.utils.Jsoner;
import org.hawklithm.netty.client.NettyClient;
import org.hawklithm.netty.exception.ChannelMustNotBeNullException;
import org.hawklithm.netty.handler.NettyHandler;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Client extends NettyClient{
	
	private boolean aquireForce=false;
	private String callerAuthString="";

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
				switch(protocol.getCount()){
				case 2:
					
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ChannelMustNotBeNullException e) {
			e.printStackTrace();
		}
	}

	public void callServiceBack(String msg){
		RPCCallInfoDO info=Jsoner.fromJson(msg, RPCCallInfoDO.class);
		bufferHandler.getValue(info.getInterfaceName());
	}
	

}
