/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class MenuDishesDTO implements DTO {
 
	private String menuDishesId;
	private String menuId;
	private String dishId;
	private Integer syncStatus;
 
	public String getMenuDishesId() {
		return menuDishesId;
	}
	public void setMenuDishesId(String menuDishesId) {
		this.menuDishesId = menuDishesId;
	 }
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	 }
	public String getDishId() {
		return dishId;
	}
	public void setDishId(String dishId) {
		this.dishId = dishId;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

