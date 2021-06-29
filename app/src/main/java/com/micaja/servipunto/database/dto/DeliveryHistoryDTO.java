package com.micaja.servipunto.database.dto;

import java.util.Date;

public class DeliveryHistoryDTO implements Comparable<DeliveryHistoryDTO>{
	private String paymentType;
	private String pay_amount;
	private String deb_amount;
	private Date dateTime;
	private String sale_id;

	public DeliveryHistoryDTO(String paymentType, String pay_amount, String deb_amount, Date dateTime, String sale_id) {
		this.paymentType = paymentType;
		this.pay_amount = pay_amount;
		this.deb_amount = deb_amount;
		this.dateTime=dateTime;
		this.sale_id=sale_id;
	}
	public String getSale_id() {
		return sale_id;
	}
	public void setSale_id(String sale_id) {
		this.sale_id = sale_id;
	}
	
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPay_amount() {
		return pay_amount;
	}
	public void setPay_amount(String pay_amount) {
		this.pay_amount = pay_amount;
	}
	public String getDeb_amount() {
		return deb_amount;
	}
	public void setDeb_amount(String deb_amount) {
		this.deb_amount = deb_amount;
	}

	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	
	@Override
	public int compareTo(DeliveryHistoryDTO o) {
		return o.dateTime.compareTo(this.dateTime);
	}
	
}
