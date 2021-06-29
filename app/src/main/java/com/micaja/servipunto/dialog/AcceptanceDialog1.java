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

public class AcceptanceDialog1 extends Dialog implements
		android.view.View.OnClickListener {
	ServiApplication appContext;
	private Button btn_acceptance_confirm, btn_acceptance_Cancel;
	private Context context;
	private ImageView img_clse;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	public SharedPreferences sharedpreferences;
	private Intent intent;
	private Double double1;
	UserDetailsDTO dto;
	private TextView acceptenceMsgStart;
	Handler uiHandler;
	private String value, distibuter;

	public AcceptanceDialog1(Context context, AcceptEfectivoDTO aedto,
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
		value = aedto.getAmount();
		distibuter = aedto.getDistribuidor();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.acceptance_dialog1);
		img_clse = (ImageView) findViewById(R.id.img_clse);
		btn_acceptance_confirm = (Button) findViewById(R.id.btn_acceptance_confirm);
		btn_acceptance_Cancel = (Button) findViewById(R.id.btn_acceptance_Cancel);
		btn_acceptance_confirm.setOnClickListener(this);
		btn_acceptance_Cancel.setOnClickListener(this);
		img_clse.setOnClickListener(this);

		acceptenceMsgStart = (TextView) findViewById(R.id.acceptence_message_start);

		acceptenceMsgStart.setText(context.getResources().getString(
				R.string.efectivo_message_start)
				+ " "
				+ aedto.getAmount()
				+ " "
				+ context.getResources().getString(
						R.string.accptnce_message_end));
	}

	@Override
	public void onClick(View v) {
		{
			switch (v.getId()) {
			case R.id.btn_acceptance_confirm:
				validateAndSave();
				break;
			case R.id.btn_acceptance_Cancel:
				this.dismiss();
				movetoMenuActivity();
				break;
			case R.id.img_clse:
				this.dismiss();
				break;

			default:
				break;
			}
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
			new AcceptEfectivo(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.no_wifi_adhoc));
		}

	}

	public class AcceptEfectivo extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean flage = false, exception = false, exception1 = false;
		private String microwInsurenceAmount_value;
		private String encrypt_key;
		private String json, server_res;

		public AcceptEfectivo(String email, String password) {
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
								ServiApplication.process_id_AcceptEfectivo,
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
					aedto = new ParsingHandler().getAcceptEfectivo(server_res);

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
				callAcceptanceDailog(aedto, server_res);
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
					+ (Long.parseLong(aedto.getAmount())));//based on mauricio request we are removed the zeros on december 30(APK Mi Caja 29122015)
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("distribuidor", aedto.getDistribuidor());
			jsonobj.put("claveDistribuidor", aedto.getDiskey());
			return jsonobj.toString();

		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public void callAcceptanceDailog(AcceptEfectivoDTO aedto2, String server_res) {
		AcceptanceDialog2 dialog2 = new AcceptanceDialog2(context, uiHandler,
				aedto, value, server_res, distibuter);
		dialog2.show();
		dialog2.setCancelable(false);
	}

}
