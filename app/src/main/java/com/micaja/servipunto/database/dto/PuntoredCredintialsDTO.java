package com.micaja.servipunto.database.dto;

public class PuntoredCredintialsDTO implements DTO {

	private String terminalId;
	private String puntoredId;
	private String comercioId;
	private String systemId;
	private String userName;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
