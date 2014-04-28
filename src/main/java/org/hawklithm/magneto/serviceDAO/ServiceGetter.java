package org.hawklithm.magneto.serviceDAO;

import java.io.UnsupportedEncodingException;

import org.hawklithm.magneto.buffer.BufferHandler;
import org.hawklithm.magneto.dataobject.RPCCallInfoDO;
import org.hawklithm.magneto.dataobject.ZooNodeInfoDO;
import org.hawklithm.magneto.exception.ServiceDataBrokenException;
import org.hawklithm.magneto.utils.Jsoner;
import org.hawklithm.magneto.zookeeper.ZookeeperConnectorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ServiceGetter {
	@Autowired @Qualifier("connector")
	private ZookeeperConnectorImpl connector;
	private BufferHandler bufferHandler;
	
	private ZooNodeInfoDO getServiceInfo(String key) throws ServiceDataBrokenException{
		try {
			return Jsoner.fromJson(new String(bufferHandler.getValue(key),"utf-8"),ZooNodeInfoDO.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ServiceDataBrokenException();
		}
	}
	
	public ZooNodeInfoDO getServiceInfo(RPCCallInfoDO info) throws ServiceDataBrokenException{
		return getServiceInfo(info.getInterfaceName());
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
