package com.micaja.servipunto.database.dto;

public class DishEditDTO implements DTO
{
	private String productId;
	private String productName;
	private String productbarCode;
	private String productPurchasePrice;
	private String productVat;
	private String productUOM;
	private String productQty;
	private String dishId;
	private String dishName;
	private String dishPrice;
	private String noOfItems;
	private String expiryDays;
	private String dishVat;
	private String sellingPricePerItem;
	private String dishProductID;
	
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
	public String getProductbarCode() {
		return productbarCode;
	}
	public void setProductbarCode(String productbarCode) {
		this.productbarCode = productbarCode;
	}
	public String getProductPurchasePrice() {
		return productPurchasePrice;
	}
	public void setProductPurchasePrice(String productPurchasePrice) {
		this.productPurchasePrice = productPurchasePrice;
	}
	public String getProductVat() {
		return productVat;
	}
	public void setProductVat(String productVat) {
		this.productVat = productVat;
	}
	public String getProductUOM() {
		return productUOM;
	}
	public void setProductUOM(String productUOM) {
		this.productUOM = productUOM;
	}
	public String getProductQty() {
		return productQty;
	}
	public void setProductQty(String productQty) {
		this.productQty = productQty;
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
	public String getDishVat() {
		return dishVat;
	}
	public void setDishVat(String dishVat) {
		this.dishVat = dishVat;
	}
	public String getSellingPricePerItem() {
		return sellingPricePerItem;
	}
	public void setSellingPricePerItem(String sellingPricePerItem) {
		this.sellingPricePerItem = sellingPricePerItem;
	}
	public String getDishProductID() {
		return dishProductID;
	}
	public void setDishProductID(String dishProductID) {
		this.dishProductID = dishProductID;
	}
}
