package com.micaja.servipunto.dialog;

import java.net.Proxy;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class PaguetigoDetailsDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;
	private EditText etxtKeyalue;
	private Button btn_reg_cancel, btnRegcnfm;
	private TextView txt_operator, txt_number, txt_reg_val, txt_ass_val;
	private String spncellular, number, spnKey;
	private Double double1;
	private boolean service_loan;
	private boolean isValid;
	private LinearLayout layout_assistance1;
	private RechargeDialog2 dialog;
	private Intent intent;
	public SharedPreferences sharedpreferences;
	private RelativeLayout layoutPopupHeader;
	CellularProviderDto cellular;
	private TextView txtPaquetigo, txtNumber, txtRegvalue;
	private ImageView img_close;

	public PaguetigoDetailsDialog(Context context, String spncellular,
			String number, Double double1, boolean service_loan,
			CellularProviderDto cellular) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		this.spncellular = spncellular;
		this.number = number;
		this.double1 = double1;
		this.service_loan = service_loan;
		this.cellular = cellular;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.recharge_dialog1);
		etxtKeyalue = (EditText) findViewById(R.id.etxt_key_value);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btnRegcnfm = (Button) findViewById(R.id.btn_reg_cnfm);
		txt_operator = (TextView) findViewById(R.id.txt_operator);
		txt_number = (TextView) findViewById(R.id.txt_number);
		txt_reg_val = (TextView) findViewById(R.id.txt_reg_val);
		txt_ass_val = (TextView) findViewById(R.id.txt_ass_val);
		layout_assistance1 = (LinearLayout) findViewById(R.id.layout_assistance1);
		layoutPopupHeader = (RelativeLayout) findViewById(R.id.layout_popup_header);

		img_close = (ImageView) findViewById(R.id.img_close);
		img_close.setOnClickListener(this);

		layout_assistance1.setVisibility(View.GONE);
		btn_reg_cancel.setOnClickListener(this);
		btnRegcnfm.setOnClickListener(this);

		txtPaquetigo = (TextView) findViewById(R.id.txt_paquetigo);
		txtPaquetigo.setText(getContext().getString(R.string.paquetigo));

		txtNumber = (TextView) findViewById(R.id.txtnumber);
		txtNumber.setText(getContext().getString(R.string.reg_value));
		txtRegvalue = (TextView) findViewById(R.id.regvalue);
		txtRegvalue.setText(getContext().getString(R.string.cellphone_no));
		setContactNumberLength();
		loadUI();
	}

	@SuppressLint("UseValueOf")
	private void loadUI() {
		txt_operator.setText(cellular.getProductName());
		txt_number.setText("$" + (new Double(cellular.getValor())).longValue());
		txt_reg_val.setText(number);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			this.dismiss();
			break;
		case R.id.img_close:
			this.dismiss();
			break;
		case R.id.btn_reg_cnfm:
			validateAndCnfm();
			break;
		default:
			break;
		}
	}

	private void validateAndCnfm() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			UserDetailsDTO dto = UserDetailsDAO.getInstance()
					.getRecordsuser_name(
							DBHandler.getDBObj(Constants.READABLE),
							sharedpreferences.getString("user_name", ""));
			this.dismiss();
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new GetPagueTigo(dto.getUserName(), dto.getPassword())
						.execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}

		}

	}

	public void moveTosuccessDialog() {
		appContext.pushActivity(intent);
		PaguetigosConfirmDialog dialog = new PaguetigosConfirmDialog(context,
				spnKey, number, ServiApplication.getReg_amt(), service_loan,
				cellular);
		dialog.show();
		dialog.setCancelable(false);
		this.dismiss();
	}

	private boolean validateFields(StringBuffer stringBuffer) {
		spnKey = etxtKeyalue.getText().toString().trim();
		isValid = true;

		if (spnKey.length() == 0) {
			isValid = false;
			stringBuffer.append(" "
					+ getContext().getString(R.string.Please_enter_key));
		} else if (spnKey.length() < 6) {
			isValid = false;
			stringBuffer.append(" "
					+ getContext().getString(R.string.Please_enter_valid_key));
		}
		return isValid;
	}

	/* call microseguros */
	private class GetPagueTigo extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private String encrypt_key, json, res_data;
		private boolean flage = false;
		private boolean exception = false, exception1 = false;

		GetPagueTigo(String email, String password) {
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
			Log.v("varahalababu", "varahalababu" + getJsonObjformicro());
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
								ServiApplication.process_id_Paquetigos,
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
					ServiApplication
							.setRecharge_conform_data(new ParsingHandler()
									.getString(new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
											"response", "data"));

				} else {
					exception1 = true;
					res_data = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
				}

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
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {

				Log.v("varahalababu",
						"varahlababu"
								+ AESTEST.AESDecrypt(res_data, AESTEST
										.AESkeyFromString(sharedpreferences
												.getString("AutoGenKey", ""))));

				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(
							res_data, AESTEST
									.AESkeyFromString(sharedpreferences
											.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(context,
							json.getString("message"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				moveTosuccessDialog();
			}
		}
	}

	private String getJsonObjformicro() {
		UserDetailsDTO dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("password", etxtKeyalue.getText().toString().trim());
			jsonobj.put("msisdn", number);
			jsonobj.put("amount", "" + Integer.parseInt(cellular.getValor())
					* 100);
			jsonobj.put("productNumber", cellular.getProductNumber());
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private void setContactNumberLength() {
		InputFilter maxLengthFilter = new InputFilter.LengthFilter(10);
		etxtKeyalue.setFilters(new InputFilter[] { maxLengthFilter });

	}
}