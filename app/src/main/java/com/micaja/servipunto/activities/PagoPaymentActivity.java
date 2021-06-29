package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.PagoDocumentDTO;
import com.micaja.servipunto.database.dto.PagoProductsDTO;
import com.micaja.servipunto.database.dto.PagoProgramsDTO;
import com.micaja.servipunto.database.dto.PagoProviderDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.dialog.PagoPaymentDialog1;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.EmailValidator;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.SalesCodes;

public class PagoPaymentActivity extends BaseActivity implements OnClickListener {
	private static View dateView;
	ServiApplication appContext;
	private boolean isValid;
	private static boolean isValids = true, pago_provider_selection_flage = false,
			pago_products_selection_flage = false, pago_program_selection_flage = false,
			pago_document_selection_flage = false, pago_document_selection_flage1 = true;
	private Intent intent;
	private LinearLayout spnlayout_tipo_de_document;
	public SharedPreferences sharedpreferences;
	private String sProduct, sProgram, sDocument, sDocumentType, spnProv;
	private Button confirm, cancel;
	private Spinner providerNo, productNo, programNo, document;

	UserDetailsDTO dto;
	private ArrayAdapter<String> providerListAdapter;
	CellularProviderDto cellular;
	// ----------for DialogClass
	private int request_type = 0;

	PagoProviderDTO pagoprovider_dto = new PagoProviderDTO();
	public List<DTO> pago_providers_dato = new ArrayList<DTO>();

	PagoProductsDTO pagoproduct_dto = new PagoProductsDTO();
	public List<DTO> pago_product_dato = new ArrayList<DTO>();

	PagoProgramsDTO pagoprogram_dto = new PagoProgramsDTO();
	public List<DTO> pago_pagoprogram_dato = new ArrayList<DTO>();

	PagoDocumentDTO pagodocument_dto = new PagoDocumentDTO();
	public List<DTO> pago_document_dato = new ArrayList<DTO>();

	AcceptEfectivoDTO documetn = new AcceptEfectivoDTO();
	private List<DTO> Depositodaviplatadato = new ArrayList<DTO>();// documetn,Depositodaviplatadato

	/* Babu Code in this Module */
	private LinearLayout linear_codigo_otplayout, linear_celular, linear_email, linear_get_otp;
	private EditText edt_codigo_otp, edt_celular, edt_conform_celular, edt_email, edt_conform_email,
			ed_numere_de_documento;
	private Button get_otp_button;

	/* Dynamic layout define */

	private LinearLayout dynamic_layout_1, dynamic_layout_2, dynamic_layout_3, dynamic_layout_4;
	private static EditText dynamic_editext_date_1, dynamic_editext_number_1, dynamic_editext_text_1,
			dynamic_editext_date_2, dynamic_editext_number_2, dynamic_editext_text_2, dynamic_editext_date_3,
			dynamic_editext_number_3, dynamic_editext_text_3, dynamic_editext_date_4, dynamic_editext_number_4,
			dynamic_editext_text_4;

	private TextView dynamic_text_1, dynamic_text_2, dynamic_text_3, dynamic_text_4;

	private static boolean date_flage1 = true, date_flage2 = true, date_flage3 = true, date_flage4 = true;
	private static String date_flage_string1, date_flage_string2, date_flage_string3, date_flage_string4;

	private static String EMAIL, CELLNUMBER, OTP, DOCUMENT_NUMBER, codigoDavivienda, giroId, FADA_NUMEROCELULAR,
			GIRO_VALOR;

	// private static boolean editext_number_flage1 = true,
	// editext_number_flage2 = true, editext_number_flage3 = true,
	// editext_number_flage4 = true;
	// private static String editext_number_flage_string1,
	// editext_number_flage_string2, editext_number_flage_string3,
	// editext_number_flage_string4;
	//
	// private static boolean editext_text_flage1 = true, editext_text_flage2 =
	// true, editext_text_flage3 = true,
	// editext_text_flage4 = true;
	// private static String editext_text_flage_string1,
	// editext_text_flage_string2, editext_text_flage_string3,
	// editext_text_flage_string4;

