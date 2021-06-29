package com.micaja.servipunto.database.dto;

public class MenusInveDishesDTO implements DTO
{
	private String dishName;
	private String dishPrice;
	private String expiryDays;
	private String sellingCostPerItem;
	private String dishId;
	private String menuInventoryId;
	private String count;
	
	public String getDishName() {
		return dishName;
	}
	public void setDishName(String dishName) {
		this.dishName = dishName;
	}
	public String getDishPrice() {
		return dishPrice;
	}
	public void setDishPrice(String dishPrice) {
		this.dishPrice = dishPrice;
	}
	public String getExpiryDays() {
		return expiryDays;
	}
	public void setExpiryDays(String expiryDays) {
		this.expiryDays = expiryDays;
	}
	public String getSellingCostPerItem() {
		return sellingCostPerItem;
	}
	public void setSellingCostPerItem(String sellingCostPerItem) {
		this.sellingCostPerItem = sellingCostPerItem;
	}
	public String getDishId() {
		return dishId;
	}
	public void setDishId(String dishId) {
		this.dishId = dishId;
	}
	public String getMenuInventoryId() {
		return menuInventoryId;
	}
	public void setMenuInventoryId(String menuInventoryId) {
		this.menuInventoryId = menuInventoryId;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
}
