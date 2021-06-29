/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class MenuDTO implements DTO {
 
	private String menuId;
	private String name;
	private String menuTypeId;
	private String startDate;
	private String endDate;
	private String weekDays;
	private Integer syncStatus;
 
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	 }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	 }
	public String getMenuTypeId() {
		return menuTypeId;
	}
	public void setMenuTypeId(String menuTypeId) {
		this.menuTypeId = menuTypeId;
	 }
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	 }
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	 }
	public String getWeekDays() {
		return weekDays;
	}
	public void setWeekDays(String weekDays) {
		this.weekDays = weekDays;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

