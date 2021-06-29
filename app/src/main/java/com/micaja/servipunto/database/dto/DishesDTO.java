/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class DishesDTO implements DTO {
 
	private String dishId;
	private String dishName;
	private String dishPrice;
	private String noOfItems;
	private String expiryDays;
	private String vat;
	private String sellingCostperItem;
	private Integer syncStatus;
 
	public String getDishId() {
		return dishId;
	}
	public void setDishId(String dishId) {
		this.dishId = dishId;
	 }
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
	public String getNoOfItems() {
		return noOfItems;
	}
	public void setNoOfItems(String noOfItems) {
		this.noOfItems = noOfItems;
	 }
	public String getExpiryDays() {
		return expiryDays;
	}
	public void setExpiryDays(String expiryDays) {
		this.expiryDays = expiryDays;
	 }
	public String getVat() {
		return vat;
	}
	public void setVat(String vat) {
		this.vat = vat;
	 }
	public String getSellingCostperItem() {
		return sellingCostperItem;
	}
	public void setSellingCostperItem(String sellingCostperItem) {
		this.sellingCostperItem = sellingCostperItem;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

