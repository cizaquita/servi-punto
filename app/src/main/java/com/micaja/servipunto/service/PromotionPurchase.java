package com.micaja.servipunto.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class PromotionPurchase extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (NetworkConnectivity.netWorkAvailability(PromotionPurchase.this)) {
			new CickServer().execute(""+ServiApplication.promotion_id);
		} else {
			
		}
	}

	class CickServer extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			new ServiceHandler(PromotionPurchase.this).makeServiceCall(ServiApplication.URL
					+ "/promotion/" + params[0] + "/purchase",
					ServiceHandler.POST, null);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			stopSelf();
		}

	}
}
