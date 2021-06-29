/**
 * 
 */
package com.micaja.servipunto.database.dto;
 
public class DishProductsDTO implements DTO {
 
	private String dishProductId;
	private String dishId;
	private String productId;
	private String uom;
	private String quantity;
	private Integer syncStatus;
 
	public String getDishProductId() {
		return dishProductId;
	}
	public void setDishProductId(String dishProductId) {
		this.dishProductId = dishProductId;
	 }
	public String getDishId() {
		return dishId;
	}
	public void setDishId(String dishId) {
		this.dishId = dishId;
	 }
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	 }
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	 }
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

