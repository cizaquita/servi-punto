package com.micaja.servipunto.utils;

public enum ConstantsEnum {

	ADD_CLIENT("add client"), EDIT_CLIENT("edit client"), CLIENT_MODE(
			"client mode"), CLIENT_ID("Client id"), CLIENT_NAME("Client name"), CLIENT_TELEPHONE(
			"Client number"), ADD_SUPPLIER("Add Supplier"), EDIT_SUPPLIER(
			"Edit Supplier"), SUPPLIER_MODE("Supplier mode"), SUPPLIER_ID(
			"Supplier id"), INVENTORY("inventory"), CASH_DEPOSIT("Cash Deposit"), CASH_WITHDRAW(
			"Cash Withdraw"), PAYMENT_TYPE_CASH("Cash"), PAYMENT_TYPE_CARD(
			"card"), PAYMENT_TYPE_CREDIT("credit"), INCOME_TYPE_SALE("sale"), INCOME_TYPE_DEBIT(
			"debit"), CLIENT_PAY("clientpay"), CLIENT_LEND("lendmoney"), SUPPLIER_PAY(
			"suppay"), SUPPLIER_PURCHASE_TYPE("Supplier Debit"), CASH_TYPE_WITHDRAW(
			"cash withdraw"), CASH_TYPE_DEPOSIT("cash Deposit"), CLIENT_CEDULA(
			"cedula"), SUPPLIER_PAYMENT("Supp  Expenses"), CASH_SHOP_OPEN(
			"shop open cash"),ORDERS("Orders"),EDIT_PRODUCT("edit product"),ADD_PRODUCT("add product"),PRODUCT_MODE("edit product"),PRODUCT_ID("product id"),PRODUCT_SUPPLIER("product supplier"),
	SALES("Sales"),WEEKLY("Weekly"),OCCASIONALLY("Occasionally"),DAILY("Daily"),DISH("Dish"),
	EDIT_DISH("Edit Dish"),VIEW_MENUS("View Menus"),VIEW_DISH("View Dish"),SELECTION("Selectio"),VIEW("View"),PRODUCT_DISH("product dish"),PRODUCT_NON_DISH("product non dish"),INVOICE("invoice"),
	DELIVERY_LEND("lend_delivery"),
	DELIVERY_PAYMENT("payment_delivery"),
	PAY_DELIVERY("payment_do_delivery"),
	DELIVERY_PAY_INVOICE("debt_delivery_invoice");
	
	private String name;

	private ConstantsEnum(String name) {
		this.name = name;
	}

	public String code() {
		return name;
	}
}



