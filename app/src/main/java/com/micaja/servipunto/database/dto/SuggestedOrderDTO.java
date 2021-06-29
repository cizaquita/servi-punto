package com.micaja.servipunto.database.dto;

public class SuggestedOrderDTO implements DTO
{
	String quantity;
	String uom;
	String name;
	String purchasePrice;
	String min_count_inventory;
	public String getMin_count_inventory() {
		return min_count_inventory;
	}
	public void setMin_count_inventory(String min_count_inventory) {
		this.min_count_inventory = min_count_inventory;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
}
