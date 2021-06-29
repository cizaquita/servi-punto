package com.micaja.servipunto.database.dto;

public class ComisionDTO implements DTO{
	private String comision;
	private String codigoconvenio;
	private String tipodocumento;
	private String documento;
	private String nombre;
	private String value;
	private String total;
	
	public String getComision() {
		return comision;
	}
	public void setComision(String comision) {
		this.comision = comision;
	}
	public String getCodigoconvenio() {
		return codigoconvenio;
	}
	public void setCodigoconvenio(String codigoconvenio) {
		this.codigoconvenio = codigoconvenio;
	}
	public String getTipodocumento() {
		return tipodocumento;
	}
	public void setTipodocumento(String tipodocumento) {
		this.tipodocumento = tipodocumento;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
}
