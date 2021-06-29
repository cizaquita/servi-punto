package com.micaja.servipunto.utils;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public final class SalesPrint {
	public Context mContext;
	private String data;
	private String Result;

	private int leftDistance = 0;
	private int lineDistance = 0;
	private int wordFont = 1;// the old values is 1
	private int printGray = 3;
	private boolean flage;
	SharedPreferences sharedpreferences;

	public SalesPrint(Context context, String data) {
		this.mContext = context;
		this.data = data;
		sharedpreferences = mContext.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

	}

	public boolean print() {
		System.out.println(data);
		System.out.println("====================>"+sharedpreferences.getString("Allin_one_device_other_printers", ""));

		if (sharedpreferences.getString("Allin_one_device_other_printers", "").contains("Other")) {
			flage = false;
		} else {
			try {
				CommonMethods.showProgressDialog(mContext.getString(R.string.please_wait), mContext);
				flage = new contentPrintThread().execute().get();
			} catch (Exception e) {
				flage = false;
			}
			
			System.out.println("====================>"+"Babu Test");
		}
		CommonMethods.dismissProgressDialog();
		return flage;
	}

	private class contentPrintThread extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ThermalPrinter.start();
				ThermalPrinter.reset();
				// if (ServiApplication.flage_for_log_print) {
				// Bitmap icon =
				// BitmapFactory.decodeResource(mContext.getResources(),
				// R.drawable.print_logo);
				// ThermalPrinter.printLogo(icon);
				// ThermalPrinter.reset();
				// }
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
				ThermalPrinter.setLeftIndent(leftDistance);
				ThermalPrinter.setLineSpace(lineDistance);
				if (wordFont == 3) {
					ThermalPrinter.setFontSize(4);
					ThermalPrinter.enlargeFontSize(4, 4);
				} else {
					ThermalPrinter.setFontSize(wordFont);
				}
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.addString(data);
				ThermalPrinter.printString();
				ThermalPrinter.clearString();
				ThermalPrinter.walkPaper(100);
				ThermalPrinter.stop();
				CommonMethods.dismissProgressDialog();
				return true;
			} catch (TelpoException e) {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

	public void ShowErrorMessage(String string) {
		CommonMethods.showCustomToast(mContext, string);
	}
}
