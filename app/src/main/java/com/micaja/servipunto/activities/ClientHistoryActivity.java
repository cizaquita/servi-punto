/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.ClientHistoryAdapter;
import com.micaja.servipunto.database.dto.ClientHistoryDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class ClientHistoryActivity extends BaseActivity {

	private ListView lvClient;
	private ClientHistoryAdapter adapter;
	private TextView txtClientName, txtPhoneNumber;
	private ServiApplication appContext;
	private Intent intent;
	private String clientID = "", name, phoneNum;
	private int customer_balance;
	private int ih_req_count = 1;
	private ArrayList<ClientHistoryDTO> historyDetails = new ArrayList<ClientHistoryDTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, ClientsActivity.class);
		inItUI();
	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.history_activity);
		clientID = getIntent().getExtras().getString(
				ConstantsEnum.CLIENT_ID.code());
		name = getIntent().getExtras().getString(
				ConstantsEnum.CLIENT_NAME.code());
		phoneNum = getIntent().getExtras().getString(
				ConstantsEnum.CLIENT_TELEPHONE.code());
		lvClient = (ListView) findViewById(R.id.lv_Client);
		txtClientName = (TextView) findViewById(R.id.txt_clientname);
		txtPhoneNumber = (TextView) findViewById(R.id.txt_phonenumber);
		getDataFromDB();
	}

	// Result of this method,showing customer debt history details
	private void getDataFromDB() {
		txtClientName.setText(name);
		txtPhoneNumber.setText(phoneNum);


		if (NetworkConnectivity.netWorkAvailability(ClientHistoryActivity.this)) {
			new GetInventoryHistory().execute();
		} else {
			CommonMethods.showCustomToast(ClientHistoryActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	// Result of this method, customer history details bind to customer adapter
	private void setDataToViews() {
		ServiApplication.clientHistoryDTO.clear();
		ServiApplication.clientHistoryDTO = historyDetails;
		if (ServiApplication.clientHistoryDTO.size() > 0) {
			Collections.sort(ServiApplication.clientHistoryDTO);
			adapter = new ClientHistoryAdapter(this,
					ServiApplication.clientHistoryDTO);
			lvClient.setAdapter(adapter);
			lvClient.invalidateViews();
		} else {
			CommonMethods.showToast(this,
					getResources().getString(R.string.nodata));
		}
	}

	public class GetInventoryHistory extends AsyncTask<Void, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(
				ClientHistoryActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					ClientHistoryActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/customer/historic-debt/" + clientID + "?page="
					+ ih_req_count, ServiceHandler.GET);
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
						JSONObject json1 = json.getJSONObject("data");
						JSONObject pagenation = json1
								.getJSONObject("paginator");
						ih_req_count = pagenation.getInt("next_page");
						getInventoryInvoiceDTOData(responds);
						getDataFromDB();
					} catch (Exception e) {
						System.out.println("==========================");
						getInventoryInvoiceDTOData(responds);
						setDataToViews();
					}
				}
			}
		}
	}

	private void getInventoryInvoiceDTOData(String responds2) {
		
		try {
			
			JSONObject json = new JSONObject(responds2);
			JSONObject datajsonobj = json.getJSONObject("data");
			JSONObject customerJSONObj = datajsonobj.getJSONObject("customer");

			ClientHistoryDTO clientHistoryDto = new ClientHistoryDTO(
					"customer", customerJSONObj.getString("initial_debts"),
					Dates.serverdateformateDateObj(customerJSONObj
							.getString("created_date")), "00");
			historyDetails.add(clientHistoryDto);

			JSONObject balenceJSONObj = datajsonobj.getJSONObject("customer");
			ClientHistoryDTO balenceJSONObjHistoryDto = new ClientHistoryDTO(
					"balence", balenceJSONObj.getString("balance_amount"),
					new Date(), "00");

			historyDetails.add(balenceJSONObjHistoryDto);

			JSONArray paymentsDataArray = datajsonobj
					.getJSONArray("payments_data");
			for (int i = 0; i < paymentsDataArray.length(); i++) {
				JSONObject paymentsDataJSONObj = paymentsDataArray
						.getJSONObject(i);
				try {
					ClientHistoryDTO paymentsDataClientHistoryDto = new ClientHistoryDTO(
							"payments_data",
							paymentsDataJSONObj.getString("amount_paid"),
							Dates.serverdateformateDateObj(paymentsDataJSONObj
									.getString("date_time")), ""
									+ paymentsDataJSONObj.getLong("sale_id"));
					historyDetails.add(paymentsDataClientHistoryDto);
				} catch (Exception e) {
					ClientHistoryDTO paymentsDataClientHistoryDto = new ClientHistoryDTO(
							"payments_data",
							paymentsDataJSONObj.getString("amount_paid"),
							Dates.serverdateformateDateObj(paymentsDataJSONObj
									.getString("date_time")), "00");
					historyDetails.add(paymentsDataClientHistoryDto);
				}

			}

			JSONArray lendDataArray = datajsonobj.getJSONArray("lend_data");
			for (int i = 0; i < lendDataArray.length(); i++) {
				JSONObject lendDataJSONObj = lendDataArray.getJSONObject(i);
				ClientHistoryDTO lendDataClientHistoryDto = new ClientHistoryDTO(
						"lend_data", lendDataJSONObj.getString("amount"),
						Dates.serverdateformateDateObj(lendDataJSONObj
								.getString("date_time")), "");
				historyDetails.add(lendDataClientHistoryDto);
			}

		} catch (JSONException e) {
		}

		Log.v("varahalababu", "====count======" + historyDetails.size());
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(ClientHistoryActivity.this, ClientsActivity.class);
		startActivity(intent);
		this.finish();
	}

}
