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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
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

public class PinesDailog1 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;

	private ImageView img_close;
	private Button btn_reg_cancel, btn_reg_cnfm;
	private boolean isValid;
	private TextView tv_pin, tv_value, tv_number_of_celular;
	EditText ed_key;
	CellularProviderDto pinesProvides;
	CellularProviderDto pinesProducts;
	private String cellular, password;
	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	public PinesDailog1(Context context, CellularProviderDto pinesProvides,
			CellularProviderDto pinesProducts, String cellular) {
		super(context);
		this.context = context;
		this.pinesProvides = pinesProvides;
		this.pinesProducts = pinesProducts;
		this.cellular = cellular;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.pines_dailog1);
		loadUI();
	}

	private void loadUI() {

		img_close = (ImageView) findViewById(R.id.img_close);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btn_reg_cnfm = (Button) findViewById(R.id.btn_reg_cnfm);

		tv_pin = (TextView) findViewById(R.id.tv_pin);
		tv_value = (TextView) findViewById(R.id.tv_value);
		tv_number_of_celular = (TextView) findViewById(R.id.tv_number_of_celular);

		tv_pin.setText(pinesProducts.getProductName());
		tv_value.setText("$ "
				+ (new Double(Double.parseDouble(pinesProducts.getValor())))
						.longValue());
		tv_number_of_celular.setText(cellular);
		ed_key = (EditText) findViewById(R.id.ed_key);

		img_close.setOnClickListener(this);
		btn_reg_cancel.setOnClickListener(this);
		btn_reg_cnfm.setOnClickListener(this);

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

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new VentaPines(dto.getUserName(), dto.getPassword()).execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		password = ed_key.getText().toString().trim();
		isValid = true;

		if (password.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.enter_key));

		} else {
			if (CommonMethods.keyvalidation(password)) {
				stringBuffer.append(" "
						+ getContext()
								.getString(R.string.select_valid_terminal));
				isValid = false;
			} else {
				isValid = true;
			}
		}

		// if (CommonMethods.keyvalidation(password)) {
		// stringBuffer.append(" "
		// + getContext().getString(R.string.select_key));
		// isValid = false;
		// }else
		// {
		// isValid = true;
		// }
		return isValid;

	}

	private void callPinesDailog2(String string) {
		this.dismiss();
		PinesDailog2 dialog = new PinesDailog2(context, string, pinesProducts,
				cellular);
		dialog.show();
		dialog.setCancelable(false);
	}

	private String getventaPinesjson() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("password", password);
			jsonobj.put("msisdn", cellular);
			jsonobj.put("amount",
					"" + (Long.parseLong(pinesProducts.getValor()) * 100));
			jsonobj.put("productNumber", pinesProducts.getProductNumber());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private class VentaPines extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private String json;
		private boolean flage = false, exception = false, exception1 = false;
		private String encrypt_key, server_res_data;

		VentaPines(String email, String password) {
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
			Log.v("==========", "varahalababu" + getventaPinesjson());
			json = AESTEST.AESCrypt(getventaPinesjson(), key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_ventaPines,
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
					exception1 = true;
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
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
			} else if (flage) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(
							server_res_data, AESTEST
									.AESkeyFromString(sharedpreferences
											.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(context,
							json.getString("message"));
					// callPinesDailog2(AESTEST.AESDecrypt(server_res_data,
					// AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey",
					// ""))));

				} catch (Exception e) {
				}

			} else {
				callPinesDailog2(AESTEST.AESDecrypt(server_res_data, AESTEST
						.AESkeyFromString(sharedpreferences.getString(
								"AutoGenKey", ""))));
			}

		}
	}

}
