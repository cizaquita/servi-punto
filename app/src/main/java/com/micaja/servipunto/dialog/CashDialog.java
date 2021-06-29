/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.CashFlowDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dto.CashFlowDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class CashDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtbal, etxtReason;
	private Button btnSave, btnCancel;
	private TextView dialogTitle, txtBalance, txtReason;
	private String type;
	private OnDialogSaveClickListener1 listener;
	private ImageView imgClose;

	private RelativeLayout layoutHeader;
	private Double amount = 0.0;
	private String reason, cashAmount;
	private boolean isValid;
	public SharedPreferences sharedpreferences;

	public CashDialog(Context context, String type) {
		super(context);
		this.context = context;
		this.type = type;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");
	}

	public void setOnDialogSaveClickListener(OnDialogSaveClickListener1 listener) {
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {

		setContentView(R.layout.cash_dialog);

		etxtbal = (EditText) findViewById(R.id.etxt_bal);
		etxtReason = (EditText) findViewById(R.id.etxt_reason);
		dialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtBalance = (TextView) findViewById(R.id.txt_bal);
		txtReason = (TextView) findViewById(R.id.txt_reason);

		btnSave = (Button) findViewById(R.id.btn_dialog_save);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		imgClose = (ImageView) findViewById(R.id.img_close);

		layoutHeader = (RelativeLayout) findViewById(R.id.layout_header);

		btnSave.setBackgroundResource(R.drawable.cash_innerbtn_bg);
		btnCancel.setBackgroundResource(R.drawable.cancel_popup_btn_bg);

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);
		etxtbal.setFilters(new InputFilter[] { new DecimalInputFilter() });

		loadUI();

	}

	private void loadUI() {

		dialogTitle.setText(type);
//		layoutHeader.setBackgroundColor(context.getResources().getColor(
//				R.color.cash_popup_header_color));
		txtBalance.setText(context.getResources().getString(R.string.value));
		txtReason.setText(context.getResources().getString(R.string.reason));

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_dialog_save:
			validateAndSave();
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

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer))
			CommonMethods.showCustomToast(context, stringBuffer.toString());
		else if (CashFlowDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getDataFromViews())) {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new ClientPayListSyncservice().execute();
			} else {
				dismiss();
				listener.onSaveClick();
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}

		} else {
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.not_added));

		}
	}

	private boolean validateFields(StringBuffer stringBuffer) {
		isValid = true;
		reason = etxtReason.getText().toString().trim();
		cashAmount = etxtbal.getText().toString().trim();
		if (cashAmount.length() == 0 && reason.length() == 0) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.cash_warn_msg));
		} else if (reason.length() == 0) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.enter_reason));
		} else if ("0".equals(cashAmount) || cashAmount.length() == 0) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.enter_valid_value));
		} else if (".".equals(cashAmount)) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.enter_valid_value));

		}

		return isValid;

	}

	private List<DTO> getDataFromViews() {

		List<DTO> cashFlow = new ArrayList<DTO>();

		CashFlowDTO cashDTO = new CashFlowDTO();

		cashDTO.setAmount(etxtbal.getText().toString().trim());
		cashDTO.setReason(etxtReason.getText().toString().trim());
		cashDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		cashDTO.setSyncStatus(0);

		if (type.equals(context.getResources().getString(
				R.string.btn_cashWithdraw))) {

			cashDTO.setAmountType("cash withdraw");

		} else {
			cashDTO.setAmountType("cash Deposit");

		}
		cashFlow.add(cashDTO);
		return cashFlow;

	}

	private String makejsonobj() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("amount", etxtbal.getText().toString().trim());
			jsonobj.put("store_code", ServiApplication.store_id);
			jsonobj.put("reason", etxtReason.getText().toString().trim());
			return jsonobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;

	}

	private void getCallTransaccionBoxService(String module_nem, String tipo,
			String PaymentType) {
		Log.v("varahalababu", "transation box if");
		List<DTO> dto = new ArrayList<DTO>();
		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(etxtbal.getText().toString().trim());
		tdto.setModule_name(module_nem);
		tdto.setSyncstatus(0);

		tdto.setTipo_transction(tipo);
		tdto.setTransaction_type(PaymentType);
		Log.v("varahalababu", "transation box if 2");
		tdto.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		Log.v("varahalababu", "transation box if 3");
		tdto.setUsername(sharedpreferences.getString("user_name", ""));
		tdto.setStore_code(sharedpreferences.getString("store_code", ""));
		Log.v("varahalababu", "transation box if 4");
		dto.add(tdto);
		TransaccionBoxDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), dto);
	}

	/* ClientPayments sync service call 4 */
	private class ClientPayListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(context);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(Void... params) {
			CashFlowDTO cashDTO = new CashFlowDTO();

			cashDTO.setAmount(etxtbal.getText().toString().trim());
			cashDTO.setReason(etxtReason.getText().toString().trim());
			cashDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
			cashDTO.setSyncStatus(0);

			if (type.equals(context.getResources().getString(
					R.string.btn_cashWithdraw))) {
				getCallTransaccionBoxService(
						ServiApplication.Cashflow_Withdraw_M_name,
						ServiApplication.Cashflow_Withdraw_TipoTrans,
						ServiApplication.Cashflow_Withdraw_PaymentType);
				cashDTO.setAmountType("cash withdraw");
				ServiApplication.responds_feed = servicehandler
						.makeServiceCall(ServiApplication.URL
								+ "/cash/withdraw", ServiceHandler.POST,
								makejsonobj());

				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					cashDTO.setSyncStatus(1);
					CashFlowDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE), cashDTO);
				}

			} else {
				cashDTO.setAmountType("cash Deposit");
				getCallTransaccionBoxService(
						ServiApplication.Cashflow_Disposit_M_name,
						ServiApplication.Cashflow_Disposit_M_name,
						ServiApplication.Cashflow_Disposit_PaymentType);
				ServiApplication.responds_feed = servicehandler
						.makeServiceCall(
								ServiApplication.URL + "/cash/deposit",
								ServiceHandler.POST, makejsonobj());

				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					cashDTO.setSyncStatus(1);
					CashFlowDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE), cashDTO);
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.added_success));
			listener.onSaveClick();
			dismiss();
		}
	}
}
