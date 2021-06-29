package com.micaja.servipunto.database.dto;

import java.util.Date;

public class ClientHistoryDTO implements Comparable<ClientHistoryDTO>{
	private String paymentType;
	private String amount;
	private Date dateTime;
	private String sale_id;
	
	public ClientHistoryDTO(String paymentType, String amount, Date dateTime,String sale_id) {
		this.paymentType = paymentType;
		this.amount = amount;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	@Override
	public int compareTo(ClientHistoryDTO o) {
		return o.dateTime.compareTo(this.dateTime);
	}
	
}
