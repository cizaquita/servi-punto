package com.micaja.servipunto.service;

import java.net.Proxy;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class SincronizarTransacciones extends Service {

	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	// private String ModuleName,Module_Tipo_id;
	private Long lvalue;
	SincronizarTransaccionesDTO sdto = null;

	public boolean flage = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Bundle b = intent.getExtras();
		// ModuleName=b.getString("ModuleName");
		// Module_Tipo_id=b.getString("Module_Tipo_id");

	}

	@Override
	public void onCreate() {
		super.onCreate();
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		binddata();
	}
	
	void binddata(){
		getDataFromDb();
		loadDataFromAsset();
	}

	private void getDataFromDb() {
		Log.v("varun", "$%^&&&");
		List<DTO> ldto = SincronizarTransaccionesDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));
		for (DTO dto : ldto) {
			sdto = (SincronizarTransaccionesDTO) dto;
//			if (ssdto.getServitienda_synck_status().equals("0")) {
//				sdto = (SincronizarTransaccionesDTO) dto;
//				flage = true;
//			}
		}
	}

	private void loadDataFromAsset() {
		stopSelf();
		if (NetworkConnectivity.netWorkAvailability(SincronizarTransacciones.this)) {
			new GetSymmetricalKey("", "").execute();
		} else {

		}
	}

	public class GetSymmetricalKey extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, sucess = false;
		private String encrypt_key;

		public GetSymmetricalKey(String email, String password) {
			mEmail = email;
			mPassword = password;
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String jsonvalue = getJsonObj();
				Log.v("varahalbabu", "varahlababu" + jsonvalue);
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(SincronizarTransacciones.this,
						encrypt_key, ServiApplication.process_id_sincronizarTransacciones, ServiApplication.username,
						jsonvalue)));
				request.addProperty(MakeHeader.makepropertyInfo2(jsonvalue, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONDS", ht.responseDump);
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					sucess = true;
				}
				Log.d("SOAP", "varahalbabu " + resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, ServiApplication.SOAP_URL,
					ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (sucess) {
				SincronizarTransaccionesDAO.getInstance().updatePuntoRedSynck(DBHandler.getDBObj(Constants.WRITABLE),sdto);
				calServiTiendaService();
			}
		}
	}

	public String getJsonObj() {

		Double du = Double.parseDouble(sdto.getTransaction_value().replace(",", ""));
		lvalue = (du).longValue();
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("idpdvServitiendas", sdto.getId_pdb_servitienda());
			jsonobj.put("idTransaccion", sdto.getTipo_transaction());
			jsonobj.put("tipoTransaccion", sdto.getModule_tipo_id());
			jsonobj.put("fechaTransaccion", "" + Dates.getDateFromeString(sdto.getCreation_date()));
			jsonobj.put("horaTransaccion", Dates.getCurrentTime(sdto.getCreation_date()));
			jsonobj.put("valorTransaccion", "" + (lvalue * 100));
			return jsonobj.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public void calServiTiendaService() {

		if (NetworkConnectivity.netWorkAvailability(SincronizarTransacciones.this)) {
			new CalSincronizaeSynck().execute();
		} else {

		}
	}

	private class CalSincronizaeSynck extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(SincronizarTransacciones.this);
		String val_productDetails;

		@Override
		protected Void doInBackground(Void... params) {
			val_productDetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync", ServiceHandler.POST,
					getServiTiendaJsonObj());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (new JSONStatus().status(val_productDetails) == 0) {
				SincronizarTransaccionesDAO.getInstance().updateServiTiendaSynck(DBHandler.getDBObj(Constants.WRITABLE),sdto);
//				if (flage) {
//					flage = false;
//					binddata();
//				}
			}
		}
	}

	public String getServiTiendaJsonObj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "puntored_transaction");
			jsonobj.put("type", "insert");

			JSONArray jsonRecordsArray = new JSONArray();

			JSONObject jso_client_obj = new JSONObject();
			jso_client_obj.put("rowid", Long.parseLong(sdto.getRowid()));
			jso_client_obj.put("module", sdto.getModule());
			jso_client_obj.put("tipo_transaction", sdto.getTipo_transaction());
			jso_client_obj.put("authorization_number", sdto.getAuthorization_number());
			jso_client_obj.put("id_commercio", dto.getComercioId());
			jso_client_obj.put("id_pdv", dto.getPuntoredId());
			jso_client_obj.put("id_terminal", dto.getTerminalId());
			jso_client_obj.put("id_pdb_servitienda", sdto.getId_pdb_servitienda());
			jso_client_obj.put("transaction_datetime", Dates.serverdateformate(sdto.getTransaction_datetime()));
			jso_client_obj.put("transaction_value", Double.parseDouble(sdto.getTransaction_value().replace(",", "")));
			jso_client_obj.put("status", sdto.getStatus());
			jso_client_obj.put("creation_date", Dates.serverdateformate(sdto.getCreation_date()));
			jso_client_obj.put("created_by", sdto.getCreated_by());
			jso_client_obj.put("modified_date", Dates.serverdateformate(sdto.getModified_date()));
			jso_client_obj.put("modified_by", sdto.getModified_by());
			jsonRecordsArray.put(jso_client_obj);
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

}
