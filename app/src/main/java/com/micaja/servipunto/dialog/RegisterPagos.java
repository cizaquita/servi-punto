package com.micaja.servipunto.dialog;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.service.SincronizarTransacciones;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class RegisterPagos extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {
	private Context context;
	ServiApplication appContext;
	String value, password, conformpassword;
	public SharedPreferences sharedpreferences;
	private EditText et_value, et_key;
	private Button btn_EndSaleSave, btn_EndSaleCancel;
	private ImageView img_close;
	Boolean Isvalid;
	String key, newkey, confirmkey;
	UserDetailsDTO udto;
	private Long long1;
	private boolean flage;

	public RegisterPagos(Context context, Long long1, boolean flage) {
		super(context);
		this.context = context;
		this.flage = flage;
		this.long1 = long1;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ServiApplication) context.getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.registerpagodialog);
		et_key = (EditText) findViewById(R.id.et_key);
		et_value = (EditText) findViewById(R.id.et_value);
		

		btn_EndSaleCancel = (Button) findViewById(R.id.btn_EndSaleCancel);
		btn_EndSaleSave = (Button) findViewById(R.id.btn_EndSaleSave);
		img_close = (ImageView) findViewById(R.id.img_close);
		if (flage) {
			et_value.setText("" + long1);
			et_value.setKeyListener(null);
		}
		img_close.setOnClickListener(this);
		btn_EndSaleSave.setOnClickListener(this);
		btn_EndSaleCancel.setOnClickListener(this);
		et_value.setFocusable(true);
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

	private void validateAndsave() {
		StringBuffer stringbf = new StringBuffer();
		if (!validation(stringbf)) {
			
			CommonMethods.displayAlert(true, context.getResources().getString(R.string.fail), stringbf.toString(),
					context.getResources().getString(R.string.ok), "", context, null, false);
			
		} else {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				this.dismiss();
				new RegistroPago().execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	private boolean validation(StringBuffer stringbf) {
		key = et_key.getText().toString().trim();
		try {
			long1 = Long.parseLong(et_value.getText().toString().trim());
		} catch (Exception e) {
			long1 = Long.parseLong("0");
		}
		Isvalid = true;

		if (CommonMethods.keyvalidation(et_key.getText().toString().trim())) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(R.string.enter_key));
		}

		if (et_value.getText().toString().trim().length() == 0) {
			Isvalid = false;
			stringbf.append("\n"
					+ context.getResources().getString(
							R.string.enter_valid_value));

		}
		return Isvalid;
	}

	/* call RegistroPago */
	private class RegistroPago extends AsyncTask<Void, Void, Void> {

		private static final String SOAP_ACTION = "";
		private boolean flage = false, exception = false,exception1=false;
		private String microwInsurenceAmount_value;
		private String json, jsondata,responds;
		private String encrypt_key;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjforRgb1(), key);
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_reg_pago,
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
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					jsondata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");

				} else {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return null;
			}
			return null;
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
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();

			if (exception) {
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			}else if (exception1) {
				try {
					
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(responds, AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", ""))));
					CommonMethods.showCustomToast(context, json.getString("message"));
				} catch (Exception e) {
					
				}
			} else {
				String value = AESTEST.AESDecrypt(jsondata, AESTEST
						.AESkeyFromString(sharedpreferences.getString(
								"AutoGenKey", "")));
				JSONObject json;
				try {
					json = new JSONObject(value);
					CommonMethods.showCustomToast(context,
							json.getString("message"));

					SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
					dto.setTipo_transaction("");
					dto.setCreation_date(Dates.currentdate());
					dto.setId_pdb_servitienda("");
					dto.setModule("abono a cuenta");
					dto.setAuthorization_number("");
					dto.setTransaction_value("" + long1);
					dto.setCreated_by(sharedpreferences.getString("user_name",
							""));
					dto.setModified_by(sharedpreferences.getString("user_name",
							""));
					dto.setModified_date(Dates.currentdate());
					dto.setPunthored_synck_status("0");
					dto.setServitienda_synck_status("0");
					dto.setStatus("true");
					dto.setTransaction_datetime(Dates.currentdate());
					dto.setRowid("");
					dto.setModule_tipo_id(ServiApplication.tipo_abono_acenta_id);
					List<DTO> list = new ArrayList<DTO>();
					list.add(dto);
					SincronizarTransaccionesDAO.getInstance().insert(
							DBHandler.getDBObj(Constants.WRITABLE), list);
					Intent intent = new Intent(context,
							SincronizarTransacciones.class);
					context.startService(intent);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public String getJsonObjforRgb1() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", udto.getComercioId());
			jsonobj.put("terminalId", udto.getTerminalId());
			jsonobj.put("puntoDeVentaId", udto.getPuntoredId());
			jsonobj.put("amount", "" + (long1));//just removed those *100 based on mauricio request
			jsonobj.put("password", key);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
