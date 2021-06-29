package com.micaja.servipunto.database.dto;

public class ConsultarCiudadesDTO implements DTO{
	private String depto_id;
	private String descripcion;
	public String getDepto_id() {
		return depto_id;
	}
	public void setDepto_id(String depto_id) {
		this.depto_id = depto_id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
