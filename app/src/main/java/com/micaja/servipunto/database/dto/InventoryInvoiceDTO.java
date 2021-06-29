package com.micaja.servipunto.database.dto;

import java.util.List;

public class InventoryInvoiceDTO implements DTO
{
	private String supplierName;
	private String invoiceNum;
	private String totalAmount;
	private List<DTO> productsList;
	
	
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getInvoiceNum() {
		return invoiceNum;
	}
	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<DTO> getProductsList() {
		return productsList;
	}
	public void setProductsList(List<DTO> productsList) {
		this.productsList = productsList;
	}
}
