package com.micaja.servipunto.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class SyncDataFromServerChangedClass12 {

	public static Context mContext;
	public static int classtype;
	public SharedPreferences sharedpreferences;
	public Editor editor;
	GroupDTO gdto;
	private int supplier_next_page = 1,inventory_next_page = 1,product_next_page = 1,client_next_page = 1;

	public SyncDataFromServerChangedClass12(Context context, int value) {
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		mContext = context;
		classtype = value;
		editor = sharedpreferences.edit();
		getDbblm();
		if (NetworkConnectivity.netWorkAvailability(mContext)) {
			
			
			ServiApplication.setSupplier_downloded_count(0);
			ServiApplication.setSupplier_total_count(0);
			
			ServiApplication.setProduct_downloded_count(0);
			ServiApplication.setProduct_total_count(0);
			
			ServiApplication.setClient_downloded_count(0);
			ServiApplication.setClient_total_count(0);

			InventoryFromCentralServer inventory = new InventoryFromCentralServer();
			SupplierDataFromCentralServer supplier = new SupplierDataFromCentralServer();
			ProductDataFromCentralServer product = new ProductDataFromCentralServer();
			ClientDataFromCentralServer client = new ClientDataFromCentralServer();
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				inventory.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else{
				inventory.execute();
			}
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				supplier.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				supplier.execute();
			}
			
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				product.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				product.execute();
			}
			
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				client.execute();
			}

		} else {
		}
	}

	// calling Inventory data from server1
	public class InventoryFromCentralServer extends AsyncTask<Void, Void, Void> {
		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.inventory_sync.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + inventory_next_page,
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
					
//					 InventoryDAO.getInstance().executeUpdateDelete(DBHandler.getDBObj(Constants.READABLE), ServiApplication.inventory_sync);
				}
				callto13thmethod(responds);
			} else {
				callto13thmethod(responds);
			}
		}

		private void callto13thmethod(String responds) {
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				inventory_next_page = onJson.getInt("next_page");
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
					new InventoryFromCentralServer().execute();
				} else {
				}
			} catch (Exception e) {		
					ServiApplication.connectionTimeOutState = true;
					inventory_next_page = 1;
					ServiApplication.setDownloded_count(0);
					ServiApplication.setTotal_count(0);
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
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + supplier_next_page,
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

			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				supplier_next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setSupplier_total_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setSupplier_total_count(0);
				}

				try {
					ServiApplication.setSupplier_downloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());;
				} catch (Exception e) {
					ServiApplication.setSupplier_downloded_count(0);
				}

				ServiApplication.setDownloded_table_name("Supplier");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {

					new SupplierDataFromCentralServer().execute();

				} else {
				}
			} catch (Exception e) {
					supplier_next_page = 1;
					ServiApplication.setSupplier_total_count(0);
					ServiApplication.setSupplier_downloded_count(0);
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
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + product_next_page,
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


			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				product_next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setProduct_total_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setProduct_total_count(0);
				}

				try {
					ServiApplication.setProduct_downloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setProduct_downloded_count(0);
				}

				ServiApplication.setDownloded_table_name("Product");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					new ProductDataFromCentralServer().execute();
				} else {
				}
			} catch (Exception e) {
					product_next_page = 1;
					ServiApplication.setProduct_downloded_count(0);
					ServiApplication.setProduct_total_count(0);
			}
		}
	}


	public class ClientDataFromCentralServer extends AsyncTask<Void, Void, Void> {

		String responds;

		@Override
		protected Void doInBackground(Void... params) {
			ServiApplication.clients.clear();
			ServiceHandler servicehandler = new ServiceHandler(mContext);
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/sync-client?page=" + client_next_page,
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
			try {
				JSONObject json = new JSONObject(responds);
				JSONObject onJson = json.getJSONObject("paginator");
				client_next_page = onJson.getInt("next_page");
				try {
					ServiApplication.setClient_total_count(onJson.getInt("item_count"));
				} catch (Exception e) {
					ServiApplication.setClient_total_count(0);
				}

				try {
					ServiApplication.setClient_downloded_count(
							ServiApplication.getDownloded_count() + json.getJSONArray("data").length());
				} catch (Exception e) {
					ServiApplication.setClient_downloded_count(0);
				}
				ServiApplication.setDownloded_table_name("Client");

				if (NetworkConnectivity.netWorkAvailability(mContext)) {
					new ClientDataFromCentralServer().execute();
				} else {
				}
			} catch (Exception e) {
					client_next_page = 1;
					ServiApplication.setClient_downloded_count(0);
					ServiApplication.setClient_total_count(0);
//					synkMethod();
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
			}



			jsonobj.put("username", ServiApplication.userName);
			jsonobj.put("store_code", ServiApplication.store_id);
		} catch (Exception e) {

		}
		return jsonobj.toString().trim();
	}


	private void getDbblm() {
		gdto = GroupDAO.getInstance().DBLMByGroup_id(DBHandler.getDBObj(Constants.WRITABLE), "1");
	}

}
