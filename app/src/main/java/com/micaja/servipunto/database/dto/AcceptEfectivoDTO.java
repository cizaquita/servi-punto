package com.micaja.servipunto.database.dto;

public class AcceptEfectivoDTO implements DTO {

	private String password;
	private String amount;
	private String diskey;
	private String distribuidor;
	private String idCentroAcopio;
	private String valor;
	private String clave;

	public String getIdCentroAcopio() {
		return idCentroAcopio;
	}

	public void setIdCentroAcopio(String idCentroAcopio) {
		this.idCentroAcopio = idCentroAcopio;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getDiskey() {
		return diskey;
	}

	public void setDiskey(String diskey) {
		this.diskey = diskey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDistribuidor() {
		return distribuidor;
	}

	public void setDistribuidor(String distribuidor) {
		this.distribuidor = distribuidor;
	}

}
