package com.micaja.servipunto.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;

public class SupplierUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (NetworkConnectivity.netWorkAvailability(SupplierUpdateService.this)) {
			new CickServer().execute("" + ServiApplication.promotion_id);
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
			new ServiceHandler(SupplierUpdateService.this).makeServiceCall(
					ServiApplication.URL + "/sync", ServiceHandler.POST,
					new RequestFiels(SupplierUpdateService.this)
							.addsupplier(ServiApplication.setSupplier));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			List<DTO> sDTO = ServiApplication.setSupplier;
			SupplierDTO SDTO = (SupplierDTO) sDTO.get(0);
			SupplierDAO.getInstance().changesynckstatus(
					DBHandler.getDBObj(Constants.WRITABLE), SDTO);
			stopSelf();
		}

	}
}
