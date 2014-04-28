package org.hawklithm.magneto.dataobject;

import java.util.Date;

public class ZooNodeInfoDO {
	private Date time;
	private String providerAddress;
	private String aclString;
	private String providerName;
	private String version;
	/**
	 * @return the providerAddress
	 */
	public String getProviderAddress() {
		return providerAddress;
	}
	/**
	 * @param providerAddress the providerAddress to set
	 */
	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}
	/**
	 * @return the aclString
	 */
	public String getAclString() {
		return aclString;
	}
	/**
	 * @param aclString the aclString to set
	 */
	public void setAclString(String aclString) {
		this.aclString = aclString;
	}
	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}
	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
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
}
