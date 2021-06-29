package com.micaja.servipunto.service;

import java.net.Proxy;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.Encryption;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.KeyGenEncryption;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class KeyRegistrationService extends IntentService {
	public KeyRegistrationService() {
		super("");
	}

	public SharedPreferences sharedpreferences;
	private String AutoGenKey, keygendate, currentdate;
	Editor editor;
	private UserDetailsDTO dto;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void loadDataFromAsset() {
		stopSelf();
		if (NetworkConnectivity
				.netWorkAvailability(KeyRegistrationService.this)) {
			try {
				new GetSymmetricalKey("", "").execute();
			} catch (Exception e) {
				new GetSymmetricalKey("", "").execute();
			}
		} else {

		}
	}

	public class GetSymmetricalKey extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false;
		private String encrypt_key;

		public GetSymmetricalKey(String email, String password) {
			mEmail = email;
			mPassword = password;
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String jsonvalue = getJsonObj();
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(
								KeyRegistrationService.this,
								encrypt_key,
								ServiApplication.process_id_get_symmetrical_Key,
								ServiApplication.username, jsonvalue)));
				request.addProperty(MakeHeader.makepropertyInfo2(jsonvalue,
						"JSON"));
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
					editor.putString("AutoGenKey", AutoGenKey);
					editor.putString("AutoGenDate", Dates.c_date());
					editor.putString("AutoGen_User",
							sharedpreferences.getString("user_name", ""));
					editor.commit();
				}
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
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

//			if (sharedpreferences.getString("distribuidor", "").length() > 0) {
				if (NetworkConnectivity
						.netWorkAvailability(KeyRegistrationService.this)) {
					new InfoLogin().execute();
//				}
			}
		}
	}

	public String getJsonObj() {
		AutoGenKey = new Encryption().generarClave();
		try {
			return "{\"llaveAES\":\""
					+ new KeyGenEncryption().encryptData(AutoGenKey) + "\"}";
		} catch (Exception e) {
			return "";
		}

	}

	public class InfoLogin extends AsyncTask<Void, Void, Void> {

		String json = getNewJSON();
		String encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
				ServiApplication.AES_secret_key);
		private static final String SOAP_ACTION = "";

		@Override
		protected Void doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(KeyRegistrationService.this, encrypt_key,
								ServiApplication.process_id_InfoLogin,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {

					String value=new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					try {
						JSONObject json=new JSONObject(new ParsingHandler().getString(
								new GetDocumentObject()
								.getDocumentObj(ht.responseDump),
						"response", "data"));
						editor.putString("distribuidor", json.getString("distribuidor"));
						editor.putString("name_of_store", json.getString("name"));
						editor.commit();
						ServiApplication.setDistribuidor(json.getString("distribuidor"));
					} catch (Exception e) {
					}

				} else {

				}
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
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

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,
					ServiApplication.SOAP_URL, ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

	}

	public String getNewJSON() {
		JSONObject json = null;
		try {
			json = new JSONObject();
			json.put("terminalId", dto.getTerminalId());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return json.toString();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		loadDataFromAsset();
	}
}
