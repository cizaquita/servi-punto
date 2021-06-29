/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dto;

public class UserDetailsDTO implements DTO {
	private String userId;
	private String nitShopId;
	private String userName;
	private String password;
	private String name;
	private String companyName;
	private String imei;
	private String registrationDate;
	private String lastLogin;
	private String cedulaDocument;
	private String openingBalance;
	private String openingDateTime;
	private Integer isClosed;
	private String closeDateTime;
	private String actualBalance;
	private Integer syncStatus;
	private String terminalId;
	private String puntoredId;
	private String comercioId;
	private String systemId;
	private String is_admin;
	private String is_authorized;
	private String email;
	private String authtoken;

	public String getAuthtoken() {
		return authtoken;
	}

	public void setAuthtoken(String authtoken) {
		this.authtoken = authtoken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIs_admin() {
		return is_admin;
	}

	public void setIs_admin(String is_admin) {
		this.is_admin = is_admin;
	}

	public String getIs_authorized() {
		return is_authorized;
	}

	public void setIs_authorized(String is_authorized) {
		this.is_authorized = is_authorized;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getPuntoredId() {
		return puntoredId;
	}

	public void setPuntoredId(String puntoredId) {
		this.puntoredId = puntoredId;
	}

	public String getComercioId() {
		return comercioId;
	}

	public void setComercioId(String comercioId) {
		this.comercioId = comercioId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNitShopId() {
		return nitShopId;
	}

	public void setNitShopId(String nitShopId) {
		this.nitShopId = nitShopId;
	}

	public String getCedulaDocument() {
		return cedulaDocument;
	}

	public void setCedulaDocument(String cedulaDocument) {
		this.cedulaDocument = cedulaDocument;
	}

	public String getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getOpeningDateTime() {
		return openingDateTime;
	}

	public void setOpeningDateTime(String openingDateTime) {
		this.openingDateTime = openingDateTime;
	}

	public Integer getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(Integer isClosed) {
		this.isClosed = isClosed;
	}

	public String getCloseDateTime() {
		return closeDateTime;
	}

	public void setCloseDateTime(String closeDateTime) {
		this.closeDateTime = closeDateTime;
	}

	public Integer getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(Integer syncStatus) {
		this.syncStatus = syncStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getActualBalance() {
		return actualBalance;
	}

	public void setActualBalance(String actualBalance) {
		this.actualBalance = actualBalance;
	}

}
