package com.micaja.servipunto.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.RequestFiels;

public class TransaccionBoxService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (com.micaja.servipunto.utils.NetworkConnectivity
				.netWorkAvailability(TransaccionBoxService.this)) {
			if (com.micaja.servipunto.utils.CommonMethods
					.getInternetSpeed(TransaccionBoxService.this) >= ServiApplication.local_data_speed) {
				new TransaccionBox().execute("");
			} else {
				// CommonMethods.showCustomToast(
				// TransaccionBoxService.this,
				// TransaccionBoxService.this.getResources().getString(
				// R.string.no_wifi_adhoc));
			}

		} else {
			// CommonMethods.showCustomToast(
			// TransaccionBoxService.this,
			// TransaccionBoxService.this.getResources().getString(
			// R.string.no_wifi_adhoc));
		}
	}

	class TransaccionBox extends AsyncTask<String, Void, Void> {
		List<DTO> transaccionBox;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			transaccionBox = TransaccionBoxDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
		}

		@Override
		protected Void doInBackground(String... params) {
			ServiApplication.responds_feed = new ServiceHandler(
					TransaccionBoxService.this).makeServiceCall(
					ServiApplication.URL + "/sync", ServiceHandler.POST,
					new RequestFiels(TransaccionBoxService.this)
							.TransaccionBox(transaccionBox));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			stopSelf();
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				TransaccionBoxDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.READABLE));
			}
		}
	}
}
