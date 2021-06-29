/**
 * 
 */
package com.micaja.servipunto.database.dto;

public class OrderDetailsDTO implements DTO {
 
	private String orderDetailsId;
	private String orderId;
	private String productId;
	private String count;
	private String uom;
	private String price;
	private Integer syncStatus;
 
	public String getOrderDetailsId() {
		return orderDetailsId;
	}
	public void setOrderDetailsId(String orderDetailsId) {
		this.orderDetailsId = orderDetailsId;
	 }
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	 }
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	 }
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	 }
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	 }
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	 }
	public int getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	 }
}

