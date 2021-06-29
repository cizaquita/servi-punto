package com.micaja.servipunto.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.SupplierPaymentsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.UpdateSynkStatus;

public class InventoryDbSynckService extends Service {
	final Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		callAutoSyncMethodService();
	}

	public void callAutoSyncMethodService() {
		if (NetworkConnectivity
				.netWorkAvailability(InventoryDbSynckService.this)) {
			new AutoSyncServer().execute();
		} else {
		}
	}

	class AutoSyncServer extends AsyncTask<Void, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(
				InventoryDbSynckService.this);

		List<DTO> inventorylist = InventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> inventoryHistorylist = InventoryHistoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> supplierDetails = SupplierDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> supplierPaymentsDetails = SupplierPaymentsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> productDetails = ProductDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (productDetails.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(InventoryDbSynckService.this) >= ServiApplication.local_data_speed) {
					String val_productDetails = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									InventoryDbSynckService.this)
									.getProductTableData(productDetails));

					if (new JSONStatus().status(val_productDetails) == 0) {
						new UpdateSynkStatus(productDetails,
								ServiApplication.updateProducts);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (supplierPaymentsDetails.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(InventoryDbSynckService.this) >= ServiApplication.local_data_speed) {
					String val_supplierpayments = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(
											InventoryDbSynckService.this)
											.getSupplierPayTableData(supplierPaymentsDetails));

					if (new JSONStatus().status(val_supplierpayments) == 0) {
						new UpdateSynkStatus(supplierPaymentsDetails,
								ServiApplication.supplierpayments);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}
			if (supplierDetails.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(InventoryDbSynckService.this) >= ServiApplication.local_data_speed) {
					String val_supplierDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(
											InventoryDbSynckService.this)
											.getSuppliersDetailsData(supplierDetails));

					if (new JSONStatus().status(val_supplierDetails) == 0) {
						new UpdateSynkStatus(supplierDetails,
								ServiApplication.supplier);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}

			}

			if (inventoryHistorylist.size() > 0) {
				if (true) {
					String val_inventoryHistorylist = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(
											InventoryDbSynckService.this)
											.getInventoryHistoryTableData(inventoryHistorylist));
					if (new JSONStatus().status(val_inventoryHistorylist) == 0) {
						new UpdateSynkStatus(inventoryHistorylist,
								ServiApplication.inventoryHistory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (inventorylist.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(InventoryDbSynckService.this) >= ServiApplication.local_data_speed) {
					String val_inventorylist = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									InventoryDbSynckService.this)
									.getInventoryTableData(inventorylist));
					if (new JSONStatus().status(val_inventorylist) == 0) {
						new UpdateSynkStatus(inventorylist,
								ServiApplication.inventory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			inventorylist.clear();
			inventoryHistorylist.clear();
			supplierDetails.clear();
			supplierPaymentsDetails.clear();
			productDetails.clear();
			stopSelf();
		}
	}
}
