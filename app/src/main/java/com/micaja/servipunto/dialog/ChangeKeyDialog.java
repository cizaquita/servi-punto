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
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
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

public class ChangeKeyDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {
	private Context context;
	ServiApplication appContext;
	String value, password, conformpassword;
	public SharedPreferences sharedpreferences;
	private EditText change_password1, etxt_password, etxt_conformpassword;
	private Button btn_EndSaleSave, btn_EndSaleCancel;
	private Intent intent;
	private ImageView img_close;
	Boolean Isvalid;
	String key, newkey, confirmkey;
	private TextView tv_changepass_title;

	public ChangeKeyDialog(Context context) {
		super(context);
		this.context = context;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

	}

	private void dialogdismiss() {
		this.dismiss();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ServiApplication) context.getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initUI();
	}

	// Result of this method,registration for all UI views.
	private void initUI() {
		setContentView(R.layout.changepassword);
		change_password1 = (EditText) findViewById(R.id.change_password1);
		etxt_password = (EditText) findViewById(R.id.etxt_password);
		etxt_conformpassword = (EditText) findViewById(R.id.etxt_conformpassword);
		tv_changepass_title = (TextView) findViewById(R.id.tv_changepass_title);

		btn_EndSaleCancel = (Button) findViewById(R.id.btn_EndSaleCancel);
		btn_EndSaleSave = (Button) findViewById(R.id.btn_EndSaleSave);
		img_close = (ImageView) findViewById(R.id.img_close);
		img_close.setOnClickListener(this);
		btn_EndSaleSave.setOnClickListener(this);
		btn_EndSaleCancel.setOnClickListener(this);

		tv_changepass_title.setText(context.getResources().getString(
				R.string.change_pass_title));
		change_password1.setKeyListener(DigitsKeyListener
				.getInstance("0123456789"));
		etxt_password.setKeyListener(DigitsKeyListener
				.getInstance("0123456789"));
		etxt_conformpassword.setKeyListener(DigitsKeyListener
				.getInstance("0123456789"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_EndSaleSave:
			validateAndsave();
			break;
		case R.id.btn_EndSaleCancel:
			this.dismiss();
			break;
		case R.id.img_close:
			this.dismiss();
			break;
		default:
			break;
		}

	}

	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndsave() {
		StringBuffer stringbf = new StringBuffer();
		if (!validation(stringbf)) {
			CommonMethods
					.showcustomdialogbox(context, context.getResources()
							.getString(R.string.oops_errmsg), stringbf
							.toString(), null);
			change_password1.setText(null);
			etxt_password.setText(null);
			etxt_conformpassword.setText(null);
		} else {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new UserLoginTask("", "").execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	// This method using for Validation purpose
	private boolean validation(StringBuffer stringbf) {
		key = change_password1.getText().toString().trim();
		newkey = etxt_password.getText().toString().trim();
		confirmkey = etxt_conformpassword.getText().toString().trim();
		Isvalid = true;

		if (key.length() == 0) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.Please_enter_key));
		} else if (key.length() < 6) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.Please_enter_valid_key));
		}

		if (newkey.length() == 0) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.please_enter_new_key));
		} else if (newkey.length() < 6) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.please_enter_valid_new_key));
		}
		if (confirmkey.length() == 0) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.please_enter_confirm_key));
		} else if (confirmkey.length() < 6) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.please_enter_valid_confirm_key));
		}

		if (key.length() == 0 && confirmkey.length() == 0
				&& newkey.length() == 0) {

		} else {
			if (newkey.equals(confirmkey)) {

			} else {
				Isvalid = false;
				stringbf.append("\n"
						+ context.getResources().getString(
								R.string.password_validation));
			}

			if (key.equals(newkey)) {
				Isvalid = false;
				stringbf.append("\n"
						+ context.getResources().getString(
								R.string.pwd_validation));
			}
		}

		return Isvalid;
	}

	// To call service for Change password
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		private String encrypt_key, jsonobj, microdto;

		public UserLoginTask(String email, String password) {
			mEmail = email;
			mPassword = password;
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
			jsonobj = AESTEST.AESCrypt(makeJsonObj(), key);
			// jsonobj =
			// AES.encrypt(makeJsonObj(),ServiApplication.AES_secret_key);//makeJsonObj();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_Change_key,
								ServiApplication.username, jsonobj)));
				request.addProperty(MakeHeader.makepropertyInfo2(jsonobj,
						"JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				try {
					if (new ParsingHandler()
							.getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "state").contains("SUCCESS")) {
						dialogdismiss();
					} else {
						if (new ParsingHandler()
								.getString(
										new GetDocumentObject()
												.getDocumentObj(ht.responseDump),
										"response", "state").contains("ERROR")) {
							microdto = new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data");
							exception1 = true;
						} else {
							exception = true;
						}
					}
				} catch (Exception e) {
					exception = true;
				}
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				Log.e("ERROR", e.getMessage());
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
			} else if (exception1) {
				try {
					JSONObject test = new JSONObject(microdto);
					CommonMethods.showCustomToast(context,
							test.getString("message"));
				} catch (Exception e) {
				}
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.key_sucess));
				dialogdismiss();
			}
		}

		// @SuppressWarnings("unused")
		private String makeJsonObj() {
			UserDetailsDTO dto = UserDetailsDAO.getInstance()
					.getRecordsuser_name(
							DBHandler.getDBObj(Constants.READABLE),
							sharedpreferences.getString("user_name", ""));
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId());
				jsonobj.put("terminalId", dto.getTerminalId());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
				jsonobj.put("claveActual", change_password1.getText()
						.toString().trim());
				jsonobj.put("claveNueva", etxt_password.getText().toString()
						.trim());
				jsonobj.put("confirmaClaveNueva", etxt_conformpassword
						.getText().toString().trim());

			} catch (Exception e) {
			}
			return jsonobj.toString();
		}
	}
}