	private static String DynamicFields = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = (ServiApplication) getApplicationContext();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		InitUi();
	}

	private void InitUi() {
		setContentView(R.layout.paymeny_layout);
		spnlayout_tipo_de_document = (LinearLayout) findViewById(R.id.spnlayout_tipo_de_document);

		ed_numere_de_documento = (EditText) findViewById(R.id.ed_numere_de_documento);

		providerNo = (Spinner) findViewById(R.id.spi_provider_id);
		productNo = (Spinner) findViewById(R.id.spi_product_id);
		programNo = (Spinner) findViewById(R.id.spi_programa_id);
		document = (Spinner) findViewById(R.id.spi_tipo_id);

		confirm = (Button) findViewById(R.id.btn_payment_confirm);
		cancel = (Button) findViewById(R.id.btn_payment_cancel);
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);

		spnlayout_tipo_de_document.setVisibility(View.GONE);

		getoperatorsList();

		providerNo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				dynamic_layout_1.setVisibility(View.GONE);
				dynamic_layout_2.setVisibility(View.GONE);
				dynamic_layout_3.setVisibility(View.GONE);
				dynamic_layout_4.setVisibility(View.GONE);
				pago_provider_selection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		productNo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				dynamic_layout_1.setVisibility(View.GONE);
				dynamic_layout_2.setVisibility(View.GONE);
				dynamic_layout_3.setVisibility(View.GONE);
				dynamic_layout_4.setVisibility(View.GONE);
				pago_product_selection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		programNo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				dynamic_layout_1.setVisibility(View.GONE);
				dynamic_layout_2.setVisibility(View.GONE);
				dynamic_layout_3.setVisibility(View.GONE);
				dynamic_layout_4.setVisibility(View.GONE);

				spinner_clickevent(3);
				pago_program_selection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		document.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pago_document_selection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		/* Here is babu code */
		linear_codigo_otplayout = (LinearLayout) findViewById(R.id.linear_codigo_otplayout);
		linear_celular = (LinearLayout) findViewById(R.id.linear_celular);
		linear_email = (LinearLayout) findViewById(R.id.linear_email);
		linear_get_otp = (LinearLayout) findViewById(R.id.linear_get_otp);

		linear_codigo_otplayout.setVisibility(View.GONE);
		linear_celular.setVisibility(View.GONE);
		linear_email.setVisibility(View.GONE);
		linear_get_otp.setVisibility(View.GONE);

		edt_codigo_otp = (EditText) findViewById(R.id.edt_codigo_otp);
		edt_celular = (EditText) findViewById(R.id.edt_celular);
		edt_conform_celular = (EditText) findViewById(R.id.edt_conform_celular);
		edt_email = (EditText) findViewById(R.id.edt_email);
		edt_conform_email = (EditText) findViewById(R.id.edt_conform_email);
		get_otp_button = (Button) findViewById(R.id.get_otp_button);
		get_otp_button.setOnClickListener(this);

		dynamic_layout_1 = (LinearLayout) findViewById(R.id.dynamic_layout_1);
		dynamic_layout_2 = (LinearLayout) findViewById(R.id.dynamic_layout_2);
		dynamic_layout_3 = (LinearLayout) findViewById(R.id.dynamic_layout_3);
		dynamic_layout_4 = (LinearLayout) findViewById(R.id.dynamic_layout_4);

		dynamic_layout_1.setVisibility(View.GONE);
		dynamic_layout_2.setVisibility(View.GONE);
		dynamic_layout_3.setVisibility(View.GONE);
		dynamic_layout_4.setVisibility(View.GONE);

		dynamic_text_1 = (TextView) findViewById(R.id.dynamic_text_1);
		dynamic_text_2 = (TextView) findViewById(R.id.dynamic_text_2);
		dynamic_text_3 = (TextView) findViewById(R.id.dynamic_text_3);
		dynamic_text_4 = (TextView) findViewById(R.id.dynamic_text_4);

		dynamic_editext_date_1 = (EditText) findViewById(R.id.dynamic_editext_date_1);
		dynamic_editext_number_1 = (EditText) findViewById(R.id.dynamic_editext_number_1);
		dynamic_editext_text_1 = (EditText) findViewById(R.id.dynamic_editext_text_1);

		dynamic_editext_date_2 = (EditText) findViewById(R.id.dynamic_editext_date_2);
		dynamic_editext_number_2 = (EditText) findViewById(R.id.dynamic_editext_number_2);
		dynamic_editext_text_2 = (EditText) findViewById(R.id.dynamic_editext_text_2);

		dynamic_editext_date_3 = (EditText) findViewById(R.id.dynamic_editext_date_3);
		dynamic_editext_number_3 = (EditText) findViewById(R.id.dynamic_editext_number_3);
		dynamic_editext_text_3 = (EditText) findViewById(R.id.dynamic_editext_text_3);

		dynamic_editext_date_4 = (EditText) findViewById(R.id.dynamic_editext_date_4);
		dynamic_editext_number_4 = (EditText) findViewById(R.id.dynamic_editext_number_4);
		dynamic_editext_text_4 = (EditText) findViewById(R.id.dynamic_editext_text_4);

		dynamic_editext_date_1.setOnClickListener(this);
		dynamic_editext_date_2.setOnClickListener(this);
		dynamic_editext_date_3.setOnClickListener(this);
		dynamic_editext_date_4.setOnClickListener(this);
	}

	private void loadPaymentConformationscreen(PagoProductsDTO pagoproduct_dto2) {

		Log.v("pagoproduct_dto2.getEmail()", pagoproduct_dto2.getEmail());
		Log.v("pagoproduct_dto2.getSms()", pagoproduct_dto2.getSms());
		Log.v("pagoproduct_dto2.getOtp()", pagoproduct_dto2.getOtp());

		if (pagoproduct_dto2.getEmail().equals("1")) {
			linear_email.setVisibility(View.VISIBLE);
		}
		if (pagoproduct_dto2.getSms().equals("2")) {

		} else {
			linear_celular.setVisibility(View.VISIBLE);
		}

		if (pagoproduct_dto2.getOtp().equals("2")) {
			linear_codigo_otplayout.setVisibility(View.VISIBLE);
			linear_get_otp.setVisibility(View.GONE);
		} else {
			linear_codigo_otplayout.setVisibility(View.GONE);
			linear_get_otp.setVisibility(View.GONE);// I was changed this code
													// based on salomon request
		}
		if (pagoproduct_dto2.getDocRequired().equals("0")) {
			spnlayout_tipo_de_document.setVisibility(View.GONE);
			pago_document_selection_flage1 = true;
		} else {
			spnlayout_tipo_de_document.setVisibility(View.VISIBLE);
			pago_document_selection_flage1 = false;
		}
	}

	public void pago_document_selection(int position) {
		if (position > 0) {
			pago_document_selection_flage = true;
			pago_document_selection_flage1 = true;
			documetn = (AcceptEfectivoDTO) Depositodaviplatadato.get(position);
			sDocumentType = documetn.getDistribuidor();
		} else {
			pago_document_selection_flage = false;
			pago_document_selection_flage1 = false;
		}
	}

	public void pago_program_selection(int position) {
		if (position > 0) {
			pago_program_selection_flage = true;
			pagoprogram_dto = (PagoProgramsDTO) pago_pagoprogram_dato.get(position);
			sProgram = pagoprogram_dto.getName();

			dynamic_editext_date_1.setText(null);
			dynamic_editext_number_1.setText(null);
			dynamic_editext_text_1.setText(null);
			dynamic_editext_date_2.setText(null);
			dynamic_editext_number_2.setText(null);
			dynamic_editext_text_2.setText(null);
			dynamic_editext_date_3.setText(null);
			dynamic_editext_number_3.setText(null);
			dynamic_editext_text_3.setText(null);
			dynamic_editext_date_4.setText(null);
			dynamic_editext_number_4.setText(null);
			dynamic_editext_text_4.setText(null);

			spinner_clickevent(3);
			if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
				request_type = 3;
				new GetConsultation_of_configured_fields(dto.getUserName(), dto.getPassword()).execute();

			} else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
			}

		} else {
			pago_program_selection_flage = false;
		}
	}

	public void pago_provider_selection(int position) {

		if (position > 0) {
			pago_provider_selection_flage = true;
			pagoprovider_dto = (PagoProviderDTO) pago_providers_dato.get(position);
			linear_codigo_otplayout.setVisibility(View.GONE);
			linear_celular.setVisibility(View.GONE);
			linear_email.setVisibility(View.GONE);
			linear_get_otp.setVisibility(View.GONE);
			spnlayout_tipo_de_document.setVisibility(View.GONE);
			pago_document_selection_flage1 = false;
			spinner_clickevent(1);
			spnProv = pagoprovider_dto.getProvDesc();
			if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
				request_type = 1;
				new GetProducts(dto.getUserName(), dto.getPassword()).execute();
			} else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
			}

		} else {
			pago_provider_selection_flage = false;
			linear_codigo_otplayout.setVisibility(View.GONE);
			linear_celular.setVisibility(View.GONE);
			linear_email.setVisibility(View.GONE);
			linear_get_otp.setVisibility(View.GONE);
			spnlayout_tipo_de_document.setVisibility(View.GONE);
			pago_document_selection_flage1 = false;
		}
	}

	public void pago_product_selection(int position) {

		if (position > 0) {
			pago_products_selection_flage = true;
			pagoproduct_dto = (PagoProductsDTO) pago_product_dato.get(position);
			sProduct = pagoproduct_dto.getProdDesc();
			ed_numere_de_documento.setText(null);

			edt_codigo_otp.setText(null);
			edt_celular.setText(null);
			edt_conform_celular.setText(null);
			edt_email.setText(null);
			edt_conform_email.setText(null);
			ed_numere_de_documento.setText(null);

			OTP = "";
			CELLNUMBER = "";
			EMAIL = "";
			DOCUMENT_NUMBER = "";

			spinner_clickevent(2);
			loadPaymentConformationscreen(pagoproduct_dto);
			if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
				request_type = 2;
				new GetPrograms(dto.getUserName(), dto.getPassword()).execute();

			} else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
			}
		} else {
			pago_products_selection_flage = false;
			linear_codigo_otplayout.setVisibility(View.GONE);
			linear_celular.setVisibility(View.GONE);
			linear_email.setVisibility(View.GONE);
			linear_get_otp.setVisibility(View.GONE);
			spnlayout_tipo_de_document.setVisibility(View.GONE);
		}
	}

	private void getoperatorsList() {
		if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
			new GetProvider(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	private void spinner_clickevent(int type) {
		String[] val = {};
		providerListAdapter = new ArrayAdapter<String>(PagoPaymentActivity.this, android.R.layout.simple_spinner_item,
				val);
		providerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (type == 1) {
			productNo.setAdapter(providerListAdapter);
			programNo.setAdapter(providerListAdapter);
			document.setAdapter(providerListAdapter);

			pagoproduct_dto = null;
			pago_product_dato.clear();

			pagoprogram_dto = null;
			pago_pagoprogram_dato.clear();

			pagodocument_dto = null;
			pago_document_dato.clear();

			documetn = null;
			Depositodaviplatadato.clear();

			pago_products_selection_flage = false;
			pago_program_selection_flage = false;
			pago_document_selection_flage = false;

		} else if (type == 2) {
			pagoprogram_dto = null;
			pago_pagoprogram_dato.clear();

			pagodocument_dto = null;
			pago_document_dato.clear();

			documetn = null;
			Depositodaviplatadato.clear();

			pago_program_selection_flage = false;
			pago_document_selection_flage = false;
			programNo.setAdapter(providerListAdapter);
			document.setAdapter(providerListAdapter);

		} else if (type == 3) {
			pagodocument_dto = null;
			pago_document_dato.clear();

			documetn = null;

			Depositodaviplatadato.clear();

			pago_document_selection_flage = false;
			document.setAdapter(providerListAdapter);

		} else if (type == 4) {

		}
	}

	@Override
	public void onClick(View v) {
		dateView = v;
		DatePicker datePicker = new DatePicker();

		switch (v.getId()) {

		case R.id.btn_payment_confirm:
			validateAndSave();

			break;
		case R.id.btn_payment_cancel:
			showMenuScreen(v);
			break;
		case R.id.get_otp_button:
			call_consulta_de_la_OTP();
			break;

		case R.id.dynamic_editext_date_1:
			dateView = v;
			datePicker.show(getFragmentManager(), "datePicker");
			break;

		case R.id.dynamic_editext_date_2:
			dateView = v;
			datePicker.show(getFragmentManager(), "datePicker");
			break;
		case R.id.dynamic_editext_date_3:
			dateView = v;
			datePicker.show(getFragmentManager(), "datePicker");
			break;
		case R.id.dynamic_editext_date_4:
			dateView = v;
			datePicker.show(getFragmentManager(), "datePicker");
			break;
		default:
			break;
		}

	}

	private void call_consulta_de_la_OTP() {
		if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
			new GetConsulta_De_La_OTP(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.displayAlert(true, getResources().getString(R.string.fail), stringBuffer.toString(),
					getResources().getString(R.string.ok), "", PagoPaymentActivity.this, null, false);
		} else {

			// if (OTP.length()>0) {
			// PagoPaymentDialog1 dialog = new PagoPaymentDialog1(
			// PagoPaymentActivity.this, spnProv, sProduct, sProgram,
			// sDocumentType, EMAIL, CELLNUMBER, OTP, DOCUMENT_NUMBER,
			// DynamicFields, pagoprovider_dto, pagoproduct_dto,
			// pagodocument_dto, documetn,giroId,codigoDavivienda);
			// dialog.show();
			// dialog.setCancelable(false);
			// }else{
			// if
			// (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this))
			// {
			// new ConsultavalidarInfoPago(dto.getUserName(),
			// dto.getPassword()).execute();
			// } else {
			// CommonMethods.showCustomToast(this,
			// getResources().getString(R.string.no_wifi_adhoc));
			// }
			// }

			if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
				new ConsultavalidarInfoPago(dto.getUserName(), dto.getPassword()).execute();
			} else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}

	}

	/* call Recharges confirmation */
	public class ConsultavalidarInfoPago extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean exception = false, exception1 = false;
		private String encrypt_key;
		private String json, server_res;

		public ConsultavalidarInfoPago(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjValidarInfopago(), key);
			Log.v("varahalababu", getJsonObjValidarInfopago());
		}

		@Override
		public Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this,
						encrypt_key, ServiApplication.process_id_validarInfo_pago, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("DUMP REQUEST", "varahalababu" + ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.v("SOAP", "varahalababu rec" + resultsString.toString());
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					ServiApplication.setRecharge_conform_data(new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"));

					server_res = AESTEST.AESDecrypt(
							new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));
				} else {
					exception1 = true;
					server_res = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
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
		public void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(server_res,
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));
					// movetoNextDialog();
				} catch (Exception e) {
					appContext.pushActivity(intent);
					OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
					dialog.show();
					dialog.setCancelable(false);
				}
			} else {
				try {
					JSONObject json = new JSONObject(server_res);
					String data = json.getString("message");
					JSONArray json_arry = new JSONArray(data);
					Log.v("varahalababu", "varahalababu json res" + data);
					for (int i = 0; i < json_arry.length(); i++) {
						JSONObject c_json = json_arry.getJSONObject(i);
						codigoDavivienda = "" + c_json.getLong("CODA_ID");
						giroId = "" + c_json.getLong("GIRO_ID");
						FADA_NUMEROCELULAR = c_json.getString("FADA_NUMEROCELULAR");
						GIRO_VALOR = "" + c_json.getLong("GIRO_VALOR");
					}

				} catch (Exception e) {
					Log.v("varahalababu", "varahalababu rec" + "exception");
				}

				if (OTP.length() > 0) {
					movetoNextDialog();
				} else {
					linear_get_otp.setVisibility(View.VISIBLE);
				}
			}

			// samplePrint();
		}
	}

	public void movetoNextDialog() {
		PagoPaymentDialog1 dialog = new PagoPaymentDialog1(PagoPaymentActivity.this, spnProv, sProduct, sProgram,
				sDocumentType, EMAIL, CELLNUMBER, OTP, DOCUMENT_NUMBER, DynamicFields, pagoprovider_dto,
				pagoproduct_dto, pagodocument_dto, documetn, giroId, codigoDavivienda, GIRO_VALOR);
		dialog.show();
		dialog.setCancelable(false);
	}

	public void samplePrint() {
		ServiApplication.himp_print_flage = false;
		ServiApplication.flage_for_log_print = true;
		ServiApplication.setUiHandler(uuiHandler);

		ServiApplication.print_flage = "EncabezadoProducto" + "\n" + "TRANSACCION EXITOSA" + "\n" + "DOCUMENTO" + "   "
				+ "cDocument" + "\n" + "FECHA DE PAGO" + "   " + "2016-01-08" + " " + "20:20:20" + "\n" + "VALOR"
				+ "  $   " + "10000" + "\n" + "TRANSACCION" + "   " + "7842947494" + "\n" + "USUARIO" + "   "
				+ sharedpreferences.getString("name_of_store", "") + "\n" + "pieProducto";

		Log.v("varahalababu", ServiApplication.print_flage);

		ServiApplication.print_flage_eps = ServiApplication.print_flage;
		Intent serverIntent = new Intent(PagoPaymentActivity.this, PRTSDKApp.class);
		appContext.pushActivity(serverIntent);
		serverIntent.putExtra("babu", "print");
		startActivityForResult(serverIntent, 10);

	}

	private final Handler uuiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				
			} else {
			}
		}
	};

	private String getJsonObj() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			if (request_type == 1) {
				jsonobj.put("providerId", pagoprovider_dto.getId());
			} else if (request_type == 2) {
				jsonobj.put("provId", pagoprovider_dto.getId());
				jsonobj.put("prodCode", pagoproduct_dto.getProdCode());
			} else if (request_type == 3) {
				jsonobj.put("progId", pagoprogram_dto.getProgId());
				jsonobj.put("prodCode", pagoproduct_dto.getProdCode());
			}
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private String getJsonObj_for_opt() {
		JSONObject jsonobj = new JSONObject();
		try {
			// jsonobj.put("giroId", "794114");
			// jsonobj.put("phoneNumber", "2147483647");

			jsonobj.put("giroId", giroId);
			jsonobj.put("phoneNumber", FADA_NUMEROCELULAR);

			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private boolean validateFields(StringBuffer stringBuffer) {

		Log.v("varun", "" + date_flage1 + date_flage2 + date_flage3 + date_flage4);

		isValid = true;
		if (pago_provider_selection_flage) {
		} else {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.debe_seleccionar_el_proveedores));
		}

		if (pago_products_selection_flage) {
		} else {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.debe_seleccionar_el_producto));
		}
		if (pago_program_selection_flage) {
		} else {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.debe_seleccionar_el_programo));
		}

		if (pago_document_selection_flage1) {

		} else {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.debe_ingresar_el_numero_de_documentos));
		}

		if (date_flage1) {

		} else {
			isValid = false;
			stringBuffer.append("\n" + date_flage_string1);
			Log.v("varun", "1");
		}

		if (date_flage2) {

		} else {
			isValid = false;
			stringBuffer.append("\n" + date_flage_string2);
			Log.v("varun", "2");
		}

		if (date_flage3) {

		} else {
			isValid = false;
			stringBuffer.append("\n" + date_flage_string3);
			Log.v("varun", "3");
		}
		if (date_flage4) {

		} else {
			isValid = false;
			stringBuffer.append("\n" + date_flage_string4);
			Log.v("varun", "4");
		}

		if (getDynamicFieldValidation(stringBuffer)) {

		} else {
			isValid = false;
		}

		if (getPaymentConfirmation(stringBuffer)) {

		} else {
			isValid = false;
		}

		return isValid;
	}

	private boolean getPaymentConfirmation(StringBuffer stringBuffer) {
		boolean dynamic_flage = true;
		try {
			if (pagoproduct_dto.getEmail().equals("1")) {
				if (edt_email.getText().toString().length() > 0) {

					if (new EmailValidator().validate(edt_email.getText().toString())) {
						if (edt_email.getText().toString().equals(edt_conform_email.getText().toString())) {
							EMAIL = edt_email.getText().toString();
						} else {
							dynamic_flage = false;
							if (edt_conform_email.getText().toString().length() > 2) {
								stringBuffer.append(
										"\n" + getResources().getString(R.string.email_and_confirmation_are_defferent));

							} else {
								stringBuffer.append("\n" + getResources().getString(R.string.cnfm_email_id));
							}
						}
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.invalid_email));
					}

				} else {
					dynamic_flage = false;
					stringBuffer.append("\n" + getResources().getString(R.string.email_id));
				}

			} else {

			}

			if (pagoproduct_dto.getSms().equals("2")) {

			} else {
				if (edt_celular.getText().toString().length() > 0) {
					if (edt_celular.getText().toString().length() == 10) {
						if (edt_celular.getText().toString().equals(edt_conform_celular.getText().toString())) {
							CELLNUMBER = edt_celular.getText().toString();
						} else {
							dynamic_flage = false;
							stringBuffer.append("\n" + getResources().getString(R.string.cnfm_cell_number));
						}
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.invalid_phonenum));
					}
				} else {
					dynamic_flage = false;
					stringBuffer.append("\n" + getResources().getString(R.string.cell_number));
				}
			}
			if (pagoproduct_dto.getOtp().equals("2")) {
				if (edt_codigo_otp.getText().toString().length() > 0) {
					OTP = edt_codigo_otp.getText().toString();
				} else {
					dynamic_flage = false;
					stringBuffer.append("\n" + getResources().getString(R.string.otp));
				}

			} else {

			}
			if (pagoproduct_dto.getDocRequired().equals("0")) {

			} else {
				if (ed_numere_de_documento.getText().toString().length() > 0) {
					DOCUMENT_NUMBER = ed_numere_de_documento.getText().toString();
				} else {
					dynamic_flage = false;
					stringBuffer.append("\n" + getResources().getString(R.string.enter_document_number));
				}
			}
		} catch (Exception e) {
			dynamic_flage = true;
		}
		return dynamic_flage;
	}

	private boolean getDynamicFieldValidation(StringBuffer stringBuffer) {
		boolean dynamic_flage = true;
		int position = 0;
		DynamicFields = "";
		for (DTO dto : pago_document_dato) {
			PagoDocumentDTO pago_dynamic_page = (PagoDocumentDTO) dto;
			position++;
			if (position == 1) {
				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					if (dynamic_editext_number_1.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_number_1.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {

					DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
							+ dynamic_editext_date_1.getText().toString().replace("-", "") + "|";

				} else {
					if (dynamic_editext_text_1.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_text_1.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				}
			} else if (position == 2) {
				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					if (dynamic_editext_number_2.getText().toString().length() > 0) {

						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_number_2.getText().toString() + "|";

					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
							+ dynamic_editext_date_2.getText().toString().replace("-", "") + "|";
				} else {
					if (dynamic_editext_text_2.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_text_2.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				}
			} else if (position == 3) {
				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					if (dynamic_editext_number_3.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_number_3.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
							+ dynamic_editext_date_3.getText().toString().replace("-", "") + "|";
				} else {
					if (dynamic_editext_text_3.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_text_3.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				}
			} else if (position == 4) {

				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					if (dynamic_editext_number_4.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_text_4.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
							+ dynamic_editext_date_4.getText().toString().replace("-", "") + "|";
				} else {
					if (dynamic_editext_text_4.getText().toString().length() > 0) {
						DynamicFields = DynamicFields + pago_dynamic_page.getField() + "|"
								+ dynamic_editext_text_4.getText().toString() + "|";
					} else {
						dynamic_flage = false;
						stringBuffer.append("\n" + getResources().getString(R.string.please_enter) + " "
								+ pago_dynamic_page.getName().toLowerCase());
					}
				}
			}
		}

		Log.v("varahlababu", "=============>" + DynamicFields);
		return dynamic_flage;
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
			if (dateView.getId() == R.id.dynamic_editext_date_4) {
				dynamic_editext_date_4.setText(date);
				date_flage4 = true;
			} else if (dateView.getId() == R.id.dynamic_editext_date_1) {
				dynamic_editext_date_1.setText(date);
				date_flage1 = true;
			} else if (dateView.getId() == R.id.dynamic_editext_date_2) {
				dynamic_editext_date_2.setText(date);
				date_flage2 = true;
			} else if (dateView.getId() == R.id.dynamic_editext_date_3) {
				dynamic_editext_date_3.setText(date);
				date_flage3 = true;
			}
		}
	}

	private List<String> getProviders(List<DTO> dato) {
		List<String> list = new ArrayList<String>();
		for (DTO dto : dato) {
			if (request_type == 0) {
				PagoProviderDTO supplier = (PagoProviderDTO) dto;
				list.add(supplier.getProvDesc());
			} else if (request_type == 1) {
				PagoProductsDTO supplier = (PagoProductsDTO) dto;
				list.add(supplier.getProdDesc());
			} else if (request_type == 2) {
				PagoProgramsDTO supplier = (PagoProgramsDTO) dto;
				list.add(supplier.getName());
			} else if (request_type == 3) {
				AcceptEfectivoDTO supplier = (AcceptEfectivoDTO) dto;
				list.add(supplier.getDistribuidor());
			} else if (request_type == 4) {
				AcceptEfectivoDTO supplier = (AcceptEfectivoDTO) dto;
				list.add(supplier.getDistribuidor());
			}
		}
		return list;
	}

	public class GetProvider extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public GetProvider(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this,
						encrypt_key, ServiApplication.process_id_paGoproviders, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("varahalababu", ht.responseDump);
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					pago_providers_dato = new ParsingHandler().getPagoProviders(new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							PagoPaymentActivity.this, 1);
				} else {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}

			return true;
		}

		private HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, ServiApplication.SOAP_URL,
					ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;

		}

		private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
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
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);

			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));
				} catch (Exception e) {

				}
			} else {
				try {
					if (pago_providers_dato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(PagoPaymentActivity.this,
								android.R.layout.simple_spinner_item, getProviders(pago_providers_dato));
						providerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						providerNo.setAdapter(providerListAdapter);
					}
				} catch (Exception e) {

				}
			}
		}

	}

	public class GetProducts extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public GetProducts(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this,
						encrypt_key, ServiApplication.process_id_paGoproduct, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("varun", ht.requestDump);
				Log.v("varun", ht.responseDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					pago_product_dato = new ParsingHandler().getPagoProducts(new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							PagoPaymentActivity.this, 1);
				} else {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}

			return true;
		}

		private HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, ServiApplication.SOAP_URL,
					ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;

		}

		private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
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
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {

				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));

				} catch (Exception e) {

				}
			} else {
				try {
					if (pago_product_dato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(PagoPaymentActivity.this,
								android.R.layout.simple_spinner_item, getProviders(pago_product_dato));
						providerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						productNo.setAdapter(providerListAdapter);

						// call_consulta_de_la_OTP();

					}
				} catch (Exception e) {

				}
			}
		}
	}

	public class GetPrograms extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public GetPrograms(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
			date_flage1 = true;
			date_flage2 = true;
			date_flage3 = true;
			date_flage4 = true;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this,
						encrypt_key, ServiApplication.process_id_paGoprogram, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("varahalababu", ht.responseDump);
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					pago_pagoprogram_dato = new ParsingHandler().getPagoPrograms(new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							PagoPaymentActivity.this, 1);
				} else {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}

			return true;
		}

		private HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, ServiApplication.SOAP_URL,
					ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;

		}

		private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
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
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {

				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));

				} catch (Exception e) {

				}
			} else {
				try {
					if (pago_product_dato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(PagoPaymentActivity.this,
								android.R.layout.simple_spinner_item, getProviders(pago_pagoprogram_dato));
						providerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						programNo.setAdapter(providerListAdapter);
					}
				} catch (Exception e) {

				}
			}
		}
	}

	public class GetConsultation_of_configured_fields extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public GetConsultation_of_configured_fields(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this,
						encrypt_key, ServiApplication.process_id_paGoConsulta_de_campos_configurados,
						ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("=========", ht.requestDump);
				Log.v("==========", ht.requestDump);
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					pago_document_dato = new ParsingHandler().getpago_document_dato(new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							PagoPaymentActivity.this, 1);

				} else {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
				Log.d("response", ht.requestDump);
				Log.d("response", ht.responseDump);
				Log.d("response", resultsString.toString());

			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}

			return true;
		}

		private HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, ServiApplication.SOAP_URL,
					ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;

		}

		private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
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
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {

				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));
				} catch (Exception e) {

				}
			} else {
				try {
					if (pago_document_dato.size() > 0) {
						displayDynamicFieldsData();
					}
				} catch (Exception e) {

				}
			}
			getconsultartipoDocumento();
		}

	}

	private void getconsultartipoDocumento() {
		if (NetworkConnectivity.netWorkAvailability(PagoPaymentActivity.this)) {
			new ConsultarTipoDocumento(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	public void displayDynamicFieldsData() {
		int position = 0;
		for (DTO dto : pago_document_dato) {
			PagoDocumentDTO pago_dynamic_page = (PagoDocumentDTO) dto;
			position++;
			String mlength = pago_dynamic_page.getMaxlength();
			Log.v("varahalababu", "varahalababu_dynamic fields assignment" + pago_dynamic_page.getType());
			if (position == 1) {
				dynamic_layout_1.setVisibility(View.VISIBLE);
				dynamic_text_1.setText(CommonMethods.firstLetterasUppercase(pago_dynamic_page.getName()));

				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					dynamic_editext_number_1.setVisibility(View.VISIBLE);
					dynamic_editext_date_1.setVisibility(View.GONE);
					dynamic_editext_text_1.setVisibility(View.GONE);
					dynamic_editext_number_1
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });
				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					dynamic_editext_number_1.setVisibility(View.GONE);
					dynamic_editext_date_1.setVisibility(View.VISIBLE);
					dynamic_editext_text_1.setVisibility(View.GONE);
					date_flage1 = false;
					date_flage_string1 = getResources().getString(R.string.please_select) + " "
							+ pago_dynamic_page.getName().toLowerCase();

					// dynamic_editext_date_1
					// .setFilters(new InputFilter[] { new
					// InputFilter.LengthFilter(
					// Integer.parseInt(mlength)) });
				} else {

					dynamic_editext_number_1.setVisibility(View.GONE);
					dynamic_editext_date_1.setVisibility(View.GONE);
					dynamic_editext_text_1.setVisibility(View.VISIBLE);
					dynamic_editext_text_1
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });

				}
			} else if (position == 2) {
				dynamic_layout_2.setVisibility(View.VISIBLE);
				dynamic_text_2.setText(CommonMethods.firstLetterasUppercase(pago_dynamic_page.getName()));

				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					dynamic_editext_number_2.setVisibility(View.VISIBLE);
					dynamic_editext_date_2.setVisibility(View.GONE);
					dynamic_editext_text_2.setVisibility(View.GONE);

					dynamic_editext_number_2
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });

				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					dynamic_editext_number_2.setVisibility(View.GONE);
					dynamic_editext_date_2.setVisibility(View.VISIBLE);
					dynamic_editext_text_2.setVisibility(View.GONE);
					date_flage2 = false;
					date_flage_string2 = getResources().getString(R.string.please_select) + " "
							+ pago_dynamic_page.getName().toLowerCase();

					// dynamic_editext_date_2
					// .setFilters(new InputFilter[] { new
					// InputFilter.LengthFilter(
					// Integer.parseInt(mlength)) });

				} else {

					dynamic_editext_number_2.setVisibility(View.GONE);
					dynamic_editext_date_2.setVisibility(View.GONE);
					dynamic_editext_text_2.setVisibility(View.VISIBLE);

					dynamic_editext_text_2
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });

				}
			} else if (position == 3) {
				dynamic_layout_3.setVisibility(View.VISIBLE);
				dynamic_text_3.setText(CommonMethods.firstLetterasUppercase(pago_dynamic_page.getName()));
				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					dynamic_editext_number_3.setVisibility(View.VISIBLE);
					dynamic_editext_date_3.setVisibility(View.GONE);
					dynamic_editext_text_3.setVisibility(View.GONE);

					dynamic_editext_number_3
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });
				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					dynamic_editext_number_3.setVisibility(View.GONE);
					dynamic_editext_date_3.setVisibility(View.VISIBLE);
					dynamic_editext_text_3.setVisibility(View.GONE);
					date_flage3 = false;
					date_flage_string3 = getResources().getString(R.string.please_select) + " "
							+ pago_dynamic_page.getName().toLowerCase();
					// dynamic_editext_date_3
					// .setFilters(new InputFilter[] { new
					// InputFilter.LengthFilter(
					// Integer.parseInt(mlength)) });

				} else {
					dynamic_editext_number_3.setVisibility(View.GONE);
					dynamic_editext_date_3.setVisibility(View.GONE);
					dynamic_editext_text_3.setVisibility(View.VISIBLE);
					dynamic_editext_text_3
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });

				}
			} else if (position == 4) {
				dynamic_layout_4.setVisibility(View.VISIBLE);

				dynamic_text_4.setText(CommonMethods.firstLetterasUppercase(pago_dynamic_page.getName()));

				if (pago_dynamic_page.getType().equalsIgnoreCase("NUMERICO")) {
					dynamic_editext_number_4.setVisibility(View.VISIBLE);
					dynamic_editext_date_4.setVisibility(View.GONE);
					dynamic_editext_text_4.setVisibility(View.GONE);

					dynamic_editext_number_4
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });

				} else if (pago_dynamic_page.getType().equalsIgnoreCase("CALENDARIO")) {
					dynamic_editext_number_4.setVisibility(View.GONE);
					dynamic_editext_date_4.setVisibility(View.VISIBLE);
					dynamic_editext_text_4.setVisibility(View.GONE);
					date_flage4 = false;
					date_flage_string4 = getResources().getString(R.string.please_select) + " "
							+ pago_dynamic_page.getName().toLowerCase();

					// dynamic_editext_date_4
					// .setFilters(new InputFilter[] { new
					// InputFilter.LengthFilter(
					// Integer.parseInt(mlength)) });
				} else {
					dynamic_editext_number_4.setVisibility(View.GONE);
					dynamic_editext_date_4.setVisibility(View.GONE);
					dynamic_editext_text_4.setVisibility(View.VISIBLE);

					dynamic_editext_text_4
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Integer.parseInt(mlength)) });
				}
			}
		}
	}

	public class ConsultarTipoDocumento extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json;
		private String encrypt_key, json_responds;

		public ConsultarTipoDocumento(String email, String password) {
			mEmail = password;
			mPassword = password;
			request_type = 0;
			json = getJsonObj();
			request_type = 4;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(
						MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this, encrypt_key,
								ServiApplication.process_id_ConsultarTipoDocumento, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("============>", ht.requestDump);
				Log.v("============>", ht.responseDump);
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					Depositodaviplatadato = new ParsingHandler().getTipoDiposts(new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							PagoPaymentActivity.this);
				} else {
					json_responds = new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data");

					exception1 = true;
				}
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
		protected void onPostExecute(Boolean result) {
			CommonMethods.dismissProgressDialog();

			// documetn,Depositodaviplatadato
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));
				} catch (Exception e) {
				}
			} else {
				try {
					if (Depositodaviplatadato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(PagoPaymentActivity.this,
								android.R.layout.simple_spinner_item, getProviders(Depositodaviplatadato));
						providerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						document.setAdapter(providerListAdapter);

					} else {
						document.setAdapter(null);
					}
				} catch (Exception e) {

				}
			}
		}
	}

	public class GetConsulta_De_La_OTP extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json;
		private String encrypt_key, json_responds;

		public GetConsulta_De_La_OTP(String email, String password) {
			mEmail = password;
			mPassword = password;
			request_type = 0;
			json = getJsonObj_for_opt();
			request_type = 4;
			Log.v("===========", getJsonObj_for_opt());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), PagoPaymentActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(json, key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(PagoPaymentActivity.this,
						encrypt_key, ServiApplication.process_id_consulta_de_la_OTP, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("============>", ht.requestDump);
				Log.v("============>", ht.responseDump);
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					json_responds = AESTEST.AESDecrypt(
							new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));
				} else {
					json_responds = AESTEST.AESDecrypt(
							new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));

					exception1 = true;
				}
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
		protected void onPostExecute(Boolean result) {
			CommonMethods.dismissProgressDialog();

			// documetn,Depositodaviplatadato
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(PagoPaymentActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(PagoPaymentActivity.this, json.getString("message"));
				} catch (Exception e) {
				}
			} else {
				try {
					JSONObject json = new JSONObject(json_responds);
					// CommonMethods.showCustomToast(PagoPaymentActivity.this,
					// json.getString("message"));
					OTP = json.getString("message");
					movetoNextDialog();
				} catch (Exception e) {
				}
			}
		}
	}

	public String getJsonObjValidarInfopago() {

		JSONObject jsonobj = new JSONObject();
		try {
			// jsonobj.put("comercioId", dto.getComercioId());
			// jsonobj.put("terminalId", dto.getTerminalId());
			// jsonobj.put("puntoDeVentaId", dto.getPuntoredId());

			try {
				if (DynamicFields.length() > 0) {
					DynamicFields = DynamicFields.substring(0, DynamicFields.length());
				}
			} catch (Exception e) {

			}

			jsonobj.put("progId", pagoprogram_dto.getProgId());
			if (null != DynamicFields) {
				jsonobj.put("query", DynamicFields);
			} else {
				jsonobj.put("query", "");
			}
			if (null != DOCUMENT_NUMBER) {
				jsonobj.put("document", DOCUMENT_NUMBER);
			} else {
				jsonobj.put("document", "");
			}
			try {
				if (null != documetn.getIdCentroAcopio()) {
					jsonobj.put("documentType", documetn.getIdCentroAcopio());
				} else {
					jsonobj.put("documentType", "");
				}
			} catch (Exception e) {
				jsonobj.put("documentType", "");
			}
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
