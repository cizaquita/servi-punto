package com.micaja.servipunto.utils;

public enum UserModuleCodes {
	SHOP_OPEN_CLOSE("1"), CLIENT("2"), SUPPLIERS("3"), INVENTORY("4"), CASH("5"), SALES(
			"6"), ORDERS("7"), REPORTS("8"), PROMOTIONS("9"), MENUS("10"), RECHARGE(
			"11"), Paquetigo("12");

	private String userModuleCode;

	private UserModuleCodes(String userModuleCode) {
		this.userModuleCode = userModuleCode;
	}

	public String code() {
		return userModuleCode;
	}
}
