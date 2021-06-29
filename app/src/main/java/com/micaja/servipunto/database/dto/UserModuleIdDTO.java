package com.micaja.servipunto.database.dto;

public class UserModuleIdDTO implements DTO
{
	private String userModuleId;
	private String moduleName;
	private String moduleCode;
	
	public String getUserModuleId() {
		return userModuleId;
	}
	public void setUserModuleId(String userModuleId) {
		this.userModuleId = userModuleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
}
