/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dto;

import java.util.Comparator;

public class ClientDTO implements DTO //, Comparator<ClientDTO>
{
	private String clientID;
	private String name;
	private String cedula;
	private String address;
	private String email;
	
	private String gender;
	private Integer activeStatus;
	private String initialDebt;

	private String balanceAmount;
	private String lastPaymentDate;
	private String createdDate;
	private String modifiedDate;
	private Integer syncStatus;
	
	private String telephone;
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getInitialDebt() {
		return initialDebt;
	}
	public void setInitialDebt(String initialDebt) {
		this.initialDebt = initialDebt;
	}
	public Integer getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Integer activeStatus) {
		this.activeStatus = activeStatus;
	}
	public String getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public String getLastPaymentDate() {
		return lastPaymentDate;
	}
	public void setLastPaymentDate(String lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Integer getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(Integer syncStatus) {
		this.syncStatus = syncStatus;
	}

	/*
	// Los nombres de los clientes se ordenan desde el sql, si los registros retornados no son los deseados,
	// se descomentan estas lineas para que la clase implemente la interfaz Comparator
	@Override
	public int compare(ClientDTO object1, ClientDTO object2) {
		return object1.getName().compareTo(object2.getName());
	}
	*/
}
