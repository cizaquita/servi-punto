package com.micaja.servipunto.database.dto;

public class DishesEditDTO implements DTO
{
	private String productId;
	private String productName;
	private String purchasePrice;
	private String sellingPrice;
	private String dishProductId;
	private String units;
	private String quantity;
	private String dishId;
	private String dishName;
	private String dishPrice;
	private String noOfItems;
	private String expiryDays;
	private String vat;
	private String sellingCostPerItem;
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public String getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public String getDishProductId() {
		return dishProductId;
	}
	public void setDishProductId(String dishProductId) {
		this.dishProductId = dishProductId;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
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
	public String getSellingCostPerItem() {
		return sellingCostPerItem;
	}
	public void setSellingCostPerItem(String sellingCostPerItem) {
		this.sellingCostPerItem = sellingCostPerItem;
	}
}
