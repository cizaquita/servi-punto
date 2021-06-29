/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dto;

public class InventoryAdjustmentDTO implements DTO 
{
	private String adjustmentId;
	private String productId;
	private String damageTypeId;
	private String quantity;
	private String uom;
	private Integer syncStatus;
	
	
	
	public String getAdjustmentId() {
		return adjustmentId;
	}
	public void setAdjustmentId(String adjustmentId) {
		this.adjustmentId = adjustmentId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getDamageTypeId() {
		return damageTypeId;
	}
	public void setDamageTypeId(String damageTypeId) {
		this.damageTypeId = damageTypeId;
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
	
	public Integer getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(Integer syncStatus) {
		this.syncStatus = syncStatus;
	}
	
}
