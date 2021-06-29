package com.micaja.servipunto.database.dto;

public class ConsultarFacturasDTO implements DTO{
	private String comercioId;
	private String idFactura;
	private Long saldoInicial;
	private Long valorCausado;
	private Long aceptaciones;
	private Long devoluciones;
	private Long pagos;
	private Long comisiones;
	private Long totalCorte;
	private Long abonos;
	private Long creditos;
	private Long debitos;
	private Long otrosCargos;
	private Long saldoTotal;
	private Double ventas;
	public String getComercioId() {
		return comercioId;
	}
	public void setComercioId(String comercioId) {
		this.comercioId = comercioId;
	}
	public String getIdFactura() {
		return idFactura;
	}
	public void setIdFactura(String idFactura) {
		this.idFactura = idFactura;
	}
	public Long getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(Long saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public Long getValorCausado() {
		return valorCausado;
	}
	public void setValorCausado(Long valorCausado) {
		this.valorCausado = valorCausado;
	}
	public Long getAceptaciones() {
		return aceptaciones;
	}
	public void setAceptaciones(Long aceptaciones) {
		this.aceptaciones = aceptaciones;
	}
	public Long getDevoluciones() {
		return devoluciones;
	}
	public void setDevoluciones(Long devoluciones) {
		this.devoluciones = devoluciones;
	}
	public Long getPagos() {
		return pagos;
	}
	public void setPagos(Long pagos) {
		this.pagos = pagos;
	}
	public Long getComisiones() {
		return comisiones;
	}
	public void setComisiones(Long comisiones) {
		this.comisiones = comisiones;
	}
	public Long getTotalCorte() {
		return totalCorte;
	}
	public void setTotalCorte(Long totalCorte) {
		this.totalCorte = totalCorte;
	}
	public Long getAbonos() {
		return abonos;
	}
	public void setAbonos(Long abonos) {
		this.abonos = abonos;
	}
	public Long getCreditos() {
		return creditos;
	}
	public void setCreditos(Long creditos) {
		this.creditos = creditos;
	}
	public Long getDebitos() {
		return debitos;
	}
	public void setDebitos(Long debitos) {
		this.debitos = debitos;
	}
	public Long getOtrosCargos() {
		return otrosCargos;
	}
	public void setOtrosCargos(Long otrosCargos) {
		this.otrosCargos = otrosCargos;
	}
	public Long getSaldoTotal() {
		return saldoTotal;
	}
	public void setSaldoTotal(Long saldoTotal) {
		this.saldoTotal = saldoTotal;
	}
	public Double getVentas() {
		return ventas;
	}
	public void setVentas(Double ventas) {
		this.ventas = ventas;
	}
	
}
