package com.micaja.servipunto.database.dto;

public class AddMenusInvenDTO implements DTO
{
	private String purchagePrice;
	private String sellingPrice;
	private String quantity;
	private String vat;
	private String totalItems;
	private String dishID;
	private String dishName;
	private String noOfItems;
	private String expiryDays;
	
	
	public String getPurchagePrice() {
		return purchagePrice;
	}
	public void setPurchagePrice(String purchagePrice) {
		this.purchagePrice = purchagePrice;
	}
	public String getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quntity) {
		this.quantity = quntity;
	}
	public String getVat() {
		return vat;
	}
	public void setVat(String vat) {
		this.vat = vat;
	}
	public String getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(String totalItems) {
		this.totalItems = totalItems;
	}
	public String getDishID() {
		return dishID;
	}
	public void setDishID(String dishID) {
		this.dishID = dishID;
	}
	public String getDishName() {
		return dishName;
	}
	public void setDishName(String dishName) {
		this.dishName = dishName;
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
}
