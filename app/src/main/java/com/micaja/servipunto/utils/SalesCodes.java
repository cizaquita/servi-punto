package com.micaja.servipunto.utils;

public enum SalesCodes {

	DISCOUNT(100), 
	EDIT(101), 
	DELETE(102),
	CLIENT_CODE(103),

	CLIENT_SALE_END(104),
	DISH_CREATE(105),
	PRODUCT_CODE(106),

	SELECT_DISH(200),
	SELECT_PRODUCT(201),
	ADD_MENUS_INVENTORY(202),
	INVOICE_ADJ(203),
	INVOICE_ADJ_CANCEL(204),
	SHOP_CLOSE_ACTUAL(205),
	ADD_INVENTORY(206),
	SUCESS_PRINT(999),
	ERROR(0),
	LEND_TO_DELIVERY(2),
	LEND_TO_CUSTOMER(1);


	
	private int salesCodes;
	
	private SalesCodes(int salesCodes)
	{
		this.salesCodes	= salesCodes;
	}
	
	public int code()
	{
		return salesCodes;
	}
}
