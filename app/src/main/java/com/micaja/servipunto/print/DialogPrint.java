package com.micaja.servipunto.print;

import com.micaja.servipunto.ServiApplication;

public class DialogPrint extends PRTSDKApp {
	boolean flage;

	public boolean callPrint(String blutooth, String USB) {
		ServiApplication.print_flage_eps = USB;
		ServiApplication.print_flage = blutooth;
		try {
			return Allprints();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
