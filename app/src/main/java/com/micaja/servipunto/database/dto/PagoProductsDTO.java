package com.micaja.servipunto.database.dto;

public class PagoProductsDTO implements DTO{
	private String prodCode;
	private  String prodDesc;
	private  String docRequired;
	private  String attempts;
	private  String otp;
	private  String valuetype;
	private  String validility;
	private  String sms;
	private  String email;
	private  String emailTxt;
	private  String ticket;
	private  String autorizer;
	
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getProdDesc() {
		return prodDesc;
	}
	public void setProdDesc(String prodDesc) {
		this.prodDesc = prodDesc;
	}
	public String getDocRequired() {
		return docRequired;
	}
	public void setDocRequired(String docRequired) {
		this.docRequired = docRequired;
	}
	public String getAttempts() {
		return attempts;
	}
	public void setAttempts(String attempts) {
		this.attempts = attempts;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getValuetype() {
		return valuetype;
	}
	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}
	public String getValidility() {
		return validility;
	}
	public void setValidility(String validility) {
		this.validility = validility;
	}
	public String getSms() {
		return sms;
	}
	public void setSms(String sms) {
		this.sms = sms;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmailTxt() {
		return emailTxt;
	}
	public void setEmailTxt(String emailTxt) {
		this.emailTxt = emailTxt;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getAutorizer() {
		return autorizer;
	}
	public void setAutorizer(String autorizer) {
		this.autorizer = autorizer;
	}
	
	

}
