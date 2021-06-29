/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class MenuTypesDTO implements DTO {
 
	private String menuTypeId;
	private String name;
	private Integer syncStatus;
 
	public String getMenuTypeId() {
		return menuTypeId;
	}
	public void setMenuTypeId(String menuTypeId) {
		this.menuTypeId = menuTypeId;
	 }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int	 syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

