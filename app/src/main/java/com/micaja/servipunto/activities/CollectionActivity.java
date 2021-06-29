package com.micaja.servipunto.activities;

import java.net.Proxy;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.CollectionDailog1;
import com.micaja.servipunto.dialog.OopsErrorDialog;
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

public class CollectionActivity extends Activity implements OnClickListener {

	ServiApplication appContext;
	Button collectionSearch;
	EditText ed_convention_ac_no, ed_confirm_convention_ac_no;
	private boolean isValid;
	private String ac_No, cnfm_acNo;
	private Intent intent;
	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	private String values;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	// Result of this method,registration for all UI views.
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initUI() {

		setContentView(R.layout.collections);
		collectionSearch = (Button) findViewById(R.id.collection_search);
		ed_convention_ac_no = (EditText) findViewById(R.id.ed_convention_ac_no);
		ed_confirm_convention_ac_no = (EditText) findViewById(R.id.ed_confirm_convention_ac_no);
		// movetoCollectionDailog1(values);
		loadUI();

	}

	private void loadUI() {
		collectionSearch.setOnClickListener(this);
		ed_convention_ac_no.requestFocus();

	}

	// navigate menu screen
	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.collection_search:
			validateAndSearch();
			break;

		default:
			break;
		}

	}
	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndSearch() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(CollectionActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else if (isValid) {
			if (NetworkConnectivity
					.netWorkAvailability(CollectionActivity.this)) {
				new RechargesConfirmation(dto.getUserName(), dto.getPassword())
						.execute();
			} else {
				CommonMethods.showCustomToast(CollectionActivity.this,
						getResources().getString(R.string.no_wifi_adhoc));
			}
		}

	}
//move to Collection Dailog1 screen
	private void movetoCollectionDailog1(String responds_json) {
		CollectionDailog1 dialog = new CollectionDailog1(this, responds_json,
				ac_No);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.show();
		dialog.getWindow().setAttributes(lp);
	}
	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {

		ac_No = ed_convention_ac_no.getText().toString().trim();
		cnfm_acNo = ed_confirm_convention_ac_no.getText().toString().trim();
		isValid = true;

		if (ac_No.length() == 0 && cnfm_acNo.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.collection_invalid_msg)
					+ "\n"
					+ getResources().getString(R.string.collection_errmsg1));
		} else if (ac_No.length() == 0) {
			isValid = false;
			stringBuffer
					.append("\n"
							+ getResources().getString(
									R.string.collection_invalid_msg));
		} else if (cnfm_acNo.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources()
							.getString(R.string.collection_invalid_msg1));
		} else if (!ac_No.equals(cnfm_acNo)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_acno));

		}
		// if (ac_No.length() == 0) {
		// isValid = false;
		// stringBuffer.append("\n"
		// + getResources().getString(R.string.collection_errmsg1));
		// }
		// if (cnfm_acNo.length() == 0) {
		// isValid = false;
		// stringBuffer.append("\n"
		// + getResources().getString(R.string.collection_errmsg2));
		// }

		return isValid;
	}

	/* Recharges json object */
	public String getConsultaConvenio() {

		JSONObject jsonobj = new JSONObject();
		try {

			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("codigoConvenio", ac_No);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private class RechargesConfirmation extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private String json;
		private boolean flage = false, exception = false;
		private String encrypt_key, server_res_data;

		RechargesConfirmation(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					CollectionActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);

			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getConsultaConvenio(), key);
			Log.v("varahalababu", "varahalababu" + getConsultaConvenio());

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(CollectionActivity.this, encrypt_key,
								ServiApplication.process_id_Consulta_Convenio,
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

					server_res_data = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");

				} else {
					server_res_data = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					flage = true;
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
		protected void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						CollectionActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (flage) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(
							server_res_data, AESTEST
									.AESkeyFromString(sharedpreferences
											.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(CollectionActivity.this,
							json.getString("message"));
					Log.v("varahalababu", "varahalababu" + json.toString());
				} catch (Exception e) {
				}
			} else {
				Log.v("varahalababu",
						"varahalababu"
								+ AESTEST.AESDecrypt(server_res_data, AESTEST
										.AESkeyFromString(sharedpreferences
												.getString("AutoGenKey", ""))));
				movetoCollectionDailog1(AESTEST.AESDecrypt(server_res_data,
						AESTEST.AESkeyFromString(sharedpreferences.getString(
								"AutoGenKey", ""))));
			}

		}
	}

}
