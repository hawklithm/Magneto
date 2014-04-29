package org.hawklithm.magneto.dataobject;

public class RPCRegistInfoDO extends ZooNodeInfoDO{
	private String interfaceName;
	
	public RPCRegistInfoDO(){
	}
	public RPCRegistInfoDO(ZooNodeInfoDO zooNodeInfo){
		this.setAclString(zooNodeInfo.getAclString());
		this.setProviderAddress(zooNodeInfo.getProviderAddress());
		this.setProviderName(zooNodeInfo.getProviderName());
		this.setTime(zooNodeInfo.getTime());
		this.setVersion(zooNodeInfo.getVersion());
	}

	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}

	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}