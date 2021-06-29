package com.micaja.servipunto.database.dto;

public class InventoryAmountDTO implements DTO {
	private String product_name;
	private String purchase;
	private String selling;
	private String debt_days;
	private String product;
	private String quantity;
	private String sumatoria;

	private String id_transaccion;
	private String fecha_hora;
	private String tipo;
	private String valor;
	private String estado;

	public String getInventoryName() {
		return product_name;
	}

	public void setInventoryName(String inventoryName) {
		this.product_name = inventoryName;
	}

	public String getpurchase() {
		return purchase;
	}

	public void setpurchases(String purchase) {
		this.purchase = purchase;
	}

	public String getselling() {
		return selling;
	}

	public void setselling(String selling) {
		this.selling = selling;
	}

	public String getdebt_days() {
		return debt_days;
	}

	public void setdebt_days(String debt_days) {
		this.debt_days = debt_days;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getSumatoria() {
		return sumatoria;
	}

	public void setSumatoria(String sumatoria) {
		this.sumatoria = sumatoria;
	}

	public String getIdTransaccion() {
		return id_transaccion;
	}

	public void setIdTransaccion(String id_transaccion) {
		this.id_transaccion = id_transaccion;
	}

	public String getFechaHora() {
		return fecha_hora;
	}

	public void setFechaHora(String fecha_hora) {
		this.fecha_hora = fecha_hora;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	

}
