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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

public class DaviplatapaymentDialog2 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private ImageView img_close;
	private Button btn_reg_cancel, btn_reg_cnfm;
	private TextView txt_daviplata_grant_for;
	public SharedPreferences sharedpreferences;
	private String puntoDeVentaId, token, valorPago, clave;
	private UserDetailsDTO dto;

	public DaviplatapaymentDialog2(Context context, String puntoDeVentaId,
			String token, String valorPago, String clave) {
		super(context);
		this.context = context;
		this.puntoDeVentaId = puntoDeVentaId;
		this.token = token;
		this.valorPago = valorPago;
		this.clave = clave;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.daviplatapayment_dailog2);

		loadUI();
	}

	private void loadUI() {

		txt_daviplata_grant_for = (TextView) findViewById(R.id.txt_daviplata_grant_for);
		img_close = (ImageView) findViewById(R.id.img_close);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btn_reg_cnfm = (Button) findViewById(R.id.btn_reg_cnfm);
		btn_reg_cancel.setText(getContext().getResources().getString(
				R.string.reg_no));
		btn_reg_cnfm.setText(getContext().getResources().getString(
				R.string.reg_yes));
		img_close.setOnClickListener(this);
		btn_reg_cancel.setOnClickListener(this);
		btn_reg_cnfm.setOnClickListener(this);

		txt_daviplata_grant_for.setText(context.getResources().getString(
				R.string.daviplata_grant_for)
				+ " "
				+ valorPago
				+ " "
				+ context.getResources().getString(
						R.string.daviplata_grant_for1));
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			this.dismiss();
			break;

		case R.id.btn_reg_cnfm:
			validateAndSave();

			break;

		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	public void movetoSucessDaviplatapayment(String jsonformate) {
		DaviplatapaymentDialog3 dialog = new DaviplatapaymentDialog3(context,
				jsonformate, valorPago);
		dialog.show();
		dialog.setCancelable(false);
	}

	private void validateAndSave() {
		this.dismiss();

		if (NetworkConnectivity.netWorkAvailability(context)) {
			new EjemploConsultaConvenio(dto.getUserName(), dto.getPassword())
					.execute();

		} else {
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.no_wifi_adhoc));
		}
	}

	public class EjemploConsultaConvenio extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json;
		private String encrypt_key, jsonformate;

		public EjemploConsultaConvenio(String email, String password) {
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
			json = AESTEST.AESCrypt(getJsonObj(), key);
			Log.v("varahalababu", "varahalababu" + getJsonObj());
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(
								context,
								encrypt_key,
								ServiApplication.process_id_retiropagoDaviplata,
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
					jsonformate = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
					Log.v("varahalababu", "varahalababu" + jsonformate);
				} else {
					jsonformate = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
					Log.v("varahalababu", "varahalababu" + jsonformate);
					exception1 = true;
					exception = false;
				}
				Log.d("DUMP RESPONSE", ht.responseDump);
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
			CommonMethods.dismissProgressDialog();
			if (exception) {
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(jsonformate);
					CommonMethods.showCustomToast(context,
							json.getString("message"));
				} catch (Exception e) {

				}
			} else {
				movetoSucessDaviplatapayment(jsonformate);
			}
		}
	}

	public String getJsonObj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("numerocelular", puntoDeVentaId);
			jsonobj.put("token", token);
			jsonobj.put("amount", "" + Integer.parseInt(valorPago) * 100);
			jsonobj.put("password", clave);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

}
