package com.micaja.servipunto.database.dto;

public class ConsultarLoteriasDTO implements DTO{
	private String loteName;
	private String loteID;
	private String loteNumberLong;
	private String loteSerieLong;
	private String loteFraction;
	private String valor;
	private String sorteo;
	private String dateClose;
	public String getLoteName() {
		return loteName;
	}
	public void setLoteName(String loteName) {
		this.loteName = loteName;
	}
	public String getLoteID() {
		return loteID;
	}
	public void setLoteID(String loteID) {
		this.loteID = loteID;
	}
	public String getLoteNumberLong() {
		return loteNumberLong;
	}
	public void setLoteNumberLong(String loteNumberLong) {
		this.loteNumberLong = loteNumberLong;
	}
	public String getLoteSerieLong() {
		return loteSerieLong;
	}
	public void setLoteSerieLong(String loteSerieLong) {
		this.loteSerieLong = loteSerieLong;
	}
	public String getLoteFraction() {
		return loteFraction;
	}
	public void setLoteFraction(String loteFraction) {
		this.loteFraction = loteFraction;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getSorteo() {
		return sorteo;
	}
	public void setSorteo(String sorteo) {
		this.sorteo = sorteo;
	}
	public String getDateClose() {
		return dateClose;
	}
	public void setDateClose(String dateClose) {
		this.dateClose = dateClose;
	}
	
}
