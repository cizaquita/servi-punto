/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.LoginActivity;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.CashFlowDAO;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.InventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.LendDeliveryDAO;
import com.micaja.servipunto.database.dao.LendMoneyDAO;
import com.micaja.servipunto.database.dao.MenuDAO;
import com.micaja.servipunto.database.dao.MenuDishesDAO;
import com.micaja.servipunto.database.dao.MenuInventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dao.OrderDetailsDAO;
import com.micaja.servipunto.database.dao.OrdersDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SalesDAO;
import com.micaja.servipunto.database.dao.SalesDetailDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.SupplierPaymentsDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.UpdateSynkStatus;

@SuppressLint("ResourceAsColor")
public class ShopCloseDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtbal;
	private Button btnSave, btnCancel;
	private TextView txtOpenBal, txtCloseBal, txtActualBal, txtErrorMsg;
	private String openBal, closebal;
	private UserDetailsDTO userDto;
	private String actualBal;
	private AlertDialog acceptDialog;
	public SharedPreferences sharedpreferences;
	Editor editor;
	private ImageView imgClose;

	public ShopCloseDialog(Context context, String closebal,
			UserDetailsDTO userDto, String actualBal) {
		super(context);
		this.context = context;
		this.closebal = closebal;
		this.userDto = userDto;
		this.actualBal = actualBal;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {

		setContentView(R.layout.shopclosedialog);

		txtOpenBal = (TextView) findViewById(R.id.txt_openbal);
		txtCloseBal = (TextView) findViewById(R.id.txt_closebal);
		txtActualBal = (TextView) findViewById(R.id.txt_ActualBal);
		txtErrorMsg = (TextView) findViewById(R.id.txt_BalErrorMsg);

		btnSave = (Button) findViewById(R.id.btn_dialog_save);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		imgClose = (ImageView) findViewById(R.id.img_close);

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		loadUI();

	}

	private void loadUI() {

		txtOpenBal.setText(context.getResources()
				.getString(R.string.openingbal)
				+ " : "
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(userDto.getOpeningBalance())));

		txtCloseBal.setText(context.getResources().getString(
				R.string.closebalance)
				+ " : "
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(closebal)));

		txtActualBal.setText(context.getResources().getString(
				R.string.actual_balance)
				+ " "
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(actualBal)));

		if (CommonMethods.getDoubleFormate(closebal) != CommonMethods
				.getDoubleFormate(actualBal))
			txtErrorMsg.setText(context.getResources().getString(
					R.string.error_balance_msg));


		else
			validateSync();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		dialog.dismiss();
		if (dialog == acceptDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				// Intent intent = new Intent(context, MenuActivity.class);
				// context.startActivity(intent); // finish();
				syncRecords();
			}
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_dialog_save:
			validateSync();
			this.dismiss();

			break;

		case R.id.btn_dialog_cancel:
			this.dismiss();
			break;
		case R.id.img_close:
			this.dismiss();
			break;
		default:
			break;
		}
	}

	private void validateSync() {
		this.dismiss();
		updateDB();
		syncRecords();

	}

	private boolean updateRecordInDB() {



		userDto.setIsClosed(Constants.TRUE);
		userDto.setOpeningBalance(userDto.getOpeningBalance());
		userDto.setOpeningDateTime(userDto.getOpeningDateTime());
		userDto.setSyncStatus(Constants.TRUE);
		userDto.setCloseDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		userDto.setActualBalance(actualBal);

		if (UserDetailsDAO.getInstance().updateShopOpenInfoDB(
				DBHandler.getDBObj(Constants.WRITABLE), userDto)) {
			return true;
		} else
			CommonMethods.showToast(context,
					context.getResources().getString(R.string.not_added));
		return false;
	}

	private boolean updateDB() {

		userDto.setIsClosed(Constants.FALSE);
		userDto.setOpeningBalance(userDto.getOpeningBalance());
		userDto.setCloseDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		userDto.setSyncStatus(Constants.FALSE);
		userDto.setActualBalance(actualBal);

		if (UserDetailsDAO.getInstance().updateShopOpenInfoDB(
				DBHandler.getDBObj(Constants.WRITABLE), userDto)) {
			return true;
		} else
			CommonMethods.showToast(context,
					context.getResources().getString(R.string.not_added));
		return false;
	}

	// Sync
	private void syncRecords() {
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new ClientListSyncservice().execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.no_wifi_adhoc));
		}
	}

	public void getCallTransaccionBoxService(String module_nem, String tipo,
			String PaymentType) {
		List<DTO> dto = new ArrayList<DTO>();
		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(actualBal);
		tdto.setModule_name(module_nem);
		tdto.setSyncstatus(0);
		tdto.setTipo_transction(tipo);
		tdto.setTransaction_type(PaymentType);
		tdto.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		tdto.setUsername(sharedpreferences.getString("user_name", ""));
		tdto.setStore_code(sharedpreferences.getString("store_code", ""));
		dto.add(tdto);
		if (TransaccionBoxDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				Intent intent = new Intent(context, TransaccionBoxService.class);
				context.startService(intent);
			}
		}
	}



	public void movetonextactivity() {
		getCallTransaccionBoxService(ServiApplication.Shop_Close_M_name,
				ServiApplication.Shop_Close_TipoTrans,
				ServiApplication.Shop_Close_PaymentType);

		userDto.setIsClosed(Constants.TRUE);
		userDto.setOpeningBalance(userDto.getOpeningBalance());
		userDto.setCloseDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		userDto.setSyncStatus(Constants.FALSE);
		userDto.setActualBalance(actualBal);

		if (UserDetailsDAO.getInstance().updateShopOpenInfoDB(
				DBHandler.getDBObj(Constants.WRITABLE), userDto))

			if (ServiApplication.connectionTimeOutState) {
				ServiApplication.alert_type = 2;
				updateRecordInDB();
				CommonMethods.showToast(context, context.getResources()
						.getString(R.string.close_success));
				Intent intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
			} else {
				ServiApplication.alert_type = 2;
				CommonMethods.showToast(context, context.getResources()
						.getString(R.string.sync_error_msg));
				Intent intent = new Intent(context, MenuActivity.class);
				context.startActivity(intent);
			}
	}

	/* Clients sync service call 1 */
	private class ClientListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> clientList = ClientDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (clientList.size() > 0) {
				String val_clientList = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getClientTableData(clientList));
				if (new JSONStatus().status(val_clientList) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(clientList,
								ServiApplication.Client_Info);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new CashFlowListSyncservice().execute();
				} else {
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
					CommonMethods.dismissProgressDialog();
				}
			} else {
				movetonextactivity();
			}

		}
	}

	/* CashFlowListSync service call 2 */
	private class CashFlowListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> cashFlowList = CashFlowDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (cashFlowList.size() > 0) {
				String val_cashFlowList = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getCashFlowTableData(cashFlowList));
				if (new JSONStatus().status(val_cashFlowList) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						CashFlowDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				CashFlowDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new DishProductsListSyncservice().execute();
				} else {
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
					CommonMethods.dismissProgressDialog();
				}
			} else {
				movetonextactivity();
			}

		}
	}

	/* DishProductsListSync service call 3 */
	private class DishProductsListSyncservice extends
			AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> dishProductsList = DishProductsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (dishProductsList.size() > 0) {
				String val_dishProductsList = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getDishProductsTableData(dishProductsList));
				if (new JSONStatus().status(val_dishProductsList) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(dishProductsList,
								ServiApplication.DishProductsList);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new ClientPayListSyncservice().execute();
				} else {
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
					CommonMethods.dismissProgressDialog();
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* ClientPayments sync service call 4 */
	private class ClientPayListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> clientPayList = ClientPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			if (clientPayList.size() > 0) {
				String val_clientList = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getClientPayListTableData(clientPayList));
				if (new JSONStatus().status(val_clientList) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						ClientPaymentDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
						DeliveryPaymentDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				ClientPaymentDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
				DeliveryPaymentDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new DishSyncservice().execute();
				} else {
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
					CommonMethods.dismissProgressDialog();
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* DishSyncservice sync service call 5 */
	private class DishSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> dishlist = DishesDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (dishlist.size() > 0) {
				String val_dishlist = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getDishTableTableData(dishlist));
				if (new JSONStatus().status(val_dishlist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(dishlist,
								ServiApplication.dishlist);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new MenudishSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Menu dish sync service call 6 */
	private class MenudishSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> menudishlist = MenuDishesDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (menudishlist.size() > 0) {
				String val_menudishlist = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getMenuDishTableTableData(menudishlist));
				if (new JSONStatus().status(val_menudishlist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(menudishlist,
								ServiApplication.menudishlist);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new InventorySyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Inventory sync service call 7 */
	private class InventorySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> inventorylist = InventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (inventorylist.size() > 0) {
				String val_inventorylist = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getInventoryTableData(inventorylist));
				if (new JSONStatus().status(val_inventorylist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(inventorylist,
								ServiApplication.inventory);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new InventoryAdjlistSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}

		}
	}

	/* InventoryAdj sync service call 8 */
	private class InventoryAdjlistSyncservice extends
			AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> inventoryAdjlist = InventoryAdjustmentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (inventoryAdjlist.size() > 0) {
				String val_inventoryAdjlist = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getInventoryAdjTableData(inventoryAdjlist));
				if (new JSONStatus().status(val_inventoryAdjlist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						InventoryAdjustmentDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				InventoryAdjustmentDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new InventoryHistorySyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* InventoryHistory sync service call 9 */
	private class InventoryHistorySyncservice extends
			AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> inventoryHistorylist = InventoryHistoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (inventoryHistorylist.size() > 0) {
				String val_inventoryHistorylist = servicehandler
						.makeServiceCall(
								ServiApplication.URL + "/sync",
								ServiceHandler.POST,
								new RequestFiels(context)
										.getInventoryHistoryTableData(inventoryHistorylist));
				if (new JSONStatus().status(val_inventoryHistorylist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						InventoryHistoryDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				InventoryHistoryDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {

					new LendmoneySyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Lend money sync service call 10 */
	private class LendmoneySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> lendmoney = LendMoneyDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (lendmoney.size() > 0) {
				String val_lendmoney = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getLendMoneyTableData(lendmoney));
				if (new JSONStatus().status(val_lendmoney) == 0) {
					if (ServiApplication.connectionTimeOutState) {

						LendMoneyDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
						LendDeliveryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));


					}
				}
			} else {
				LendMoneyDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
				LendDeliveryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new MenusSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}




	/* Menus sync service call 11 */
	private class MenusSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> menus = MenuDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			if (menus.size() > 0) {
				String val_menus = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context).getMenusTableData(menus));
				if (new JSONStatus().status(val_menus) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(menus, ServiApplication.menu);
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new MenuInventorySyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* MenuInventorySyncservice sync service call 12 */
	private class MenuInventorySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> menuInventorylist = MenuInventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (menuInventorylist.size() > 0) {
				String val_menuInventorylist = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getMenusInventoryTableData(menuInventorylist));
				if (new JSONStatus().status(val_menuInventorylist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(menuInventorylist,
								ServiApplication.menuInventory);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new MenuInventoryAdjlistSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* MenuInventoryAdjlist sync service call 13 */
	private class MenuInventoryAdjlistSyncservice extends
			AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> menuInventoryAdjlist = MenuInventoryAdjustmentDAO
				.getInstance().getRecordsWithValues(
						DBHandler.getDBObj(Constants.READABLE), "sync_status",
						"0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (menuInventoryAdjlist.size() > 0) {
				String val_menuInventoryAdjlist = servicehandler
						.makeServiceCall(
								ServiApplication.URL + "/sync",
								ServiceHandler.POST,
								new RequestFiels(context)
										.getMenusInventoryAdjTableData(menuInventoryAdjlist));
				if (new JSONStatus().status(val_menuInventoryAdjlist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						MenuInventoryAdjustmentDAO.getInstance()
								.deleteAllRecords(
										DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				MenuInventoryAdjustmentDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new OrderDetailsSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* OrderDetails sync service call 14 */
	private class OrderDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> orderdetails = OrderDetailsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (orderdetails.size() > 0) {
				String val_orderdetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getOrderDetailsTableData(orderdetails));
				if (new JSONStatus().status(val_orderdetails) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						OrderDetailsDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				OrderDetailsDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new OrdersSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Orders sync service call 15 */
	private class OrdersSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> orders = OrdersDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (orders.size() > 0) {
				String val_orders = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context).getOrdersTableData(orders));
				if (new JSONStatus().status(val_orders) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						OrdersDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				OrdersDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new SalesSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Sales sync service call 16 */
	private class SalesSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> sales = SalesDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (sales.size() > 0) {
				String val_sales = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context).getSalesData(sales));
				if (new JSONStatus().status(val_sales) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						SalesDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				SalesDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new SalesDetailsSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Sales Details sync service call 17 */
	private class SalesDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> salesDetails = SalesDetailDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (salesDetails.size() > 0) {
				String val_salesDetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getSalesDetailsData(salesDetails));
				if (new JSONStatus().status(val_salesDetails) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						SalesDetailDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				SalesDetailDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new ClientPaymentDetails().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}

		}
	}

	/* Client Payment Details sync service call 18 */
	private class ClientPaymentDetails extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> clientpaymentDetails = ClientPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (clientpaymentDetails.size() > 0) {
				String val_clientpaymentDetails = servicehandler
						.makeServiceCall(
								ServiApplication.URL + "/sync",
								ServiceHandler.POST,
								new RequestFiels(context)
										.getPaymentDetailsData(clientpaymentDetails));
				if (new JSONStatus().status(val_clientpaymentDetails) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(clientpaymentDetails,
								ServiApplication.clientpayments);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new SupplierDetailsSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Supplier Details sync service call 19 */
	private class SupplierDetailsSyncservice extends
			AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> supplierDetails = SupplierDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (supplierDetails.size() > 0) {
				String val_supplierDetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getSuppliersDetailsData(supplierDetails));
				if (new JSONStatus().status(val_supplierDetails) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(supplierDetails,
								ServiApplication.supplier);
					}
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new SupplierPaymentsDetailsSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Supplier Payments Details sync service call 20 */
	private class SupplierPaymentsDetailsSyncservice extends
			AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		List<DTO> supplierPaymentsDetails = SupplierPaymentsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (supplierPaymentsDetails.size() > 0) {
				String val_supplierpayments = servicehandler
						.makeServiceCall(
								ServiApplication.URL + "/sync",
								ServiceHandler.POST,
								new RequestFiels(context)
										.getSupplierPayTableData(supplierPaymentsDetails));

				if (new JSONStatus().status(val_supplierpayments) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						SupplierPaymentsDAO.getInstance().deleteAllRecords(
								DBHandler.getDBObj(Constants.WRITABLE));
					}
				}
			} else {
				SupplierPaymentsDAO.getInstance().deleteAllRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new ProductDetailsSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* Products sync service call 21 */
	private class ProductDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
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
				String val_productDetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context)
								.getProductTableData(productDetails));
				if (new JSONStatus().status(val_productDetails) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(productDetails,
								ServiApplication.updateProducts);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (ServiApplication.connectionTimeOutState) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new UserSyncservice().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(context, context
							.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				movetonextactivity();
			}
		}
	}

	/* User sync service call 21 */
	private class UserSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);
		List<DTO> user = new ArrayList<DTO>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			UserDetailsDTO user1 = UserDetailsDAO.getInstance()
					.getRecordsuser_name(
							DBHandler.getDBObj(Constants.READABLE),
							sharedpreferences.getString("user_name", ""));
			user.add(user1);

			Log.v("varahalababu", user1.getOpeningDateTime());
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (user.size() > 0) {
				String val_productDetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context).getUserTableData(user));
				if (new JSONStatus().status(val_productDetails) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(user, ServiApplication.userTable);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			DeliveryPaymentDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			LendDeliveryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			movetonextactivity();
		}
	}
}
