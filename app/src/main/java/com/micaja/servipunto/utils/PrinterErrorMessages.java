package com.micaja.servipunto.utils;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;

public  final class PrinterErrorMessages {

	static int result_usb, result_wifi, result_blooth;
	static ESCPOSPrinter printer = new ESCPOSPrinter();
	static UsbDevice usbdevice = null;

	public static String printerErrorMsg(Context context) {
		
		printer.setContext(context);
		result_usb = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		if (result_usb == ESCPOSConst.CMP_SUCCESS) {
		} else if (result_usb == 1001) {
			return context.getResources().getString(R.string.p_msg_1001);
		} else if (result_usb == 1002) {
			return context.getResources().getString(R.string.p_msg_1002);
		} else if (result_usb == 1003) {
			return context.getResources().getString(R.string.p_msg_1003);
		} else if (result_usb == 1004) {
			return context.getResources().getString(R.string.p_msg_1004);
		} else if (result_usb == 1005) {
			return context.getResources().getString(R.string.p_msg_1005);
		} else if (result_usb == 1006) {
			return context.getResources().getString(R.string.p_msg_1006);
		} else if (result_usb == 1007) {
			return context.getResources().getString(R.string.p_msg_1007);
		} else if (result_usb == 1008) {
			return context.getResources().getString(R.string.p_msg_1008);
		} else if (result_usb == 1101) {
			return context.getResources().getString(R.string.p_msg_1101);
		} else if (result_usb == 1102) {
			return context.getResources().getString(R.string.p_msg_1102);
		} else if (result_usb == 1103) {
			return context.getResources().getString(R.string.p_msg_1103);
		} else if (result_usb == 1104) {
			return context.getResources().getString(R.string.p_msg_1104);
		} else if (result_usb == 1105) {
			return context.getResources().getString(R.string.p_msg_1105);
		} else if (result_usb == 1106) {
			return context.getResources().getString(R.string.p_msg_1106);
		} else if (result_usb == 1201) {
			return context.getResources().getString(R.string.p_msg_1201);
		} else if (result_usb == 1202) {
			return context.getResources().getString(R.string.p_msg_1202);
		} else if (result_usb == 1203) {
			return context.getResources().getString(R.string.p_msg_1203);
		} else if (result_usb == 1204) {
			return context.getResources().getString(R.string.p_msg_1204);
		}
		
		return null;
	}
	
	public static boolean printerStatus(Context context){
		
		printer.setContext(context);
		result_usb = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		
		if (result_usb == ESCPOSConst.CMP_SUCCESS) {
			return true;
		}else {
			return false;
		}
		
	}

}
