package com.micaja.servipunto.service;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.utils.CommonMethods;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.IBinder;

public class OpenCashDrawer extends Service{
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new OpneDrawer().execute();
	}
	
	public class OpneDrawer extends AsyncTask<Void, Void, Void> {

		public boolean falge=false;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ESCPOSPrinter printer = new ESCPOSPrinter();
			UsbDevice usbdevice = null;
			printer.setContext(OpenCashDrawer.this);
			int result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);

			if (ESCPOSConst.CMP_SUCCESS == result) {
				printer.printText("", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
						ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
				printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
				printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
				printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
				result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);//here paper will come out from printer with hello world
				//Drawer will open after print the paper
				printer.openDrawer(ESCPOSConst.CMP_DRAWER_1, 1);
				printer.disconnect();
				
				falge=true;
			} else {
				falge=false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (falge) {
				CommonMethods.showToast(OpenCashDrawer.this, "Drawer was connected");
			}else{
				CommonMethods.showToast(OpenCashDrawer.this, "Drawer was not connected...Please check Drawer conncetion..!");
			}
		}
	}
}
