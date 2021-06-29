package com.micaja.servipunto.database.dto;

public class PagoDocumentDTO implements DTO{
	private String name;
	private String type;
	private String minlength;
	private String maxlength;
	private String field;
	private String fieldvalidation;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMinlength() {
		return minlength;
	}
	public void setMinlength(String minlength) {
		this.minlength = minlength;
	}
	public String getMaxlength() {
		return maxlength;
	}
	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getFieldvalidation() {
		return fieldvalidation;
	}
	public void setFieldvalidation(String fieldvalidation) {
		this.fieldvalidation = fieldvalidation;
	}
	
}
