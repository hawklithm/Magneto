package org.hawklithm.magneto.dataobject;

import java.util.Date;

public class CallerHistoryDO {
	private String calller;
	private String address;
	private Date time;
	private String interfaceName;
	private String version;
	/**
	 * @return the calller
	 */
	public String getCalller() {
		return calller;
	}
	/**
	 * @param calller the calller to set
	 */
	public void setCalller(String calller) {
		this.calller = calller;
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
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
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
}
