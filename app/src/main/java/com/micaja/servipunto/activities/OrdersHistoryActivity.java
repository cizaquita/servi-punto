package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.OrdersHistoryAdapter;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class OrdersHistoryActivity extends BaseActivity implements
		OnClickListener {
	private Button btnSuggested, btnHistory, btnAddCart;
	private ListView lvProductList;
	private TextView txtUnits, txtTitle, txtPrice, txtQuantity, txtName,
			txtInvoice, txt_OrdersHisInvoiceNo;
	private View viewDivide, view_id;
	private static EditText etxtFromDate, etxtToDate;
	private Button ivGo;
	private static View dateView;
	private OrdersHistoryAdapter ordAdapter;
	private int ih_req_count = 1;
	Intent intent;
	List<DTO> productList = new ArrayList<DTO>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		intent = new Intent(this, OrdersProductsActivity.class);
		appContext = (ServiApplication) getApplicationContext();

		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.orders_history);

		btnSuggested = (Button) findViewById(R.id.btn_OrdersSuggested);
		btnHistory = (Button) findViewById(R.id.btn_OrdersHistory);
		btnAddCart = (Button) findViewById(R.id.btn_OrdersCart);
		txtUnits = (TextView) findViewById(R.id.txt_OrdersHisUnits);
		viewDivide = findViewById(R.id.view_divid);

		txtName = (TextView) findViewById(R.id.txt_OrdersHisName);

		txtTitle = (TextView) findViewById(R.id.txt_HeaderTitle);
		txtPrice = (TextView) findViewById(R.id.txt_OrdersHisPrice);
		txtQuantity = (TextView) findViewById(R.id.txt_OrdersHisQty);
		txtTitle.setText(getResources().getString(R.string.order_history));
		txtInvoice = (TextView) findViewById(R.id.txt_OrdersHisInvoiceNo);

		txtName.setText(getResources().getString(R.string.profit_date));
		txtPrice.setText(getResources().getString(R.string.product));
		txtQuantity.setText(getResources().getString(R.string.price));
		txtUnits.setText(getResources().getString(R.string.orders_status));

		lvProductList = (ListView) findViewById(R.id.lv_OrdersHistory);

		view_id = (View) findViewById(R.id.view_id);
		txt_OrdersHisInvoiceNo = (TextView) findViewById(R.id.txt_OrdersHisInvoiceNo);

		etxtFromDate = (EditText) findViewById(R.id.img_fromdate);
		etxtToDate = (EditText) findViewById(R.id.img_todate);
		ivGo = (Button) findViewById(R.id.img_go);

		etxtFromDate.setOnClickListener(this);
		etxtToDate.setOnClickListener(this);
		ivGo.setOnClickListener(this);

		btnSuggested.setVisibility(View.GONE);
		btnHistory.setVisibility(View.GONE);
		btnAddCart.setVisibility(View.GONE);
		viewDivide.setVisibility(View.VISIBLE);

		view_id.setVisibility(View.VISIBLE);
		txt_OrdersHisInvoiceNo.setVisibility(View.VISIBLE);
		// txtUnits.setVisibility(View.GONE);

		etxtFromDate.setVisibility(View.VISIBLE);
		etxtToDate.setVisibility(View.VISIBLE);
		ivGo.setVisibility(View.VISIBLE);
		loadUI();
	}

	private void loadUI() {
		// if (new AsynkTaskClass().callcentralserver("/orders/historic", ""
		// + ServiceHandler.POST, makeJsonObj(),
		// OrdersHistoryActivity.this) != null) {
		// if (ServiApplication.responds_feed.length() > 0) {
		// lvProductList
		// .setAdapter(new OrdersHistoryAdapter(
		// OrdersHistoryActivity.this,
		// new ParsingHandler()
		// .getOrdersHistoricData(ServiApplication.responds_feed)));
		// }
		// } else {
		//
		// }

		if (NetworkConnectivity.netWorkAvailability(OrdersHistoryActivity.this)) {
			new GetOrdersHistory().execute();

		} else {
			CommonMethods.showCustomToast(OrdersHistoryActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	public class GetOrdersHistory extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(
				OrdersHistoryActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					OrdersHistoryActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			ServiApplication.responds_feed = servicehandler.makeServiceCall(
					ServiApplication.URL + "/orders/historic"+ "?page=" + ih_req_count,
					ServiceHandler.POST, makeJsonObj());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					
						try {
							JSONObject json=new JSONObject(ServiApplication.responds_feed);
							JSONObject json1=json.getJSONObject("paginator");
							ih_req_count=json1.getInt("next_page");
							getparsingdata(ServiApplication.responds_feed);
							lvProductList.setAdapter(new OrdersHistoryAdapter(OrdersHistoryActivity.this,productList));
							loadUI();
						} catch (Exception e) {
							getparsingdata(ServiApplication.responds_feed);
							lvProductList.setAdapter(new OrdersHistoryAdapter(OrdersHistoryActivity.this,productList));
						}
				} else {
					CommonMethods.showCustomToast(OrdersHistoryActivity.this,
							getResources().getString(R.string.nodata));
				}

			}
		}

	}

	private String makeJsonObj() {
		int position = Integer.parseInt(appContext.getSeletedSupplier());
		SupplierDTO dto = (SupplierDTO) appContext.getOrderSupplierList().get(
				position);
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("supplier_code", dto.getSupplierId());
			jsonobj.put("store_code", ServiApplication.store_id);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public void getparsingdata(String responds_feed) {


		
		try {
			JSONObject rootJSONObj = new JSONObject(responds_feed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				try {
					JSONObject productINFO = dataJSONARRAy.getJSONObject(i);

					JSONArray order_pro_jsonarray = productINFO.getJSONArray("order_details");
					for (int j = 0; j < order_pro_jsonarray.length(); j++) {
						JSONObject fproductINFO = order_pro_jsonarray.getJSONObject(j);
						ProductDTO userModuleIdDTO = new ProductDTO();
						try {
							userModuleIdDTO.setCreateDate(Dates.changeFormate(productINFO.getString("date_time")));
						} catch (Exception e) {
							userModuleIdDTO.setCreateDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
						}
						userModuleIdDTO.setName(fproductINFO.getString("barcode"));
						userModuleIdDTO.setPurchasePrice("" + fproductINFO.getDouble("price"));
						userModuleIdDTO.setSellingPrice(productINFO.getString("is_order_confirmed"));
						userModuleIdDTO.setQuantity(productINFO.getString("invoice_no"));
						productList.add(userModuleIdDTO);
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {

		dateView = v;
		DatePicker datePicker = new DatePicker();
		switch (v.getId()) {
		case R.id.img_fromdate:
			datePicker.show(getFragmentManager(), "datePicker");
			break;
		case R.id.img_todate:
			datePicker.show(getFragmentManager(), "datePickerEnd");
			break;
		case R.id.img_go:
			if (etxtFromDate.getText().toString().length() == 0
					|| etxtToDate.getText().toString().length() == 0) {
				CommonMethods.showToast(this,
						getResources().getString(R.string.select_date));
			} else {

				searchByDates(etxtFromDate.getText().toString(), etxtToDate
						.getEditableText().toString());
			}

			break;

		default:
			break;
		}

	}

	private void searchByDates(String fromDate, String twoDate) {
		if (NetworkConnectivity.netWorkAvailability(OrdersHistoryActivity.this)) {
			new GetOrdersSearchByDates().execute(fromDate, twoDate);
		} else {
			CommonMethods.showCustomToast(OrdersHistoryActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	public class GetOrdersSearchByDates extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(
				OrdersHistoryActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					OrdersHistoryActivity.this);
			lvProductList.setAdapter(null);
		}

		@Override
		protected Void doInBackground(String... params) {
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/orders/historic/get_by_dates", ServiceHandler.POST,
					makejsonobj2(params[0], params[1]));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(responds) == 0) {
					lvProductList
							.setAdapter(new OrdersHistoryAdapter(
									OrdersHistoryActivity.this,
									new ParsingHandler()
											.getOrdersHistoricData(responds)));
				} else {
					CommonMethods.showCustomToast(OrdersHistoryActivity.this,
							getResources().getString(R.string.nodata));
				}

			}
		}

	}

	public static class DatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;

			if (dateView.getId() == R.id.img_fromdate) {
				etxtFromDate.setText(date);
			} else {
				etxtToDate.setText(date);
			}
		}
	}

	public String makejsonobj2(String fromDate, String toDate) {

		int position = Integer.parseInt(appContext.getSeletedSupplier());
		SupplierDTO dto = (SupplierDTO) appContext.getOrderSupplierList().get(
				position);
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("store_code", ServiApplication.store_id);
			jsonObject.put("supplier_code", dto.getSupplierId());
			jsonObject.put("from_date", fromDate);
			jsonObject.put("to_date", toDate);
			return jsonObject.toString();

		} catch (JSONException e) {
			return jsonObject.toString();
		}

	}

}
