package com.micaja.servipunto.dialog;

import java.net.Proxy;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class RechargeDialog1 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {
	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;
	private EditText etxtKeyalue;
	private Button btn_reg_cancel, btnRegcnfm;
	private TextView txt_operator, txt_number, txt_reg_val, txt_ass_val,
			txtDialogTitle;

	private String spncellular, number, spnKey, productNumber,
			microwInsurenceAmount, microwInsurence_productId;
	private Double double1;
	private boolean service_loan;
	private boolean isValid;
	private RechargeDialog2 dialog;
	private Intent intent;
	public SharedPreferences sharedpreferences;
	private ImageView imgClose;
	CellularProviderDto cellular;

	public RechargeDialog1(Context context, String spncellular, String number,
			Double double1, boolean service_loan, String productNumber,
			String microwInsurenceAmount, CellularProviderDto cellular,
			String microwInsurence_productId) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		this.spncellular = spncellular;
		this.number = number;
		this.double1 = double1;
		this.service_loan = service_loan;
		this.productNumber = productNumber;
		this.microwInsurenceAmount = microwInsurenceAmount;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		this.cellular = cellular;
		this.microwInsurence_productId = microwInsurence_productId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.recharge_dialog1);
		etxtKeyalue = (EditText) findViewById(R.id.etxt_key_value);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btnRegcnfm = (Button) findViewById(R.id.btn_reg_cnfm);

		txt_operator = (TextView) findViewById(R.id.txt_operator);
		// txt_operator.setText(context.getResources()
		// .getString(R.string.operater));

		txt_number = (TextView) findViewById(R.id.txt_number);
		txt_reg_val = (TextView) findViewById(R.id.txt_reg_val);
		txt_ass_val = (TextView) findViewById(R.id.txt_ass_val);

		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);

		btn_reg_cancel.setOnClickListener(this);
		btnRegcnfm.setOnClickListener(this);

		txtDialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtDialogTitle.setText(context.getResources().getString(
				R.string.rechrge_dialog3));
		setContactNumberLength();
		loadUI();

	}

	private void loadUI() {

		try {
			txt_operator.setText(spncellular);
		} catch (Exception e) {
		}

		txt_number.setText(number);
		txt_reg_val.setText("$" + (new Double(double1)).longValue());
		if (service_loan) {
			txt_ass_val.setText("$"
					+ (new Double(microwInsurenceAmount)).longValue());
		} else {
			txt_ass_val.setText("$ 0");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			this.dismiss();
			break;

		case R.id.btn_reg_cnfm:
			validateAndCnfm();
			break;

		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void validateAndCnfm() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context, context.getResources()
					.getString(R.string.oops_errmsg), stringBuffer.toString(),
					null);
		} else {
			UserDetailsDTO dto = UserDetailsDAO.getInstance()
					.getRecordsuser_name(
							DBHandler.getDBObj(Constants.READABLE),
							sharedpreferences.getString("user_name", ""));

			if (NetworkConnectivity.netWorkAvailability(context)) {
				this.dismiss();
				new RechargesConfirmation(dto.getUserName(), dto.getPassword())
						.execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}

	}

	private void callRechargeConfirmDialog() {
		appContext.pushActivity(intent);
		RechargeDialog2 dialog = new RechargeDialog2(context, spnKey, number,
				ServiApplication.getReg_amt(), service_loan,
				microwInsurenceAmount, cellular, productNumber,
				microwInsurence_productId);
		dialog.show();
		dialog.setCancelable(false);
	}

	public boolean validateFields(StringBuffer stringBuffer) {

		spnKey = etxtKeyalue.getText().toString().trim();
		isValid = true;

		if (CommonMethods.keyvalidation(spnKey)) {
			isValid = false;
			stringBuffer.append(" "
					+ getContext().getString(R.string.enter_key));

		}
		return isValid;

	}

	/* call Recharges confirmation */
	public class RechargesConfirmation extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean flage = false, exception = false, exception1 = false;
		private String microwInsurenceAmount_value;
		private String encrypt_key;
		private String json, server_res;

		public RechargesConfirmation(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjformicro(), key);
		}

		@Override
		public Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_recharge,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("DUMP REQUEST", "varahalababu" + ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.v("SOAP", "varahalababu rec" + resultsString.toString());
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					ServiApplication
							.setRecharge_conform_data(new ParsingHandler()
									.getString(new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
											"response", "data"));

					Log.v("varahalababu",
							"varahalababu rec"
									+ AESTEST.AESDecrypt(
											new ParsingHandler().getString(
													new GetDocumentObject()
															.getDocumentObj(ht.responseDump),
													"response", "data"),
											AESTEST.AESkeyFromString(sharedpreferences
													.getString("AutoGenKey", ""))));
				} else {
					exception1 = true;
					server_res = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
				}
			} catch (Exception e) {
				e.printStackTrace();
				exception = true;
				return false;
			}
			return true;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,
					ServiApplication.SOAP_URL, ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(
				SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		public void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(
							server_res, AESTEST
									.AESkeyFromString(sharedpreferences
											.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(context,json.getString("message"));
					if (json.getString("message").equals(ServiApplication.wrong_passtext)) {
						ServiApplication.wrong_passcount++;
						if (ServiApplication.wrong_passcount>=3) {
							movetomenuscreen();
						}
					}
				} catch (Exception e) {
					appContext.pushActivity(intent);
					OopsErrorDialog dialog = new OopsErrorDialog(context);
					dialog.show();
					dialog.setCancelable(false);
				}
			} else {
				if (service_loan) {
					UserDetailsDTO dto = UserDetailsDAO.getInstance()
							.getRecordsuser_name(
									DBHandler.getDBObj(Constants.READABLE),
									sharedpreferences
											.getString("user_name", ""));
					if (NetworkConnectivity.netWorkAvailability(context)) {
						new VentaMicroSeguros(dto.getUserName(),
								dto.getPassword()).execute();
					} else {
						CommonMethods.showCustomToast(
								context,
								context.getResources().getString(
										R.string.no_wifi_adhoc));
					}
				} else {
					callRechargeConfirmDialog();
				}
			}

		}
	}

	/* Recharges json object */
	public String getJsonObjformicro() {
		UserDetailsDTO dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("msisdn", number);
			jsonobj.put("password", etxtKeyalue.getText().toString().trim());
			jsonobj.put("amount", "" + double1.intValue() * 100);
			jsonobj.put("productNumber", productNumber);
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
	
	
	/* Recharges json object */
	public String getVentaMicroSegurosJsonObjformicro() {
		UserDetailsDTO dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("msisdn", number);
			jsonobj.put("cedula", number);
			jsonobj.put("password", etxtKeyalue.getText().toString().trim());
			jsonobj.put("amount", "" + Long.parseLong(microwInsurenceAmount) * 100);
			jsonobj.put("productNumber", microwInsurence_productId);
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public void movetomenuscreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(context, MenuActivity.class);
	}

	private class VentaMicroSeguros extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private String json,errmessge;
		private boolean flage = false, exception = false, exception1 = false;
		private String encrypt_key,errormessage;

		VentaMicroSeguros(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);

			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getVentaMicroSegurosJsonObjformicro(), key);
			
			Log.v("babu", getVentaMicroSegurosJsonObjformicro());

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_Microinsurance,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					ServiApplication
							.setMicroseguros_conform_data(new ParsingHandler()
									.getString(new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
											"response", "data"));
					
					Log.v("varahalababu",
							"varahalababu rec MSD========"
									+ AESTEST.AESDecrypt(
											new ParsingHandler().getString(
													new GetDocumentObject()
															.getDocumentObj(ht.responseDump),
													"response", "data"),
											AESTEST.AESkeyFromString(sharedpreferences
													.getString("AutoGenKey", ""))));
					
					
				} else {
					
					errmessge=AESTEST.AESDecrypt(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences
									.getString("AutoGenKey", "")));
					
					errormessage=new ParsingHandler()
					.getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "message");
					exception1 = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("ERROR", e.getMessage());
				exception = true;
				return false;
			}
			return true;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,
					ServiApplication.SOAP_URL, ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(
				SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
				service_loan=false;
				callRechargeConfirmDialog();
			} else if (exception1) {
				
				try {
					JSONObject json=new JSONObject(errmessge);
					CommonMethods.showCustomToast(context,json.getString("message"));
				} catch (Exception e) {
					CommonMethods.showCustomToast(context,errormessage);
				}
				
				service_loan=false;
				callRechargeConfirmDialog();
				
			}else {
				callRechargeConfirmDialog();
			}

		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	private void setContactNumberLength() {
		InputFilter maxLengthFilter = new InputFilter.LengthFilter(10);
		etxtKeyalue.setFilters(new InputFilter[] { maxLengthFilter });

	}

}
