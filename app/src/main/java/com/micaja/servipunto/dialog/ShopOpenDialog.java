/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.ShopOpenCloseActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShopOpenDialog extends Dialog
		implements android.view.View.OnClickListener, android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtbal;
	private Button btnSave, btnCancel;
	private TextView dialogTitle, txtBalance;
	private LinearLayout layoutReason;
	private UserDetailsDTO userDto = new UserDetailsDTO();
	private RelativeLayout layoutHeader;
	private ImageView imgClose;
	public SharedPreferences sharedpreferences;

	public ShopOpenDialog(Context context, UserDetailsDTO userDto) {
		super(context);
		this.context = context;
		this.userDto = userDto;
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		initUI();

	}

	// Result of this method,registration for all UI views.
	private void initUI() {

		setContentView(R.layout.cash_dialog);

		etxtbal = (EditText) findViewById(R.id.etxt_bal);
		dialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtBalance = (TextView) findViewById(R.id.txt_bal);
		layoutReason = (LinearLayout) findViewById(R.id.layout_reason);
		layoutHeader = (RelativeLayout) findViewById(R.id.layout_header);
		btnSave = (Button) findViewById(R.id.btn_dialog_save);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		imgClose = (ImageView) findViewById(R.id.img_close);

		btnSave.setBackgroundResource(R.drawable.cash_button);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		imgClose.setOnClickListener(this);

		etxtbal.setFilters(new InputFilter[] { new DecimalInputFilter() });

		loadUI();

	}

	// laod screen background color
	private void loadUI() {
		layoutHeader.setBackgroundColor(context.getResources().getColor(R.color.customer_tab));
		dialogTitle.setText(context.getResources().getString(R.string.shop_dialog_title));
		txtBalance.setText(context.getResources().getString(R.string.amount));
		layoutReason.setVisibility(View.GONE);

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_dialog_save:

			String bal = etxtbal.getText().toString().trim();
			if (bal.length() > 0 && !".".equals(bal) && !"".equals(bal)) {
				if (UserDetailsDAO.getInstance().updateShopOpenInfoDB(DBHandler.getDBObj(Constants.WRITABLE),
						getuserData())) {
					getCallTransaccionBoxService(ServiApplication.Shop_Open_M_name,
							ServiApplication.Shop_Open_TipoTrans, ServiApplication.Shop_Open_PaymentType);

					if (NetworkConnectivity.netWorkAvailability(context)) {
						new AddOrUpdateCustomer().execute("Update");
					} else {
						Intent intent = new Intent(context, ShopOpenCloseActivity.class);
						context.startActivity(intent);
						this.dismiss();
					}

				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));
				}

			} else {
				CommonMethods.showCustomToast(context, context.getResources().getString(R.string.pay_warn_msg));
			}
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

	// get data from User DB
	private UserDetailsDTO getuserData() {

		userDto.setIsClosed(Constants.FALSE);
		userDto.setOpeningBalance(etxtbal.getText().toString().trim());
		userDto.setOpeningDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		userDto.setSyncStatus(Constants.TRUE);
		userDto.setCloseDateTime(userDto.getCloseDateTime());

		return userDto;
	}

	// make a json object to user update
	private String makejsonobj(String opening_date_time) {
		// TODO Auto-generated method stub
		UserDetailsDTO user = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "user");
			jsonobj.put("type", "update");
			JSONArray jsonarry = new JSONArray();
			jsonarry.put("username");
			jsonobj.put("columns", jsonarry);
			JSONArray json_arry = new JSONArray();
			jsonobj.put("records", json_arry);

			JSONObject json_obj = new JSONObject();
			json_obj.put("opening_date_time", opening_date_time);
			json_obj.put("username", sharedpreferences.getString("user_name", ""));
			json_obj.put("opening_balance", CommonMethods.getDoubleFormate(user.getOpeningBalance()));
			json_obj.put("isclosed", "" + Constants.FALSE);
			json_arry.put(json_obj);
			return jsonobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Result of this method,shopOpen table data inserting to Transaccion box
	@SuppressWarnings("unused")
	private void getCallTransaccionBoxService(String module_nem, String tipo, String PaymentType) {
		List<DTO> dto = new ArrayList<DTO>();
		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(etxtbal.getText().toString().trim());
		tdto.setModule_name(module_nem);
		tdto.setSyncstatus(0);
		tdto.setTipo_transction(tipo);
		tdto.setTransaction_type(PaymentType);
		tdto.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		tdto.setUsername(sharedpreferences.getString("user_name", ""));
		tdto.setStore_code(sharedpreferences.getString("store_code", ""));
		dto.add(tdto);
		if (TransaccionBoxDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			if (CommonMethods.getInternetSpeed(context) >= ServiApplication.local_data_speed) {
				Intent intent = new Intent(context, TransaccionBoxService.class);
				context.startService(intent);
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
		}
	}

	public class AddOrUpdateCustomer extends AsyncTask<String, Void, Void> {
		String responds, params_string;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getResources().getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(String... params) {
			ServiApplication.responds_feed = new ServiceHandler(context).makeServiceCall(ServiApplication.URL + "/sync",
					ServiceHandler.POST, makejsonobj(new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date())));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			CommonMethods.dismissProgressDialog();

			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					ServiApplication.shope_open_alert = true;
					Intent intent = new Intent(context, ShopOpenCloseActivity.class);
					context.startActivity(intent);
					dismiss();
				} else {
					ServiApplication.shope_open_alert = true;
					Intent intent = new Intent(context, ShopOpenCloseActivity.class);
					context.startActivity(intent);
					dismiss();
					CommonMethods.showToast(context, new JSONStatus().message(ServiApplication.responds_feed));
				}
			}else{
				ServiApplication.shope_open_alert = true;
				Intent intent = new Intent(context, ShopOpenCloseActivity.class);
				context.startActivity(intent);
				dismiss();
			}

		}

	}

}
