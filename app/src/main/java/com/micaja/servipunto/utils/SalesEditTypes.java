package com.micaja.servipunto.utils;

public enum SalesEditTypes {

	PRICE("Price"), 
	QUANTITY("Quantity"), 
	UNITS("units"),
	CLIENT_SELECTION("Client Selection"),
	DISH_SELECTION("Dish Selection"),
	ORDERS_PRODUCT("Orders Product"),
	INVOICE_ADJUSTMENT("Invoice Adjustment"),
	MENUS_INVENTORY("Menus Inventory");
	
	
	private String salesEditTypes;
	
	private SalesEditTypes(String salesEditTypes)
	{
		this.salesEditTypes	= salesEditTypes;
	}
	
	public String code()
	{
		return salesEditTypes;
	}
}
