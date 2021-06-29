package com.micaja.servipunto.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.DamageTypeDAO;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.LineDAO;
import com.micaja.servipunto.database.dao.MenuDAO;
import com.micaja.servipunto.database.dao.MenuDishesDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dao.MenuTypesDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DamageTypeDTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.LineDTO;
import com.micaja.servipunto.database.dto.MenuDTO;
import com.micaja.servipunto.database.dto.MenuDishesDTO;
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.database.dto.MenuTypesDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;

public class SyncDataFromServer {
	public static Context mContext;
	public static int classtype;
	public SharedPreferences sharedpreferences;
	public Editor editor;
	GroupDTO gdto;
	private int next_page = 1;

	public SyncDataFromServer(Context context, int value) {
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		mContext = context;
		classtype = value;
		editor = sharedpreferences.edit();
		getDbblm();
		if (NetworkConnectivity.netWorkAvailability(mContext)) {
			new InventoryFromCentralServer().execute();
		} else {
			CommonMethods.showCustomToast(mContext,
					mContext.getResources().getString(R.string.no_wifi_adhoc)
							+ mContext.getResources().getString(R.string.or)
							+ mContext.getResources().getString(R.string.db_errormessage));
			CommonMethods.dismissProgressDialog();
		}
	}

	// calling Inventory data from server1
	public class InventoryFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.inventory_sync.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(13));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.inventory_sync = new ParsingHandler().getinventory(responds);
				if (gdto.getName().equals("babu")) {
					InventoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.inventory_sync);
				} else {
					for (DTO dto : ServiApplication.inventory_sync) {
						InventoryDTO idto = (InventoryDTO) dto;
						idto.setQuantityBalance(0.0);
						InventoryDTO local_Suppliers_listDTO = InventoryDAO.getInstance()
								.getRecordByProductID(DBHandler.getDBObj(Constants.READABLE), idto.getProductId());
						List<DTO> iList = new ArrayList<DTO>();
						if (local_Suppliers_listDTO.getProductId() != null) {
							InventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), idto);
						} else {
							iList.add(idto);
							InventoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), iList);
						}
					}
				}
				callto13thmethod(responds);
			} else {
				callto13thmethod(responds);
			}
		}

		private void callto13thmethod(String responds) {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("Inventory");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new InventoryFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {
				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					ServiApplication.connectionTimeOutState = true;
					next_page = 1;
					ServiApplication.setDownloded_count(0);
					ServiApplication.setTotal_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new DishDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}
		}
	}

	// calling Dish data from server2
	public class DishDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.dishs.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(11));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				ServiApplication.dishs = new ParsingHandler().getdishes(responds);
				if (gdto.getName().equals("babu")) {

				} else {
					for (DTO dto : ServiApplication.dishs) {
						DishesDTO dDTO = (DishesDTO) dto;
						DishesDTO local_product_listDTO = DishesDAO.getInstance()
								.getRecordsByDish_id(DBHandler.getDBObj(Constants.READABLE), dDTO.getDishId());
						List<DTO> dishList = new ArrayList<DTO>();
						if (local_product_listDTO.getDishId() != null) {
							DishesDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dDTO);
						} else {
							dishList.add(dDTO);
							DishesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dishList);
						}
					}
				}
				callto12thmethod();

			} else {
				callto12thmethod();
			}
		}

		private void callto12thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");

				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("Dish");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new DishDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setDownloded_count(0);
					ServiApplication.setTotal_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new DishProductsFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext, mContext.getResources().getString(R.string.no_wifi_adhoc));
				}
			}
		}
	}

	// calling DishProducts data from server3
	public class DishProductsFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.dish_products.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(12));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				ServiApplication.dish_products = new ParsingHandler().getdish_products(responds);
				if (gdto.getName().equals("babu")) {
					DishesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.dish_products);
				} else {
					for (DTO dto : ServiApplication.dish_products) {
						DishProductsDTO dpDTO = (DishProductsDTO) dto;
						DishProductsDTO local_product_listDTO = DishProductsDAO.getInstance()
								.getRecordsByDish_id(DBHandler.getDBObj(Constants.READABLE), dpDTO.getDishId());
						List<DTO> dishList = new ArrayList<DTO>();
						if (local_product_listDTO.getDishId() != null) {
							DishProductsDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dpDTO);
						} else {
							dishList.add(dpDTO);
							DishProductsDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dishList);
						}
					}
				}
				callto13thmethod();

			} else {
				callto13thmethod();
			}
		}

		private void callto13thmethod() {
			CommonMethods.dismissProgressDialog();

			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");

				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("DishProducts");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new DishProductsFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext, mContext.getResources().getString(R.string.no_wifi_adhoc));
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new DamageTypeDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling DamageType data from server4
	public class DamageTypeDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.damage_type.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(10));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.damage_type = new ParsingHandler().getdamage_type(responds);
				if (gdto.getName().equals("babu")) {
					DamageTypeDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.damage_type);

				} else {
					for (DTO dto : ServiApplication.damage_type) {
						DamageTypeDTO dyDTO = (DamageTypeDTO) dto;
						DamageTypeDTO local_product_listDTO = DamageTypeDAO.getInstance()
								.getRecordsByDamage_id(DBHandler.getDBObj(Constants.READABLE), dyDTO.getDamageTypeId());
						List<DTO> dishList = new ArrayList<DTO>();
						if (local_product_listDTO.getDamageTypeId() != null) {
							DamageTypeDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dyDTO);
						} else {
							dishList.add(dyDTO);
							DamageTypeDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dishList);
						}
					}
				}
				callto11thmethod();

			} else {
				callto11thmethod();
			}
		}

		private void callto11thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");

				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("DamageTypeData");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new DamageTypeDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new SupplierDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling Supplier data from server5
	public class SupplierDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.suppliers.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(8));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.suppliers = new ParsingHandler().getSuppliersData(responds);
				if (gdto.getName().equals("babu")) {
					SupplierDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.suppliers);
				} else {
					for (DTO dto : ServiApplication.suppliers) {
						SupplierDTO supplierdto = (SupplierDTO) dto;
						supplierdto.setSyncStatus(1);
						SupplierDTO local_Suppliers_listDTO = SupplierDAO.getInstance().getRecordsBySupplierID(
								DBHandler.getDBObj(Constants.READABLE), supplierdto.getCedula());
						List<DTO> suppliersList = new ArrayList<DTO>();
						if (local_Suppliers_listDTO.getSupplierId() != null) {
							SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), supplierdto);
						} else {
							suppliersList.add(supplierdto);
							SupplierDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), suppliersList);
						}
					}
				}
				callto9thmethod();
			} else {
				callto9thmethod();
			}
		}

		private void callto9thmethod() {
			CommonMethods.dismissProgressDialog();

			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("Supplier");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);

					new SupplierDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new ProductDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling Product data from server6
	public class ProductDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.products.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(7));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.products = new ParsingHandler().getProductData(responds);
				if (gdto.getName().equals("babu")) {
					ProductDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), ServiApplication.products);
				} else {
					for (DTO dto : ServiApplication.products) {
						ProductDTO productDTO = (ProductDTO) dto;
						ProductDTO local_product_listDTO = ProductDAO.getInstance()
								.getRecordsByBarcode(DBHandler.getDBObj(Constants.READABLE), productDTO.getBarcode());
						List<DTO> productList = new ArrayList<DTO>();
						if (local_product_listDTO.getBarcode() != null) {
							ProductDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), productDTO);
						} else {
							productList.add(productDTO);
							ProductDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), productList);
						}
					}
				}
				callto8thmethod();
			} else {
				callto8thmethod();
			}
		}

		private void callto8thmethod() {

			CommonMethods.dismissProgressDialog();

			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("Product");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new ProductDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;

					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);

					new LineDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling Line data from server7
	public class LineDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.line.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(6));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.line = new ParsingHandler().getline(responds);
				if (gdto.getName().equals("babu")) {

					LineDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), ServiApplication.line);
					MenuInventoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.menu_inventory);
				} else {
					for (DTO dto : ServiApplication.line) {
						LineDTO lDTO = (LineDTO) dto;
						LineDTO local_product_listDTO = LineDAO.getInstance()
								.getRecordsByline_id(DBHandler.getDBObj(Constants.READABLE), lDTO.getLineId());
						List<DTO> dishList = new ArrayList<DTO>();
						if (local_product_listDTO.getLineId() != null) {
							LineDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), lDTO);
						} else {
							dishList.add(lDTO);
							LineDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dishList);
						}
					}
				}
				callto7thmethod();

			} else {
				callto7thmethod();
			}
		}

		private void callto7thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("LineData");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new LineDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);

					new MenusInventoryDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}
		}
	}

	// calling Menu Inventory data from server9
	public class MenusInventoryDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.menu_inventory.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(2));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.menu_inventory = new ParsingHandler().getmenuInventory(responds);
				if (gdto.getName().equals("babu")) {
					MenuInventoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.menu_inventory);
				} else {
					for (DTO dto : ServiApplication.menu_inventory) {
						MenuInventoryDTO dyDTO = (MenuInventoryDTO) dto;

						MenuInventoryDTO local_product_listDTO = MenuInventoryDAO.getInstance()
								.getRecordsMenu_inventory_id(DBHandler.getDBObj(Constants.READABLE),
										dyDTO.getMenuInventoryId());
						List<DTO> dishList = new ArrayList<DTO>();
						if (local_product_listDTO.getMenuInventoryId() != null) {
							MenuInventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dyDTO);
						} else {
							dishList.add(dyDTO);
							MenuInventoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dishList);
						}
					}
				}
				callto3thmethod();

			} else {
				callto3thmethod();
			}
		}

		private void callto3thmethod() {
			CommonMethods.dismissProgressDialog();

			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}

				ServiApplication.setDownloded_table_name("MenusInventory");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new MenusInventoryDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new MenusTypeDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling Menu Type data from server10
	public class MenusTypeDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.menu_type.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(3));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				ServiApplication.menu_type = new ParsingHandler().getmenusTypes(responds);
				if (gdto.getName().equals("babu")) {
					MenuTypesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.menu_type);
				} else {
					for (DTO dto : ServiApplication.menu_type) {
						MenuTypesDTO dyDTO = (MenuTypesDTO) dto;
						MenuTypesDTO local_product_listDTO = MenuTypesDAO.getInstance()
								.getRecordsMenu_type_id(DBHandler.getDBObj(Constants.READABLE), dyDTO.getMenuTypeId());
						List<DTO> dishList = new ArrayList<DTO>();
						if (local_product_listDTO.getMenuTypeId() != null) {
							MenuTypesDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dyDTO);
						} else {
							dishList.add(dyDTO);
							MenuTypesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dishList);
						}
					}
				}
				callto4thmethod();

			} else {
				callto4thmethod();
			}
		}

		private void callto4thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}
				ServiApplication.setDownloded_table_name("MenusType");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new MenusTypeDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new MenusDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling Menu data from server11
	public class MenusDataFromCentralServer extends AsyncTask<Void, Void, Void> {

		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.menus.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(0));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.menus = new ParsingHandler().getmenus(responds);
				if (gdto.getName().equals("babu")) {
					MenuDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), ServiApplication.menus);
				} else {
					for (DTO dto : ServiApplication.menus) {
						MenuDTO mdto = (MenuDTO) dto;
						MenuDTO local_client_listDTO = MenuDAO.getInstance()
								.getRecordsMenu_id(DBHandler.getDBObj(Constants.READABLE), mdto.getMenuId());
						List<DTO> clientListList = new ArrayList<DTO>();
						if (local_client_listDTO.getMenuId() != null) {
							MenuDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), mdto);

						} else {
							clientListList.add(mdto);
							MenuDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), clientListList);
						}
					}
				}
				callto1thmethod();

			} else {
				callto1thmethod();
			}
		}

		private void callto1thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}
				ServiApplication.setDownloded_table_name("Menus");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new MenusDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new MenusDishesDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling MenuDishes data from server12
	public class MenusDishesDataFromCentralServer extends AsyncTask<Void, Void, Void> {

		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.menu_dishes.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(1));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.menu_dishes = new ParsingHandler().getmenusDishs(responds);
				if (gdto.getName().equals("babu")) {
					MenuDishesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
							ServiApplication.menu_dishes);
				} else {
					for (DTO dto : ServiApplication.menu_dishes) {
						MenuDishesDTO mdishdto = (MenuDishesDTO) dto;
						MenuDishesDTO local_client_listDTO = (MenuDishesDTO) MenuDishesDAO.getInstance()
								.getRecordsMenu_dishes_id(DBHandler.getDBObj(Constants.READABLE),
										mdishdto.getMenuDishesId());
						List<DTO> clientListList = new ArrayList<DTO>();
						if (local_client_listDTO.getMenuDishesId() != null) {
							MenuDishesDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), mdishdto);
						} else {
							clientListList.add(mdishdto);
							MenuDishesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), clientListList);
						}
					}
				}
				callto2thmethod();

			} else {
				callto2thmethod();
			}
		}

		private void callto2thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}
				ServiApplication.setDownloded_table_name("MenusDishes");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new MenusDishesDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new ClientDataFromCentralServer().execute();

				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling customer data from server13
	public class ClientDataFromCentralServer extends AsyncTask<Void, Void, Void> {

		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.clients.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(4));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.clients = new ParsingHandler().getCustomersData(responds);
				if (gdto.getName().equals("babu")) {
					ClientDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), ServiApplication.clients);
				} else {
					for (DTO dto : ServiApplication.clients) {
						ClientDTO clientdto = (ClientDTO) dto;
						clientdto.setSyncStatus(1);
						ClientDTO local_client_listDTO = ClientDAO.getInstance().getRecordsByClientCedula(
								DBHandler.getDBObj(Constants.READABLE), clientdto.getCedula());
						List<DTO> clientListList = new ArrayList<DTO>();
						if (local_client_listDTO.getClientID() != null) {
							ClientDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), clientdto);
						} else {
							clientListList.add(clientdto);
							ClientDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), clientListList);
						}
					}
				}
				callto5thmethod();

			} else {
				callto5thmethod();
			}
		}

		private void callto5thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}
				ServiApplication.setDownloded_table_name("Client");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new ClientDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new DeliveryDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	public class DeliveryDataFromCentralServer extends AsyncTask<Void, Void, Void> {

		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.clients.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(14));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.deliverys = new ParsingHandler().getDeliverysData(responds);
				if (gdto.getName().equals("babu")) {
					DeliveryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), ServiApplication.deliverys);
				} else {
					for (DTO dto : ServiApplication.deliverys) {
						DeliveryDTO deliveryDTO = (DeliveryDTO) dto;
						deliveryDTO.setSyncStatus(1);
						DeliveryDTO local_delivery_listDTO = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(
								DBHandler.getDBObj(Constants.READABLE), deliveryDTO.getCedula());
						List<DTO> deliveryListList = new ArrayList<DTO>();
						if (local_delivery_listDTO.getDeliveryID() != null) {
							DeliveryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), deliveryDTO);
						} else {
							deliveryListList.add(deliveryDTO);
							DeliveryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), deliveryListList);
						}
					}
				}
				callto5thmethod();

			} else {
				callto5thmethod();
			}
		}

		private void callto5thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}

				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}
				ServiApplication.setDownloded_table_name("Delivery");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new ClientDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					next_page = 1;
					ServiApplication.setTotal_count(0);
					ServiApplication.setDownloded_count(0);
					CommonMethods.dismissProgressDialog();
					CommonMethods.showProgressDialog(mContext.getString(R.string.db_cleanup), mContext);
					new GroupDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	// calling Group data from server8
	public class GroupDataFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.group.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + next_page,
					ServiceHandler.POST, makeJsonReq(5));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				ServiApplication.group = new ParsingHandler().getgroup(responds);
				if (gdto.getName().equals("babu")) {

					gdto.setName("babu");
					GroupDAO.getInstance().DBLMupdate(DBHandler.getDBObj(Constants.WRITABLE), gdto);
				} else {
					gdto.setName("varun");
					GroupDAO.getInstance().DBLMupdate(DBHandler.getDBObj(Constants.WRITABLE), gdto);
				}
				callto4thmethod();

			} else {
				callto4thmethod();
			}
		}

		private void callto4thmethod() {
			CommonMethods.dismissProgressDialog();
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setTotal_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setTotal_count(0);
				}
				try {
					ServiApplication.setDownloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setDownloded_count(0);
				}
				ServiApplication.setDownloded_table_name("GroupData");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					CommonMethods.showProgressDialog(mContext.getString(R.string.auto_sync_messsage1) + " "
							+ ServiApplication.getDownloded_count() + " "
							+ mContext.getString(R.string.auto_sync_messsage2) + " " + ServiApplication.getTotal_count()
							+ " " + mContext.getString(R.string.auto_sync_messsage3) + " "
							+ ServiApplication.getDownloded_table_name(), mContext);
					new GroupDataFromCentralServer().execute();
				} else {
					CommonMethods.showCustomToast(mContext,
							mContext.getResources().getString(R.string.no_wifi_adhoc)
									+ mContext.getResources().getString(R.string.or)
									+ mContext.getResources().getString(R.string.db_errormessage));
					CommonMethods.dismissProgressDialog();
				}
			} catch (Exception e) {
				// if (classtype == 0) {
				ServiApplication.setTotal_count(0);
				ServiApplication.setDownloded_count(0);
				CommonMethods.dismissProgressDialog();
				synkMethod();
				// } else {
				// CommonMethods.dismissProgressDialog();
				// }
			}
		}
	}

	public String makeJsonReq(int i) {
		JSONObject jsonobj = new JSONObject();
		try {
			if (i == 0) {
				jsonobj.put("name", "menu");
			} else if (i == 1) {
				jsonobj.put("name", "menu_dishes");
			} else if (i == 2) {
				jsonobj.put("name", "menu_inventory");
			} else if (i == 3) {
				jsonobj.put("name", "menu_types");
			} else if (i == 4) {
				jsonobj.put("name", "customer");
			} else if (i == 5) {
				jsonobj.put("name", "group");
			} else if (i == 6) {
				jsonobj.put("name", "line");
			} else if (i == 7) {
				jsonobj.put("name", "product");
			} else if (i == 8) {
				jsonobj.put("name", "supplier");
			} else if (i == 9) {
				jsonobj.put("name", "user");
			} else if (i == 10) {
				jsonobj.put("name", "damage_type");
			} else if (i == 11) {
				jsonobj.put("name", "dishes");
			} else if (i == 12) {
				jsonobj.put("name", "dish_products");
			} else if (i == 13) {
				jsonobj.put("name", "inventory");
			} else if (i == 14){
				jsonobj.put("name", "delivery");
			}

			/****** get user info *****/

			// List<DTO> dto = UserDetailsDAO.getInstance().getRecords(
			// DBHandler.getDBObj(Constants.READABLE));
			// UserDetailsDTO userdao = (UserDetailsDTO) dto.get(0);
			// ServiApplication.userName = userdao.getUserName();
			// ServiApplication.store_id = userdao.getNitShopId();

			/****** Bind user info to sync service *****/

			jsonobj.put("username", ServiApplication.userName);
			jsonobj.put("store_code", ServiApplication.store_id);
		} catch (Exception e) {

		}
		return jsonobj.toString().trim();
	}

	public void synkMethod() {

		CommonMethods.showProgressDialog(mContext.getString(R.string.please_wait), mContext);
		List<DTO> list = UserDetailsDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.WRITABLE));
		if (new AsynkTaskClass().callcentralserver("/sync", "" + ServiceHandler.POST,
				new RequestFiels(mContext).getMoveToMenuData(list), mContext) != null) {
			// if (new JSONStatus().status(ServiApplication.responds_feed) == 0)
			// {
			// CommonMethods.dismissProgressDialog();
			// CommonMethods.startIntent(((Activity) mContext),
			// MenuActivity.class);
			// ((Activity) mContext).finish();
			// } else {
			// CommonMethods.dismissProgressDialog();
			// CommonMethods.startIntent(((Activity) mContext),
			// MenuActivity.class);
			// ((Activity) mContext).finish();
			// CommonMethods.showCustomToast(((Activity)
			// mContext),mContext.getResources().getString(R.string.sync_error_msg));
			// }

			if (classtype == 0) {
				CommonMethods.dismissProgressDialog();
				CommonMethods.startIntent(((Activity) mContext), MenuActivity.class);
				((Activity) mContext).finish();
			} else {
				CommonMethods.dismissProgressDialog();
			}

		} else {
			// CommonMethods.dismissProgressDialog();
			// CommonMethods.startIntent(((Activity) mContext),
			// MenuActivity.class);
			// ((Activity) mContext).finish();

			if (classtype == 0) {
				CommonMethods.dismissProgressDialog();
				CommonMethods.startIntent(((Activity) mContext), MenuActivity.class);
				((Activity) mContext).finish();
			} else {
				CommonMethods.dismissProgressDialog();
			}
		}

	}

	private void getDbblm() {
		gdto = GroupDAO.getInstance().DBLMByGroup_id(DBHandler.getDBObj(Constants.WRITABLE), "1");

	}

}
