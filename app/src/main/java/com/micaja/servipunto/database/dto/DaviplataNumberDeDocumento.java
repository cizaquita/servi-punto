package com.micaja.servipunto.database.dto;

public class DaviplataNumberDeDocumento implements DTO {
	private String celular;
	private String cedula;
	private String nombres;
	private String s_nombres;
	private String apellidos;
	private String s_apellidos;

	private String direccion;
	private String ciudad;
	private String documento;
	private String fechaExpedicion;

	public String getFechaExpedicion() {
		return fechaExpedicion;
	}

	public void setFechaExpedicion(String fechaExpedicion) {
		this.fechaExpedicion = fechaExpedicion;
	}

	public String getS_apellidos() {
		return s_apellidos;
	}

	public void setS_apellidos(String s_apellidos) {
		this.s_apellidos = s_apellidos;
	}

	public String getS_nombres() {
		return s_nombres;
	}

	public void setS_nombres(String s_nombres) {
		this.s_nombres = s_nombres;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

}
