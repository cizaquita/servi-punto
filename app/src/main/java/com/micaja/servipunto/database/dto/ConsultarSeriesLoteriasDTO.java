package com.micaja.servipunto.database.dto;

public class ConsultarSeriesLoteriasDTO implements DTO{
	private String serie;
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getFractions() {
		return fractions;
	}
	public void setFractions(String fractions) {
		this.fractions = fractions;
	}
	private String fractions;
	private String comparison_string;
	public String getComparison_string() {
		return comparison_string;
	}
	public void setComparison_string(String comparison_string) {
		this.comparison_string = comparison_string;
	}
}
