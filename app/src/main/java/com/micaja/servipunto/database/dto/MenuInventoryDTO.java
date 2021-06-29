/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class MenuInventoryDTO implements DTO {
 
	private String menuInventoryId;
	private String dishId;
	private String count;
	private Integer syncStatus;
 
	public String getMenuInventoryId() {
		return menuInventoryId;
	}
	public void setMenuInventoryId(String menuInventoryId) {
		this.menuInventoryId = menuInventoryId;
	 }
	public String getDishId() {
		return dishId;
	}
	public void setDishId(String dishId) {
		this.dishId = dishId;
	 }
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

