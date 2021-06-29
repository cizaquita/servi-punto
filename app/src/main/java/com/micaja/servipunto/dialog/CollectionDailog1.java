package com.micaja.servipunto.dialog;

import java.net.Proxy;
import java.util.Calendar;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.micaja.servipunto.activities.CollectionActivity;
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
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class CollectionDailog1 extends Dialog implements android.view.View.OnClickListener, OnClickListener {

	private Context context;

	private ImageView imgClose;
	ServiApplication appContext;
	private EditText edxtNit, edxtConfirmNit, edxtPaymentcode, edxtCnfrmPaymentcode, edxtValue, edxtCnfrmValue,
			edCellular, edConfirmCellular;
	private String JsonRespondsData, ac_No;

	private static TextView tvDate, txt_purchaseprice1, txtxt_nit, txt_payment_code, confirmtxt__nit,
			txt_confirm_payment_code;

	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	private Button btnCollectionCnfm, btnCollectionCancel, btnCollectionRefresh;
	private boolean isValid;
	private ParsingHandler parsingHandler;
	ConsultaConveniosDTO ccDTO;
	private String nit, ConfirmNit, paymentCode, confirmPaymentCode, value, confirmValue, cellular, confirmCellular;

	public CollectionDailog1(Context context, String JsonRespondsData, String ac_No) {
		super(context);
		this.context = context;
		this.JsonRespondsData = JsonRespondsData;
		this.ac_No = ac_No;
		appContext = (ServiApplication) context.getApplicationContext();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.collection_dailog1);
		edxtNit = (EditText) findViewById(R.id.edxt_nit);
		edxtConfirmNit = (EditText) findViewById(R.id.edxt_confirm__nit);
		edxtPaymentcode = (EditText) findViewById(R.id.ed_payment_code);
		edxtCnfrmPaymentcode = (EditText) findViewById(R.id.ed_confirm_payment_code);
		edxtValue = (EditText) findViewById(R.id.ed_value);
		edxtCnfrmValue = (EditText) findViewById(R.id.ed_confirm_value);
		tvDate = (TextView) findViewById(R.id.tv_date);
		edCellular = (EditText) findViewById(R.id.ed_cellular);
		edConfirmCellular = (EditText) findViewById(R.id.ed_confirm_cellular);
		btnCollectionCnfm = (Button) findViewById(R.id.btn_collection_cnfm);
		btnCollectionCancel = (Button) findViewById(R.id.btn_collection_cancel);
		btnCollectionRefresh = (Button) findViewById(R.id.btn_collection_refresh);

		imgClose = (ImageView) findViewById(R.id.img_close);
		txt_purchaseprice1 = (TextView) findViewById(R.id.txt_purchaseprice1);

		btnCollectionCnfm.setOnClickListener(this);
		btnCollectionCancel.setOnClickListener(this);
		btnCollectionRefresh.setOnClickListener(this);
		tvDate.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		txtxt_nit = (TextView) findViewById(R.id.txtxt_nit);
		txt_payment_code = (TextView) findViewById(R.id.txt_payment_code);
		confirmtxt__nit = (TextView) findViewById(R.id.confirmtxt__nit);
		txt_confirm_payment_code = (TextView) findViewById(R.id.txt_confirm_payment_code);

		loadUI();
	}

	private void loadUI() {
		try {
			ccDTO = new ParsingHandler().getconsultaConvenios(JsonRespondsData);
		} catch (Exception e) {
			ccDTO = new ConsultaConveniosDTO();
		}

		if (ccDTO.getNombreReferencia1().trim().length() > 0) {
			txtxt_nit.setText(CommonMethods.firstLetterasUppercase(ccDTO.getNombreReferencia1()));
			confirmtxt__nit
					.setText(context.getResources().getString(R.string.confirm) + " " + CommonMethods.firstLetterasUppercase(ccDTO.getNombreReferencia1()));

			edxtNit.setFocusable(true);
			edxtConfirmNit.setFocusable(true);

		} else {
			edxtNit.setText(null);
			edxtNit.setFocusable(false);

			edxtConfirmNit.setText(null);
			edxtConfirmNit.setFocusable(false);

			txtxt_nit.setText("");
			confirmtxt__nit.setText("");

			edxtNit.setBackgroundResource(R.drawable.etxt_disable);
			edxtConfirmNit.setBackgroundResource(R.drawable.etxt_disable);

		}

		if (ccDTO.getNombreReferencia2().trim().length() > 0) {

			txt_payment_code.setText(CommonMethods.firstLetterasUppercase(ccDTO.getNombreReferencia2()));
			txt_confirm_payment_code
					.setText(context.getResources().getString(R.string.confirm) + " " + CommonMethods.firstLetterasUppercase(ccDTO.getNombreReferencia2()));

			edxtPaymentcode.setFocusable(true);
			edxtCnfrmPaymentcode.setFocusable(true);

		} else {
			edxtPaymentcode.setText(null);
			edxtPaymentcode.setFocusable(false);

			edxtCnfrmPaymentcode.setText(null);
			edxtCnfrmPaymentcode.setFocusable(false);

			txt_payment_code.setText("");
			txt_confirm_payment_code.setText("");

			edxtPaymentcode.setBackgroundResource(R.drawable.etxt_disable);
			edxtCnfrmPaymentcode.setBackgroundResource(R.drawable.etxt_disable);

		}

		// edxtNit.setFocusable(false);
		// edxtNit.setBackgroundResource(R.drawable.etxt_disable);

		// if (ccDTO.getNombreReferencia1().contains("null")) {
		// edxtPaymentcode.setText(null);
		// } else {
		// edxtPaymentcode.setText(ccDTO.getNombreReferencia1());
		// }
		//
		// if (ccDTO.getNombreReferencia2().contains("null")) {
		// edxtCnfrmPaymentcode.setText(null);
		// } else {
		// edxtCnfrmPaymentcode.setText(ccDTO.getNombreReferencia2());
		// }

		// edxtNit.setBackgroundResource(R.drawable.etxt_disable);
		// edxtNit.setBackgroundResource(R.drawable.etxt_disable);

		if (ccDTO.getValor().contains("null")) {
			edxtValue.setText(null);
		} else {
			edxtValue.setText(ccDTO.getValor());
		}

		// edxtValue.setFocusable(false);
		// edxtValue.setBackgroundResource(R.drawable.etxt_disable);

		txt_purchaseprice1.setText(ccDTO.getDescripcion());

		tvDate.setText(Dates.current_date());
		tvDate.setFocusable(false);
		tvDate.setBackgroundResource(R.drawable.etxt_disable);

		edxtConfirmNit.setText(null);
		edxtCnfrmPaymentcode.setText(null);
		edxtCnfrmValue.setText(null);

		edCellular.setText(null);
		edConfirmCellular.setText(null);
		edxtNit.requestFocus();

	}

	@Override
	public void onClick(View v) {

		DatePicker datePicker = new DatePicker();

		switch (v.getId()) {
		case R.id.btn_collection_cnfm:
			validateAndSearch();

			break;
		case R.id.btn_collection_cancel:
			this.dismiss();
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, CollectionActivity.class);
			break;
		case R.id.btn_collection_refresh:
			refreshService();
			break;

		case R.id.tv_date:
			// datePicker.show(((Activity) context).getFragmentManager(),
			// "datePicker");
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}

	private void refreshService() {
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new RefreshConfirmation(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getString(R.string.no_wifi_adhoc));

		}
	}

	/* RefreshConfirmation json object */
	public String getConsultaConvenio() {

		JSONObject jsonobj = new JSONObject();
		try {

			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("codigoConvenio", ac_No);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	/* RefreshConfirmation Services */

	private class RefreshConfirmation extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private String json;
		private boolean flage = false, exception = false;
		private String encrypt_key, server_res_data;

		RefreshConfirmation(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);

			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getConsultaConvenio(), key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(context, encrypt_key,
						ServiApplication.process_id_Consulta_Convenio, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON_AES"));
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
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {

					server_res_data = new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data");

				} else {
					server_res_data = new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data");
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
			CommonMethods.dismissProgressDialog();
			if (exception) {

			} else if (flage) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(server_res_data,
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(context, json.getString("message"));
					Log.v("varahalababu", "varahalababu" + json.toString());
					JsonRespondsData = json.toString();
					loadUI();
					// movetoCollectionDailog1(AESTEST.AESDecrypt(server_res_data,
					// AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey",
					// ""))));
				} catch (Exception e) {
				}
			} else {
				try {
					// JSONObject json=new
					// JSONObject(AESTEST.AESDecrypt(server_res_data,
					// AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey",
					// ""))));
					// CommonMethods.showCustomToast(context,
					// json.getString("message"));
					// Log.v("varahalababu", "varahalababu"+json.toString());
					JsonRespondsData = AESTEST.AESDecrypt(server_res_data,
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));
					loadUI();
				} catch (Exception e) {
				}
			}

		}
	}

	public void validateAndSearch() {

		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context, getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {

			this.dismiss();
			CollectionDailog2 dialog = new CollectionDailog2(context, nit, paymentCode, value, cellular,
					tvDate.getText().toString(), ccDTO);
			dialog.show();
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		nit = edxtNit.getText().toString().trim();
		ConfirmNit = edxtConfirmNit.getText().toString().trim();
		paymentCode = edxtPaymentcode.getText().toString().trim();
		confirmPaymentCode = edxtCnfrmPaymentcode.getText().toString().trim();
		value = edxtValue.getText().toString().trim();
		confirmValue = edxtCnfrmValue.getText().toString().trim();
		cellular = edCellular.getText().toString().trim();
		confirmCellular = edConfirmCellular.getText().toString().trim();
		isValid = true;
		if (ccDTO.getNombreReferencia1().trim().length() > 0) {
			if (nit.length() == 0) {

				isValid = false;
				stringBuffer.append("\n" + getContext().getString(R.string.invalidnit_n)+" "+ccDTO.getNombreReferencia1());
			}else if(ConfirmNit.length() == 0){
				isValid = false;
				stringBuffer.append("\n" + getContext().getString(R.string.invalidnit1_n)+" "+ccDTO.getNombreReferencia1());
			}else if (!nit.equals(ConfirmNit)) {
				isValid = false;
				stringBuffer.append("\n" + getContext().getString(R.string.invalidnit2_n)+" "+ccDTO.getNombreReferencia1()+" " + getContext().getString(R.string.invalidnit3_n));
			}
		}
		if (ccDTO.getNombreReferencia2().trim().length() > 0) {

			if (paymentCode.length() == 0 ) {
				isValid = false;
				stringBuffer.append("\n" + getContext().getString(R.string.invalidnit_n)+" "+ccDTO.getNombreReferencia2());
			} else if(confirmPaymentCode.length() == 0){
				isValid = false;
				stringBuffer.append("\n" + getContext().getString(R.string.invalidnit1_n)+" "+ccDTO.getNombreReferencia2());
			}else if (!paymentCode.equals(confirmPaymentCode)) {
				isValid = false;
				stringBuffer.append("\n" + getContext().getString(R.string.invalidnit2_n)+" "+ccDTO.getNombreReferencia2()+" "+ getContext().getString(R.string.invalidnit3_n));
			}
		}
		if (value.length() == 0 && confirmValue.length() == 0) {

			isValid = false;
			stringBuffer.append("\n" + getContext().getString(R.string.invalid_code));
		} else if (!value.equals(confirmValue)) {
			isValid = false;
			stringBuffer.append("\n" + getContext().getString(R.string.collection_invalid_value));

		}
		if (cellular.length() == 0 && confirmCellular.length() == 0) {

			isValid = false;
			stringBuffer.append("\n" + getContext().getString(R.string.invalid_cellular));
		} else if (!cellular.equals(confirmCellular)) {
			isValid = false;
			stringBuffer.append("\n" + getContext().getString(R.string.collection_invalid_celular));

		}

		return isValid;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;

			tvDate.setText(date);

		}
	}

}
