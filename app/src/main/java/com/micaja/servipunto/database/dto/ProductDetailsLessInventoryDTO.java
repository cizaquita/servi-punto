package com.micaja.servipunto.database.dto;

import java.util.Date;

public class ProductDetailsLessInventoryDTO implements
		Comparable<ProductDetailsLessInventoryDTO> {

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getUtilityValue() {
		return utilityValue;
	}

	public void setUtilityValue(String utilityValue) {
		this.utilityValue = utilityValue;
	}

	public String getVat() {
		return vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Date getExp_date() {
		return exp_date;
	}

	public void setExp_date(Date exp_date) {
		this.exp_date = exp_date;
	}

	private String productCode;
	private String name;
	private String supplierName;
	private Double quantity;
	private String sellingPrice;
	private String purchasePrice;
	private String utilityValue;
	private String vat;
	private String uom;
	private Date exp_date;

	public ProductDetailsLessInventoryDTO(String productCode, String name,
			String supplierName, Double quantity, String sellingPrice,
			String purchasePrice, String utilityValue, String vat, String uom,
			Date exp_date) {
		this.name = name;
		this.supplierName = supplierName;
		this.exp_date = exp_date;
		this.quantity = quantity;
		this.sellingPrice = sellingPrice;
		this.purchasePrice = purchasePrice;
		this.utilityValue = utilityValue;
		this.vat = vat;
		this.uom = uom;
	}


	@Override
	public int compareTo(ProductDetailsLessInventoryDTO another) {
		return another.quantity.compareTo(this.quantity);
	}
}
