package com.micaja.servipunto.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dao.SalesDAO;
import com.micaja.servipunto.database.dao.SalesDetailDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.SalesDTO;
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.UpdateSynkStatus;

public class SalesDetailsUpdateService extends IntentService {
	private UserDetailsDTO udto;
	private SalesDTO salesdto;
	public SalesDetailsUpdateService() {
		super("SalesDetailsUpdateService");
		
		List<DTO> saledto_list = SalesDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),"sale_id",ServiApplication.sales_id);
		SalesDTO saledto = null ;
		for (DTO dto : saledto_list) {
			saledto = (SalesDTO) dto;
		}
		this.salesdto=saledto;
		Log.v("Varahalababu", "Hello BABU=========Utils.saleID>>>"+ServiApplication.sales_id);
		Log.v("Varahalababu", "Hello BABU=========>"+salesdto.getSaleID());
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),ServiApplication.userName);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	class CickServer extends AsyncTask<String, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(
				SalesDetailsUpdateService.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			List<DTO> inventorylist = InventoryDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
			List<DTO> inventoryHistorylist = InventoryHistoryDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
			List<DTO> clientList = ClientDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");

			List<DTO> deliveryList = DeliveryDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
			List<DTO> menuInventorylist = MenuInventoryDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
			List<DTO> clientpaymentDetails = ClientPaymentDAO.getInstance()
					.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");

			List<DTO> deliverypaymentDetails = DeliveryPaymentDAO.getInstance()
					.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");

			List<DTO> sales = SalesDAO.getInstance()
					.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
			
			List<DTO> sales_details = SalesDetailDAO.getInstance()
					.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
							"sync_status", "0");
			
			if (sales.size() > 0) {
					String val_sales = servicehandler
							.makeServiceCall(ServiApplication.URL + "/sync",
									ServiceHandler.POST,new RequestFiels(
											SalesDetailsUpdateService.this)
											.getSalesData(sales));
					Log.v("Varahalababu", "Hello BABU=========>Req"+new RequestFiels(
							SalesDetailsUpdateService.this)
							.getSalesData(sales));
					Log.v("Varahalababu", "Hello BABU=========>Res        "+val_sales);
					if (new JSONStatus().status(val_sales) == 0) {
						new UpdateSynkStatus(sales,ServiApplication.sales);
					}
			}
			
			if (sales_details.size() > 0) {
				String val_sales = servicehandler
						.makeServiceCall(ServiApplication.URL + "/sync",
								ServiceHandler.POST, new RequestFiels(
										SalesDetailsUpdateService.this)
										.getSalesDetailsData(sales_details));
				
				Log.v("Varahalababu", "Hello BABU=========>Req"+new RequestFiels(
						SalesDetailsUpdateService.this)
						.getSalesDetailsData(sales_details));
				Log.v("Varahalababu", "Hello BABU=========>Res        "+val_sales);
				if (new JSONStatus().status(val_sales) == 0) {
					new UpdateSynkStatus(sales_details,ServiApplication.salesDetails);
				}
		}

			if (menuInventorylist.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_menuInventorylist = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(
											SalesDetailsUpdateService.this)
											.getMenusInventoryTableData(menuInventorylist));
					if (new JSONStatus().status(val_menuInventorylist) == 0) {
						new UpdateSynkStatus(menuInventorylist,ServiApplication.menuInventory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (inventoryHistorylist.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_inventoryHistorylist = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(
											SalesDetailsUpdateService.this)
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
						.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_inventorylist = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									SalesDetailsUpdateService.this)
									.getInventoryTableData(inventorylist));
					if (new JSONStatus().status(val_inventorylist) == 0) {
						HashMap<String,Double> inventoryServer= new JSONStatus().data(val_inventorylist);

							for(DTO dto: inventorylist){
								InventoryDTO inventoryDTO = (InventoryDTO) dto;
								inventoryDTO.setQuantityBalance(0.0);
								if (inventoryServer.containsKey(inventoryDTO.getProductId()))
									inventoryDTO.setQuantity(inventoryServer.get(inventoryDTO.getProductId()).toString());
								InventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), inventoryDTO);
							}
						new UpdateSynkStatus(inventorylist,
								ServiApplication.inventory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (clientList.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_clientList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									SalesDetailsUpdateService.this)
									.getClientTableData(clientList));
					if (new JSONStatus().status(val_clientList) == 0) {
						new UpdateSynkStatus(clientList,
								ServiApplication.Client_Info);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}
			
			if (clientpaymentDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_clientpaymentDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(SalesDetailsUpdateService.this)
											.getPaymentDetailsData(clientpaymentDetails));
					if (new JSONStatus().status(val_clientpaymentDetails) == 0) {
						new UpdateSynkStatus(clientpaymentDetails,
								ServiApplication.clientpayments);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (deliveryList.size() > 0) {
				if (CommonMethods
						.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_deliveryList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									SalesDetailsUpdateService.this)
									.getDeliveryTableData(deliveryList));
					if (new JSONStatus().status(val_deliveryList) == 0) {
						new UpdateSynkStatus(deliveryList,
								ServiApplication.Delivery_Info);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (deliverypaymentDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(SalesDetailsUpdateService.this) >= ServiApplication.local_data_speed) {
					String val_deliverypaymentDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(SalesDetailsUpdateService.this)
											.getPaymentDeliveryDetailsData(deliverypaymentDetails));
					if (new JSONStatus().status(val_deliverypaymentDetails) == 0) {
						new UpdateSynkStatus(deliverypaymentDetails,
								ServiApplication.deliveryPayments);
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
			stopSelf();
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (NetworkConnectivity
				.netWorkAvailability(SalesDetailsUpdateService.this)) {
			new CickServer().execute("" + ServiApplication.promotion_id);
		} else {

		}
		
		
	}
	

	

}
