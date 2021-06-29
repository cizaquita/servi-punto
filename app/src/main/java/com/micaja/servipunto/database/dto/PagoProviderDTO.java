package com.micaja.servipunto.database.dto;

public class PagoProviderDTO implements DTO {
	private String id;
	private String provDesc;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProvDesc() {
		return provDesc;
	}
	public void setProvDesc(String provDesc) {
		this.provDesc = provDesc;
	}
	
}
