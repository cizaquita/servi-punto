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
import com.micaja.servipunto.database.dto.ConsultaConveniosDTO;
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

public class CollectionDailog2 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;

	private ImageView img_close;
	private Button btnCollectionCnfm, btnCollectionCancel;
	private TextView tv_agrement, tv_c_or_night, tv_payment_code, tv_value,
			tv_date, tv_number_celular;
	private EditText ed_key;
	private boolean isValid;
	private String Key, nit, paymentCode, value, cellular, tvDate;
	ConsultaConveniosDTO ccDTO;

	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	public CollectionDailog2(Context context, String nit, String paymentCode,
			String value, String cellular, String tvDate,
			ConsultaConveniosDTO ccDTO) {
		super(context);
		this.context = context;
		this.nit = nit;
		this.paymentCode = paymentCode;
		this.value = value;
		this.cellular = cellular;
		this.tvDate = tvDate;
		this.ccDTO = ccDTO;
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
		setContentView(R.layout.collections_dailog2);
		img_close = (ImageView) findViewById(R.id.img_close);
		btnCollectionCancel = (Button) findViewById(R.id.btn_collection_cancel);
		btnCollectionCnfm = (Button) findViewById(R.id.btn_collection_cnfm);

		tv_agrement = (TextView) findViewById(R.id.tv_agrement);
		tv_c_or_night = (TextView) findViewById(R.id.tv_c_or_night);
		tv_payment_code = (TextView) findViewById(R.id.tv_payment_code);

		tv_value = (TextView) findViewById(R.id.tv_value);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_number_celular = (TextView) findViewById(R.id.tv_number_celular);

		ed_key = (EditText) findViewById(R.id.ed_key);
		loadUI();

	}

	private void loadUI() {
		tv_agrement.setText(ccDTO.getDescripcion());
		tv_c_or_night.setText(nit);
		tv_payment_code.setText(paymentCode);
		tv_value.setText("$ "
				+ (new Double(Double.parseDouble(value)).longValue()));
		tv_date.setText(tvDate);
		tv_number_celular.setText(cellular);
		img_close.setOnClickListener(this);
		btnCollectionCancel.setOnClickListener(this);
		btnCollectionCnfm.setOnClickListener(this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_collection_cancel:
			this.dismiss();
			break;

		case R.id.btn_collection_cnfm:
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
				new Recaudos(dto.getUserName(), dto.getPassword()).execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}

		}

	}

	private void moveToFinalDialog(String resjson) {
		this.dismiss();
		CollectionDailog3 dialog = new CollectionDailog3(context, context, nit,
				paymentCode, value, cellular, tvDate, ccDTO, resjson);
		dialog.show();
		dialog.setCancelable(false);
	}

	private boolean validateFields(StringBuffer stringBuffer) {
		Key = ed_key.getText().toString().trim();
		isValid = true;
		if (CommonMethods.keyvalidation(Key)) {
			stringBuffer.append(" "
					+ getContext().getString(R.string.enter_key));
			isValid = false;
		} else {
			isValid = true;
		}

		return isValid;
	}

	private class Recaudos extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private String json;
		private boolean flage = false, exception = false;
		private String encrypt_key, server_res_data;

		Recaudos(String email, String password) {
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
			json = AESTEST.AESCrypt(recaudos(), key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_recaudos,
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
					// moveToFinalDialog(AESTEST.AESDecrypt(server_res_data,
					// AESTEST
					// .AESkeyFromString(sharedpreferences.getString(
					// "AutoGenKey", ""))));
				} catch (Exception e) {

				}
			} else {
				moveToFinalDialog(AESTEST.AESDecrypt(server_res_data, AESTEST
						.AESkeyFromString(sharedpreferences.getString(
								"AutoGenKey", ""))));
			}
		}
	}

	public String recaudos() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("password", Key);
			jsonobj.put("amount", "" + (Long.parseLong(value) * 100));
			jsonobj.put("amount", "" + (Long.parseLong(value)));
			jsonobj.put("productoID", "338");
			jsonobj.put("codigoConvenio", ccDTO.getCodigoConvenio());
			jsonobj.put("codigoEntidad", ccDTO.getCodigoEntidad());
			jsonobj.put("codigoIAC", ccDTO.getIac());
			jsonobj.put("cuentaDestino", ccDTO.getCuentaDestino());
			jsonobj.put("tipoCuentaDestino", ccDTO.getTipoCuentaDestino());
			jsonobj.put("referencia1", nit);
			jsonobj.put("referencia2", paymentCode);
			jsonobj.put("cuentaRecaudadora", ccDTO.getCuentaRecaudadora());
			jsonobj.put("tipoCuentaRecaudadora",
					ccDTO.getTipoCuentaRecaudadora());
			jsonobj.put("convID", ccDTO.getConvId());
			jsonobj.put("msisdn", cellular);
			jsonobj.put("descripcion", ccDTO.getDescripcion());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

}
