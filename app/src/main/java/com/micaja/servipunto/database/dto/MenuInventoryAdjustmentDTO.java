/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class MenuInventoryAdjustmentDTO implements DTO {
 
	private String menuAdjustmentId;
	private String dishId;
	private String count;
	private Integer syncStatus;
 
	public String getMenuAdjustmentId() {
		return menuAdjustmentId;
	}
	public void setMenuAdjustmentId(String menuAdjustmentId) {
		this.menuAdjustmentId = menuAdjustmentId;
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

