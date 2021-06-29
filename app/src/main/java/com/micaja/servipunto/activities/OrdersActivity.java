/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.OrdersSuppAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.OrderDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.UpdateSynkStatus;

public class OrdersActivity extends BaseActivity implements OnItemClickListener {
	private TextView txtName, txtPrice, txtSuggested, txtAddCart;

	private ListView lvSuppliers;

	ServiApplication appContext;

	Intent intent;
	private int ih_req_count = 1;
	List<DTO> historyDetails = new ArrayList<DTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		intent = new Intent(this, OrdersActivity.class);
		appContext = (ServiApplication) getApplicationContext();

		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.orders_supplier);

		lvSuppliers = (ListView) findViewById(R.id.lv_OrdersSuppliers);
		appContext.setSeletedProducts(new ArrayList<DTO>());
		setInVisiable();

		getOrdersDetailsSyck();

		// setSuppliers();

		System.out.println("Order suppliers :" + appContext.getOrderSupplierList());

		if (appContext.getOrderSupplierList().size() != 0) {
			lvSuppliers.setAdapter(new OrdersSuppAdapter(this, appContext.getOrderSupplierList()));
			lvSuppliers.setOnItemClickListener(this);
		} else
			getSuppliersProducts();
	}

	private void getOrdersDetailsSyck() {
		appContext.setOrder_product(false);
		if (NetworkConnectivity.netWorkAvailability(OrdersActivity.this)) {
			List<DTO> orderdetails = OrderDetailsDAO.getInstance()
					.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");
			if (orderdetails.size() > 0) {
				new GetOrdersDetailsSyck().execute();
			}
		} else {
			CommonMethods.showCustomToast(OrdersActivity.this, getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	// Result of this method,invisible views
	private void setInVisiable() {
		txtName = (TextView) findViewById(R.id.txt_OrdersNameLeble);
		txtPrice = (TextView) findViewById(R.id.txt_OrdersPriceLebel);
		txtSuggested = (TextView) findViewById(R.id.txt_OrdersSuggestLebel);
		txtAddCart = (TextView) findViewById(R.id.txt_OrdersCartLebel);

		txtName.setText(getResources().getString(R.string.order_supplier));
		txtName.setGravity(Gravity.LEFT);
		txtName.setPadding(10, 10, 0, 10);
		txtPrice.setVisibility(View.GONE);
		txtSuggested.setVisibility(View.GONE);
		txtAddCart.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			appContext.pushActivity(intent);
			appContext.setSeletedSupplier(String.valueOf(position));
			CommonMethods.openNewActivity(OrdersActivity.this, OrdersProductsActivity.class);
			this.finish();
		} catch (Exception e) {
		}
	}

	// Result of this method,check network
	private void getSuppliersProducts() {

		if (NetworkConnectivity.netWorkAvailability(OrdersActivity.this)) {
			new GetProductCatlog().execute(ServiApplication.store_id);
		} else {
			CommonMethods.showCustomToast(OrdersActivity.this, getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	// Result of this method, Getting Master Supplier
	private class GetProductCatlog extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(OrdersActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), OrdersActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			responds = servicehandler.makeServiceCall(
					ServiApplication.URL + "/product/get-catalog/" + params[0] + "?page=" + ih_req_count,
					ServiceHandler.GET);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {

				if (new JSONStatus().status(responds) == 0) {
					try {
						JSONObject json = new JSONObject(responds);
						JSONObject pagenation = json.getJSONObject("paginator");
						ih_req_count = pagenation.getInt("next_page");
						getOrederDetailsData(responds);
						getSuppliersProducts();
					} catch (Exception e) {
						getOrederDetailsData(responds);
						setDataToViews();
					}
				}

				// ServiApplication.responds_feed = responds;
				// List<DTO> list = new
				// ParsingHandler().getSuppliersData(ServiApplication.responds_feed);

			} else {
				ServiApplication.responds_feed = null;
			}
		}
	}

	public class GetOrdersDetailsSyck extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(OrdersActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), OrdersActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<DTO> orderdetails = OrderDetailsDAO.getInstance()
					.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");
			if (orderdetails.size() > 0) {
				String val_orderdetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
						ServiceHandler.POST,
						new RequestFiels(OrdersActivity.this).getOrderDetailsTableData(orderdetails));
				if (new JSONStatus().status(val_orderdetails) == 0) {
					new UpdateSynkStatus(orderdetails, ServiApplication.orderDetails);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
		}
	}

	public void setDataToViews() {
		// TODO Auto-generated method stub
		appContext.setOrderSupplierList(historyDetails);
		lvSuppliers.setAdapter(new OrdersSuppAdapter(OrdersActivity.this, appContext.getOrderSupplierList()));
		lvSuppliers.setOnItemClickListener(OrdersActivity.this);
	}

	public void getOrederDetailsData(String responds) {

		try {
			JSONObject jsonobject = new JSONObject(responds);
			JSONArray arrayData = jsonobject.getJSONArray("data");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getSupplier = arrayData.getJSONObject(i);
				SupplierDTO supplierDTO = new SupplierDTO();
				supplierDTO.setActiveStatus(1);
				supplierDTO.setSupplierId(getSupplier.getString("supplier_code"));
				supplierDTO.setName(getSupplier.getString("name"));
				supplierDTO.setAddress(getSupplier.getString("address"));
				if (getSupplier.getString("balance_amount").contains("null")) {
					supplierDTO.setBalanceAmount("0");
				} else {
					supplierDTO.setBalanceAmount("" + getSupplier.getLong("balance_amount"));
				}
				supplierDTO.setCedula(getSupplier.getString("supplier_code"));
				supplierDTO.setContactName(getSupplier.getString("contact_name"));
				supplierDTO.setContactTelephone(getSupplier.getString("contact_telephone"));
				supplierDTO.setCreatedDate(getSupplier.getString("created_date"));
				supplierDTO.setLastPaymentDate(getSupplier.getString("last_payment_date"));
				supplierDTO.setLogo(getSupplier.getString("logo"));
				supplierDTO.setModifiedDate(getSupplier.getString("modified_date"));
				supplierDTO.setSyncStatus(1);
				supplierDTO.setTelephone(getSupplier.getString("telephone"));
				supplierDTO.setVisitDays(getSupplier.getString("visit_days"));
				supplierDTO.setVisitFrequency(getSupplier.getString("visit_frequency"));
				try {
					Integer.parseInt(getSupplier.getString("visit_frequency"));
				} catch (Exception e) {
					try {
						supplierDTO.setVisitFrequency(getSupplier.getString("active_status"));
					} catch (Exception e2) {
						supplierDTO.setVisitFrequency("0");
					}
					supplierDTO.setVisitDays(getSupplier.getString("visit_frequency"));
					supplierDTO.setTelephone(getSupplier.getString("visit_days"));
					supplierDTO.setContactName(getSupplier.getString("contact_telephone"));
					supplierDTO.setContactTelephone(getSupplier.getString("contact_name"));
				}

				try {
					JSONArray productArray = getSupplier.getJSONArray("products");
					List<DTO> productList = new ArrayList<DTO>();
					for (int j = 0; j < productArray.length(); j++) {
						JSONObject productRow = productArray.getJSONObject(j);
						ProductDTO productDTO = new ProductDTO();
						try {
							productDTO.setProductId(productRow.getString("barcode"));
							productDTO.setModifiedDate(productRow.getString("modified_date"));
							productDTO.setCreateDate(productRow.getString("create_date"));
							productDTO.setName(productRow.getString("name"));
							productDTO.setSupplierId(productRow.getString("supplier_code"));
							productDTO.setBarcode(productRow.getString("barcode"));
							productDTO.setLineId(productRow.getString("line_id"));

							if (productRow.getString("uom").equals("0")) {
								productDTO.setUom("");
							} else {
								productDTO.setUom(productRow.getString("uom"));
							}
							productDTO.setSellingPrice("" + productRow.getDouble("selling_price"));
							productDTO.setGroupId(productRow.getString("group_id"));
							productDTO.setPurchasePrice("" + productRow.getDouble("purchase_price"));
							productDTO.setVat(productRow.getString("vat"));
							productDTO.setQuantity("" + productRow.getLong("quantity"));
							try {
								if ("" != productRow.getString("barcode")) {
									productList.add(productDTO);
								}
							} catch (Exception e) {
							}
						} catch (Exception e) {

						}
					}
					supplierDTO.setProductsList(productList);
				} catch (Exception e) {
				}

				if ("" != getSupplier.getString("supplier_code")) {
					historyDetails.add(supplierDTO);
				}
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
	}

}