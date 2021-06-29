package com.micaja.servipunto.database.dto;

public class MenusEditDTO implements DTO 
{
	private String dishId;
	private String dishName;
	private String dishPrice;
	private String sellingPricePerItem;
	private String menuId;
	private String menuName;
	private String menuDishId;
	private String expiryDays;
	private String noOfItems;
	private String vat;
	
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
	public String getSellingPricePerItem() {
		return sellingPricePerItem;
	}
	public void setSellingPricePerItem(String sellingPricePerItem) {
		this.sellingPricePerItem = sellingPricePerItem;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuDishId() {
		return menuDishId;
	}
	public void setMenuDishId(String menuDishId) {
		this.menuDishId = menuDishId;
	}
	public String getExpiryDays() {
		return expiryDays;
	}
	public void setExpiryDays(String expiryDays) {
		this.expiryDays = expiryDays;
	}
	public String getNoOfItems() {
		return noOfItems;
	}
	public void setNoOfItems(String noOfItems) {
		this.noOfItems = noOfItems;
	}
	public String getVat() {
		return vat;
	}
	public void setVat(String vat) {
		this.vat = vat;
	}
}
