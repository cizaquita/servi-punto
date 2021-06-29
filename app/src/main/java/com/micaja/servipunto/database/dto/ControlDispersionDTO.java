package com.micaja.servipunto.database.dto;

public class ControlDispersionDTO implements DTO{
	private String valor;
	private String date;
	private String tipo;
	private String centroacopioId;
	private String centroacopio_name;
	public String getCentroacopio_name() {
		return centroacopio_name;
	}
	public void setCentroacopio_name(String centroacopio_name) {
		this.centroacopio_name = centroacopio_name;
	}
	private String programacionId;
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCentroacopioId() {
		return centroacopioId;
	}
	public void setCentroacopioId(String centroacopioId) {
		this.centroacopioId = centroacopioId;
	}
	public String getProgramacionId() {
		return programacionId;
	}
	public void setProgramacionId(String programacionId) {
		this.programacionId = programacionId;
	}
	
}
