package com.micaja.servipunto.database.dto;

public class CellularProviderDto implements DTO {

	private String cellularId;
	private String description;
	private String min_amount;
	private String max_amount;
	private String multiplier;
	private String productName;
	private String productNumber;
	private boolean printable;
	private String valor;
	

	public String getCellularId() {
		return cellularId;
	}

	public void setCellularId(String cellularId) {
		this.cellularId = cellularId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMinAmount() {
		return min_amount;
	}

	public void setMinAmount(String min_amount) {
		this.min_amount = min_amount;
	}

	public String getMaxAmount() {
		return max_amount;
	}

	public void setMaxAmount(String multiplier) {
		this.max_amount = multiplier;
	}

	public String getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(String multiplier) {
		this.multiplier = multiplier;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public boolean getPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
