package org.hawklithm.magneto.dataobject;

public class RPCRegistInfoDO extends ZooNodeInfoDO{
	private String interfaceName;

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
