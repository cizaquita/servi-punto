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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
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

public class DevolucionDialog1 extends Dialog implements
		android.view.View.OnClickListener {
	ServiApplication appContext;
	private TextView return_value, acceptenceMsgStart;
	private Button btn_return_confirm, btn_return_cancel;
	private ImageView img_close;
	private Context context;
	private String returnValue, distPartner, returnKey;
	private AcceptEfectivoDTO aedto;
	public SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;
	Handler uiHandler;
	
	String value,patner;

	public DevolucionDialog1(Context context, AcceptEfectivoDTO aedto,
			Handler uiHandler) {
		super(context);
		this.context = context;
		this.aedto = aedto;
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		this.uiHandler = uiHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.devolucion_dialog1);
		img_close = (ImageView) findViewById(R.id.img_close);
		return_value = (TextView) findViewById(R.id.return_value);
		btn_return_confirm = (Button) findViewById(R.id.btn_return_confirm);
		btn_return_cancel = (Button) findViewById(R.id.btn_return_cancel);
		btn_return_confirm.setOnClickListener(this);
		btn_return_cancel.setOnClickListener(this);
		img_close.setOnClickListener(this);
		acceptenceMsgStart = (TextView) findViewById(R.id.acceptence_message_start);

		acceptenceMsgStart.setText(context.getResources().getString(
				R.string.acceptence_message_start)
				+ " "
				+ aedto.getAmount()
				+ " "
				+ context.getResources().getString(
						R.string.accptnce_message_end));
		
		value=aedto.getAmount();
		patner=aedto.getDistribuidor();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_return_confirm:
			validateAndSave();
			break;
		case R.id.btn_return_cancel:
			this.dismiss();
			movetoMenuActivity();
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}
	}
	private void movetoMenuActivity() {
		this.dismiss();
		appContext.clearActivityList();
		CommonMethods.openNewActivity(context, MenuActivity.class);
	}

	private void validateAndSave() {
		UserDetailsDTO dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		if (NetworkConnectivity.netWorkAvailability(context)) {
			this.dismiss();
			new Devolucion(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.no_wifi_adhoc));
		}

	}

	public class Devolucion extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean flage = false, exception = false, exception1 = false;
		private String microwInsurenceAmount_value;
		private String encrypt_key;
		private String json, server_res;

		public Devolucion(String email, String password) {
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
			json = AESTEST.AESCrypt(getJsonObjformicro(), key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_Devolucion,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					server_res = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
					aedto = new ParsingHandler().getDevolution(server_res);

				} else {
					exception1 = true;
					server_res = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));

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
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(server_res);
					CommonMethods.showCustomToast(context,
							json.getString("message"));
				} catch (Exception e) {
					appContext.pushActivity(intent);
					OopsErrorDialog dialog = new OopsErrorDialog(context);
					dialog.show();
					dialog.setCancelable(false);
				}
			} else {
				callReturnDialog(aedto,server_res);
			}
		}

	}

	public String getJsonObjformicro() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("password", aedto.getPassword());
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("amount", ""
					+ (Long.parseLong(aedto.getAmount())));//Based on Ange request we are remove 100 multiplication on 2016-01-06
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("distribuidor", aedto.getDistribuidor());
			jsonobj.put("claveDistribuidor", aedto.getDiskey());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public void callReturnDialog(AcceptEfectivoDTO aedto2, String server_res) {
		DevolucionDialog2 dialog = new DevolucionDialog2(context, aedto2,
				uiHandler,value,patner,server_res);
		dialog.show();
		dialog.setCancelable(false);

	}
}
