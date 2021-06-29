/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database.dto;

public class DamageTypeDTO implements DTO
{
	private String damageTypeId;
	private String name;
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDamageTypeId() {
		return damageTypeId;
	}

	public void setDamageTypeId(String damageTypeId) {
		this.damageTypeId = damageTypeId;
	}
}
