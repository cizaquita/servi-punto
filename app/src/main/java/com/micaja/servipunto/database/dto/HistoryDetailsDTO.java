/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dto;

public class HistoryDetailsDTO implements DTO
{
	private String date;
	private String initialDebt;
	private String pay;
	private String balance;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getInitialDebt() {
		return initialDebt;
	}
	public void setInitialDebt(String initialDebt) {
		this.initialDebt = initialDebt;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	
	
}
