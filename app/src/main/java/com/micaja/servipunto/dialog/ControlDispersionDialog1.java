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
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ControlDispersionDTO;
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

public class ControlDispersionDialog1 extends Dialog implements OnClickListener {
	Boolean Isvalid;
	private ImageView imgclose;
	private Context context;
	private EditText etxtdispesion_key;
	private TextView txtv_dispersinmssg;
	String key;
	private Button btndispersion_save, btndispersion_cancel;
	ControlDispersionDTO dto;
	private boolean click_eventtype;
	public SharedPreferences sharedpreferences;

	public ControlDispersionDialog1(Context context, ControlDispersionDTO dto,
			boolean click_eventtype) {
		super(context);
		this.context = context;
		this.dto = dto;
		this.click_eventtype = click_eventtype;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.control_de_dispersiondialog1);
		etxtdispesion_key = (EditText) findViewById(R.id.etxt_dispesion_key);
		txtv_dispersinmssg = (TextView) findViewById(R.id.txtv_dispersin_mssg);
		btndispersion_save = (Button) findViewById(R.id.btn_dispersion_save);
		btndispersion_cancel = (Button) findViewById(R.id.btn_dispersion_cancel);
		imgclose = (ImageView) findViewById(R.id.img_close);
		btndispersion_save.setOnClickListener(this);
		btndispersion_cancel.setOnClickListener(this);
		imgclose.setOnClickListener(this);
		loadUi();
	}

	private void loadUi() {

		if ("DEVOLUCION".equalsIgnoreCase(dto.getTipo())) {

			if (click_eventtype) {
				txtv_dispersinmssg.setText(context.getResources().getString(
						R.string.dispers_accept_the_devolution)
						+ "$ "
						+ dto.getValor()
						+ " : "
						+ context.getResources().getString(
								R.string.dispers_btnaccept_sureExit));
			} else {
				txtv_dispersinmssg.setText(context.getResources().getString(
						R.string.dispers_reject_the_devolution)
						+ "$ "
						+ dto.getValor()
						+ " : "
						+ context.getResources().getString(
								R.string.dispers_btnaccept_sureExit));
			}

		} else {
			if (click_eventtype) {
				txtv_dispersinmssg.setText(context.getResources().getString(
						R.string.dispers_accept_the_acceptance)
						+ "  $ "
						+ dto.getValor()
						+ " : "
						+ context.getResources().getString(
								R.string.dispers_btnaccept_sureExit));
			} else {
				txtv_dispersinmssg.setText(context.getResources().getString(
						R.string.dispers_reject_the_acceptance)
						+ "  $ "
						+ dto.getValor()
						+ " : "
						+ context.getResources().getString(
								R.string.dispers_btnaccept_sureExit));
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dispersion_save:
			validateAndSave();
			break;
		case R.id.btn_dispersion_cancel:
			this.dismiss();
			break;
		case R.id.img_close:
			this.dismiss();
		default:
			break;
		}

	}

	private void validateAndSave() {

		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new ControlDispersion().execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		key = etxtdispesion_key.getText().toString().trim();
		Isvalid = true;
		if (key.length() == 0) {
			Isvalid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.Please_enter_key));
		} else {
			if (key.length() < 6) {
				Isvalid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.please_enter_confirmValue));
			}
		}
		return Isvalid;
	}

	/* call Recharges confirmation */
	public class ControlDispersion extends AsyncTask<Void, Void, Boolean> {
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		private String encrypt_key;
		private String json, server_res, process_id;

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
			if (click_eventtype) {
				process_id = ServiApplication.process_id_Aceptar_Dispersion;
			} else {
				process_id = ServiApplication.process_id_Rechazar_Dispersion;
			}
			
			Log.v("Babu=========", getJsonObjformicro());
		}

		@Override
		public Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key, process_id,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("Babu=========", ht.requestDump);
				Log.v("Babu=========", ht.responseDump);
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
		public void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(server_res);
					CommonMethods.showCustomToast(context,
							json.getString("message"));

				} catch (Exception e) {
				}
			} else {
				dismiss();
				ControlDispersionDialog2 dialog = new ControlDispersionDialog2(
						context, server_res, dto, click_eventtype, key);
				dialog.show();
				dialog.setCancelable(false);
			}
		}
	}

	public String getJsonObjformicro() {
		UserDetailsDTO dto1 = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto1.getComercioId());
			jsonobj.put("terminalId", dto1.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto1.getPuntoredId());
			jsonobj.put("password", key);
			jsonobj.put("programacionId", dto.getProgramacionId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
