/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.InventoryHistoryAdapter;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryInvoiceDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InventoryHistoryActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private ListView lvInventory;
	private RelativeLayout nexpage_layout;
	private InventoryHistoryAdapter invAdapter;
	private ServiApplication appContext;
	private Intent intent;
	private static EditText etxtFromDate, etxtToDate;
	List<DTO> prodDetails = new ArrayList<DTO>();
	private static View dateView;
	private Button ivGo;
	private TextView previas, next;

	private static Date frome_date, two_date;
	private boolean b = false, listview_flage = false;
	private int ih_req_count = 1, ih_req_previas=1;
	List<DTO> list = new ArrayList<DTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, InventoryHistoryActivity.class);

		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.inventory_history);
		lvInventory = (ListView) findViewById(R.id.lv_Inventory);
		etxtFromDate = (EditText) findViewById(R.id.img_fromdate);
		etxtFromDate.setOnClickListener(this);
		etxtToDate = (EditText) findViewById(R.id.img_todate);
		etxtToDate.setOnClickListener(this);
		ivGo = (Button) findViewById(R.id.img_go);
		ivGo.setOnClickListener(this);
		lvInventory.setOnItemClickListener(this);
		nexpage_layout = (RelativeLayout) findViewById(R.id.nexpage_layout);

		previas = (TextView) findViewById(R.id.previas);
		next = (TextView) findViewById(R.id.next);
		ih_req_previas=ServiApplication.getPrevius();
		ih_req_count=ServiApplication.getNext();
		if (ih_req_previas!=1||ih_req_count!=1) {
			
			if (ih_req_previas!=1 ||ih_req_previas!=0) {
				previas.setVisibility(View.VISIBLE);
				previas.setText(getResources().getString(R.string.previas) + " " + ih_req_previas);
			}else {
				previas.setVisibility(View.GONE);
			}
			
			if (ih_req_count!=1 ||ih_req_count!=0) {
				next.setText(getResources().getString(R.string.next) + " " + ih_req_count);
				next.setVisibility(View.VISIBLE);
			}else {
				next.setVisibility(View.GONE);
			}
			nexpage_layout.setVisibility(View.VISIBLE);
			
		}else {
			nexpage_layout.setVisibility(View.GONE);
		}
		previas.setOnClickListener(this);
		next.setOnClickListener(this);

		if (ServiApplication.inventory_history_flag) {
			invAdapter = new InventoryHistoryAdapter(InventoryHistoryActivity.this,
					appContext.getInventoryHistoryList());
			lvInventory.setAdapter(invAdapter);
		} else {
			getDataFromServer();
		}

		// lvInventory.setOnScrollListener(new OnScrollListener()
		// {
		// @Override
		// public void onScrollStateChanged(AbsListView arg0, int arg1)
		// {
		// // nothing here
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem, int
		// visibleItemCount, int totalItemCount)
		// {
		// if (listview_flage)
		// {
		// if (NetworkConnectivity
		// .netWorkAvailability(InventoryHistoryActivity.this)) {
		// new GetInventoryHistory().execute();
		// } else {
		// CommonMethods.showCustomToast(InventoryHistoryActivity.this,
		// getResources().getString(R.string.no_wifi_adhoc));
		// }
		// }
		// }
		// });

	}

	// Result of this method,checking for Network availability and call Getting
	// inventory history service
	private void getDataFromServer() {
		// setInventoryHistory();

		if (NetworkConnectivity.netWorkAvailability(InventoryHistoryActivity.this)) {
			new GetInventoryHistory().execute();
		} else {
			CommonMethods.showCustomToast(InventoryHistoryActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}

		// invAdapter = new InventoryHistoryAdapter(this,
		// appContext.getInventoryHistoryList());
		// lvInventory.setAdapter(invAdapter);
	}

	@Override
	public void onClick(View v) {

		dateView = v;
		DatePicker datePicker = new DatePicker();
		switch (v.getId()) {

		case R.id.previas:
			ih_req_count=ih_req_previas;
			getDataFromServer();
			break;

		case R.id.next:
			getDataFromServer();
			break;

		case R.id.img_fromdate:
			datePicker.show(getFragmentManager(), "datePicker");
			break;

		case R.id.img_todate:
			datePicker.show(getFragmentManager(), "datePickerEnd");
			break;

		case R.id.img_go:

			String edate = etxtFromDate.getText().toString().trim();
			String tdate = etxtToDate.getText().toString().trim();
			int retVal = edate.compareTo(tdate);
			try {

				if (etxtFromDate.getText().toString().length() == 0 || etxtToDate.getText().toString().length() == 0) {
					CommonMethods.showToast(this, getResources().getString(R.string.select_date));
				} else if (retVal > 0) {
					CommonMethods.showToast(this, getResources().getString(R.string.invalid_end_date));
					lvInventory.setAdapter(null);

				} else {

					searchByDates(etxtFromDate.getText().toString(), etxtToDate.getEditableText().toString());
				}
			} catch (Exception e) {
			}

			break;

		default:
			break;
		}
	}

	// Result of this method,checking for Network availability and call Getting
	// inventory history service based on Dates
	private void searchByDates(String fromDate, String twoDate) {
		if (NetworkConnectivity.netWorkAvailability(InventoryHistoryActivity.this)) {
			nexpage_layout.setVisibility(View.GONE);
			new GetInventorySearchByDates().execute(fromDate, twoDate);
		} else {
			CommonMethods.showCustomToast(InventoryHistoryActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	public class GetInventorySearchByDates extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(InventoryHistoryActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), InventoryHistoryActivity.this);
			lvInventory.setAdapter(null);

		}

		@Override
		protected Void doInBackground(String... params) {
			responds = servicehandler.makeServiceCall(ServiApplication.URL + "/inventory-history-by-dates",
					ServiceHandler.POST, makejsonobj(params[0], params[1]));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(responds) == 0) {
					List<DTO> list = new ParsingHandler().getInventoryInvoiceDTOData(responds);
					Log.v("Varahalababu", "" + list.size());
					appContext.setInventoryHistoryList(list);
					invAdapter = new InventoryHistoryAdapter(InventoryHistoryActivity.this,
							appContext.getInventoryHistoryList());
					lvInventory.setAdapter(invAdapter);
					ServiApplication.inventory_history_flag = true;
				}

			}
			CommonMethods.dismissProgressDialog();

		}

	}

	// Result of this method,Choose dates from Datepicker
	public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;

			if (dateView.getId() == R.id.img_fromdate) {
				etxtFromDate.setText(date);
				frome_date = Dates.getdate(date);
			} else {
				etxtToDate.setText(date);
				two_date = Dates.getdate(date);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		appContext.setSeletedSupplier(String.valueOf(position));
		appContext.pushActivity(intent);
		CommonMethods.openNewActivity(InventoryHistoryActivity.this, InventoryDetailHistoryActivity.class);
		this.finish();
	}

	public class GetInventoryHistory extends AsyncTask<Void, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(InventoryHistoryActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), InventoryHistoryActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			responds = servicehandler.makeServiceCall(
					ServiApplication.URL + "/inventory-history/" + ServiApplication.store_id + "?page=" + ih_req_count,
					ServiceHandler.GET);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			nexpage_layout.setVisibility(View.VISIBLE);
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(responds) == 0) {
					try {
						getInventoryInvoiceDTOData(responds);
						appContext.setInventoryHistoryList(list);
						invAdapter = new InventoryHistoryAdapter(InventoryHistoryActivity.this,
								appContext.getInventoryHistoryList());
						lvInventory.setAdapter(invAdapter);
						ServiApplication.inventory_history_flag = true;
						JSONObject json = new JSONObject(responds);
						JSONObject pagenation = json.getJSONObject("paginator");
						try {
							ih_req_previas = pagenation.getInt("previous_page");
							ServiApplication.setPrevius(ih_req_previas);
							previas.setText(getResources().getString(R.string.previas) + " " + ih_req_previas);
							previas.setVisibility(View.VISIBLE);
						} catch (Exception e) {
							previas.setVisibility(View.GONE);
						}
						next.setVisibility(View.VISIBLE);
						ih_req_count = pagenation.getInt("next_page");
						ServiApplication.setNext(ih_req_count);
						next.setText(getResources().getString(R.string.next) + " " + ih_req_count);
						listview_flage = true;
						// getDataFromServer();
					} catch (Exception e) {
						next.setVisibility(View.GONE);
						getInventoryInvoiceDTOData(responds);
						appContext.setInventoryHistoryList(list);
						invAdapter = new InventoryHistoryAdapter(InventoryHistoryActivity.this,
								appContext.getInventoryHistoryList());
						lvInventory.setAdapter(invAdapter);
						ServiApplication.inventory_history_flag = true;
						listview_flage = false;
					}
				}
			}else {
				nexpage_layout.setVisibility(View.GONE);
			}
		}

		private void getInventoryInvoiceDTOData(String responds2) {
			
			list.clear();
			try {
				JSONObject jsonobj = new JSONObject(responds2);
				JSONArray data = jsonobj.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject data_jsonobj = data.getJSONObject(i);
					InventoryInvoiceDTO iid = new InventoryInvoiceDTO();
					iid.setInvoiceNum(data_jsonobj.getString("invoice_number"));
					iid.setSupplierName(data_jsonobj.getString("supplier_name"));
					iid.setTotalAmount(" " + data_jsonobj.getLong("total_amt"));
					List<DTO> listi = new ArrayList<DTO>();
					try {
						JSONArray productarray = data_jsonobj.getJSONArray("products");
						for (int j = 0; j < productarray.length(); j++) {
							ProductDTO dto = new ProductDTO();
							JSONObject pjsonobj = productarray.getJSONObject(j);
							dto.setBarcode(pjsonobj.getString("barcode"));
							dto.setProductId(pjsonobj.getString("barcode"));
							dto.setName(pjsonobj.getString("name"));
							dto.setSupplierId(pjsonobj.getString("supplier_code"));
							dto.setQuantity(pjsonobj.getString("quantity"));
							dto.setSellingPrice("" + pjsonobj.getDouble("selling_price"));
							listi.add(dto);
						}
					} catch (Exception e) {
					}
					iid.setProductsList(listi);
					list.add(iid);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Log.v("varahalababu", "====count======" + list.size());
		}

	}

	// This method using for inventory history based on dates that time data
	// passing
	// to the json objects request
	private String makejsonobj(String fromDate, String toDate) {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("from_date", fromDate);
			jsonObject.put("to_date", toDate);
			jsonObject.put("storecode", ServiApplication.store_id);
			return jsonObject.toString();

		} catch (JSONException e) {
			return jsonObject.toString();
		}

	}

}