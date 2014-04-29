package org.hawklithm.magneto.dataobject;

import java.util.Date;

/**
 * 服务器与客户端之间数据传输的格式
 * @author bluehawky
 *
 */
public class RPCCallInfoDO {
	private String callerAuth;
	private boolean aquireForce;
	private String address;
	private String version;
	private String interfaceName;
	private Date lastVersionTime;
	private String Extention;

	/**
	 * @return the aquireForce
	 */
	public boolean isAquireForce() {
		return aquireForce;
	}
	/**
	 * @param aquireForce the aquireForce to set
	 */
	public void setAquireForce(boolean aquireForce) {
		this.aquireForce = aquireForce;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
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
	/**
	 * @return the callerAuth
	 */
	public String getCallerAuth() {
		return callerAuth;
	}
	/**
	 * @param callerAuth the callerAuth to set
	 */
	public void setCallerAuth(String callerAuth) {
		this.callerAuth = callerAuth;
	}
	/**
	 * @return the lastVersionTime
	 */
	public Date getLastVersionTime() {
		return lastVersionTime;
	}
	/**
	 * @param lastVersionTime the lastVersionTime to set
	 */
	public void setLastVersionTime(Date lastVersionTime) {
		this.lastVersionTime = lastVersionTime;
	}
	/**
	 * @return the extention
	 */
	public String getExtention() {
		return Extention;
	}
	/**
	 * @param extention the extention to set
	 */
	public void setExtention(String extention) {
		Extention = extention;
	}
}
