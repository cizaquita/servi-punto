package com.micaja.servipunto.database.dto;

public class PagoProgramsDTO implements DTO {
	private String state;
	private String name;
	private String progId;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProgId() {
		return progId;
	}
	public void setProgId(String progId) {
		this.progId = progId;
	}
	
}
