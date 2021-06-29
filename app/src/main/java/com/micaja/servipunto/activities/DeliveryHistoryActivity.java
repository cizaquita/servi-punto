/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

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
import com.micaja.servipunto.adapters.DeliveryHistoryAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DeliveryHistoryDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class DeliveryHistoryActivity extends BaseActivity {
	private ListView lvClient;

	private DeliveryHistoryAdapter adapter;
	private TextView txtClientName, txtPhoneNumber;

	ServiApplication appContext;
	private Intent intent;
	private String deliveryID = "", name, phoneNum;
	private int customer_balance;
	private int ih_req_count = 1;
	public ArrayList<DeliveryHistoryDTO> historyDetails = new ArrayList<DeliveryHistoryDTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, DeliveryActivity.class);
		inItUI();
	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.delivery_history_activity);
		deliveryID = getIntent().getExtras().getString(
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


		if (NetworkConnectivity.netWorkAvailability(DeliveryHistoryActivity.this)) {
			new GetInventoryHistory().execute();
		} else {
			CommonMethods.showCustomToast(DeliveryHistoryActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	// Result of this method, customer history details bind to customer adapter
	private void setDataToViews() {
		ServiApplication.deliveryHistoryDTO.clear();


		ServiApplication.deliveryHistoryDTO = historyDetails;

		if (ServiApplication.deliveryHistoryDTO.size() > 0) {

			Collections.sort(ServiApplication.deliveryHistoryDTO);

			adapter = new DeliveryHistoryAdapter(this,
					ServiApplication.deliveryHistoryDTO,deliveryID);
			lvClient.setAdapter(adapter);
			lvClient.invalidateViews();
		} else {
			CommonMethods.showToast(this,
					getResources().getString(R.string.nodata));
		}
	}

	public class GetInventoryHistory extends AsyncTask<Void, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(
				DeliveryHistoryActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					DeliveryHistoryActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {

			UserDetailsDTO userDTO = UserDetailsDAO.getInstance()
					.getUserRecords(
							DBHandler.getDBObj(Constants.READABLE));
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/delivery/historic-debt/" + deliveryID + "/"+ userDTO.getNitShopId() +"?page="
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
			JSONObject customerJSONObj = datajsonobj.getJSONObject("delivery");

			DeliveryHistoryDTO deliveryHistoryDTO = new DeliveryHistoryDTO(
					"delivery", customerJSONObj.getString("initial_debts"),"",
					Dates.serverdateformateDateObj(customerJSONObj
							.getString("created_date")), "00");
			historyDetails.add(deliveryHistoryDTO);

			JSONObject balenceJSONObj = datajsonobj.getJSONObject("delivery");
			DeliveryHistoryDTO balenceJSONObjHistoryDto = new DeliveryHistoryDTO(
					"balence", balenceJSONObj.getString("balance_amount"),"",
					new Date(), "00");

			historyDetails.add(balenceJSONObjHistoryDto);


			JSONArray paymentsDataArray = datajsonobj
					.getJSONArray("payments_data");
			for (int i = 0; i < paymentsDataArray.length(); i++) {
				JSONObject paymentsDataJSONObj = paymentsDataArray
						.getJSONObject(i);
				try {
					DeliveryHistoryDTO paymentsDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"payments_data",
							paymentsDataJSONObj.getString("amount_paid"),"0.0",
							Dates.serverdateformateDateObj(paymentsDataJSONObj
									.getString("date_time")), ""
									+ paymentsDataJSONObj.getLong("invoice_no"));
					historyDetails.add(paymentsDataDeliveryHistoryDto);
				} catch (Exception e) {
					DeliveryHistoryDTO paymentsDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"payments_data",
							paymentsDataJSONObj.getString("amount_paid"),"",
							Dates.serverdateformateDateObj(paymentsDataJSONObj
									.getString("date_time")), "00");
					historyDetails.add(paymentsDataDeliveryHistoryDto);
				}

			}

			JSONArray lendDataArray = datajsonobj.getJSONArray("lend_data");
			for (int i = 0; i < lendDataArray.length(); i++) {
				JSONObject lendDataJSONObj = lendDataArray.getJSONObject(i);
				try {
				DeliveryHistoryDTO lendDataDeliveryHistoryDto = new DeliveryHistoryDTO(
						"lend_data","0.0", lendDataJSONObj.getString("amount"),
						Dates.serverdateformateDateObj(lendDataJSONObj
								.getString("date_time")), "" + lendDataJSONObj.getLong("invoice_no"));
					foundSaleID(lendDataDeliveryHistoryDto);
				//historyDetails.add(lendDataDeliveryHistoryDto);


				} catch (Exception e) {
					DeliveryHistoryDTO paymentsDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"lend_data",
							"",lendDataJSONObj.getString("amount"),
							Dates.serverdateformateDateObj(lendDataJSONObj
									.getString("date_time")), "00");
					historyDetails.add(paymentsDataDeliveryHistoryDto);
				}

			}

		} catch (JSONException e) {
		}

		Log.v("varahalababu", "====count======" + historyDetails.size());
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(DeliveryHistoryActivity.this, DeliveryActivity.class);
		startActivity(intent);
		this.finish();
	}

	private void foundSaleID (DeliveryHistoryDTO deliveryHistoryDTO){

		DeliveryHistoryDTO deliveryHistoryDTO1;
		for (int i = 0; i < historyDetails.size(); i++){
			deliveryHistoryDTO1 = historyDetails.get(i);
			if (deliveryHistoryDTO.getSale_id().equals(deliveryHistoryDTO1.getSale_id())){
				deliveryHistoryDTO1.setDeb_amount(deliveryHistoryDTO.getDeb_amount());
				historyDetails.set(i,deliveryHistoryDTO1);
				break;
				}
			}
	}

}
