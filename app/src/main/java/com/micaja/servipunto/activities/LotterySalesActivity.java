package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ConsultarLoteriasDTO;
import com.micaja.servipunto.database.dto.ConsultarSeriesLoteriasDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.LotterySalesDailog1;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class LotterySalesActivity extends Activity implements OnClickListener {

	ServiApplication appContext;

	private Spinner spn_select_lot;
	private ArrayAdapter<String> loterryListAdapter;
	private AutoCompleteTextView act_number;

	private Button btn_cellularSave, btn_cellularCancel;

	private EditText ed_document, ed_cellular;
	private String spnlotery = null, NUMBER;
	private boolean isValid;
	private String actNumber, edCellular, edDocument, numbers4;
	private boolean operation_select_event = false, type = false,
			select_position_flage = false;
	public SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;
	public List<DTO> ldto = new ArrayList<DTO>();
	public List<DTO> csldto = new ArrayList<DTO>();
	private ArrayAdapter<String> providerListAdapter;
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	ConsultarLoteriasDTO clDTO;
	ConsultarSeriesLoteriasDTO cslDTO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
		loadUI();
		consultarLoterias();
	}
 // Result of this method,calling ConsultarLoterias service to bind lotteryid's
	private void consultarLoterias() {
		if (NetworkConnectivity.netWorkAvailability(LotterySalesActivity.this)) {
			new ConsultarLoterias(dto.getUserName(), dto.getPassword())
					.execute();
		} else {
			CommonMethods.showCustomToast(LotterySalesActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}

	}
	// Result of this method,registration for all UI views.
	private void initUI() {

		setContentView(R.layout.lottery_sales);

		spn_select_lot = (Spinner) findViewById(R.id.spn_select_lot);
		act_number = (AutoCompleteTextView) findViewById(R.id.act_number);
		btn_cellularSave = (Button) findViewById(R.id.btn_cellularSave);
		btn_cellularCancel = (Button) findViewById(R.id.btn_cellularCancel);
		ed_document = (EditText) findViewById(R.id.ed_document);
		ed_cellular = (EditText) findViewById(R.id.ed_cellular);
		spn_select_lot.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				act_number.setText(null);
				act_number.setAdapter(null);
				if (ldto.size() > 0) {
					act_number.setAdapter(null);
					operation_select_event = true;
					act_number.setText(null);
					getConsultarSeriesLoterias(position);
					operation_select_event = true;
				} else {
					operation_select_event = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		act_number.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				act_number.setFocusable(true);
			}
		});

		act_number.setOnClickListener(this);

		act_number.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (s.length() > 4) {
					act_number.setInputType(0);
				} else {
					act_number.setInputType(InputType.TYPE_CLASS_NUMBER);
					act_number.setKeyListener(DigitsKeyListener
							.getInstance("0123456789"));
					act_number.setEnabled(true);
					act_number.setCursorVisible(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		act_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				act_number.setEnabled(true);
				if (s.length() == 4) {
					if (select_position_flage) {
						CommonMethods.showCustomToast(
								LotterySalesActivity.this,
								getResources().getString(
										R.string.select_operator_lottery));
						act_number.setText(null);
						// act_number.setInputType(InputType.TYPE_CLASS_NUMBER);
						// act_number.setKeyListener(DigitsKeyListener
						// .getInstance("0123456789"));
					} else {
						if (operation_select_event) {
							consultarSeriesLoterias();
							act_number.setEnabled(false);
						} else {

						}
					}

					if (s.length() >= 4) {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);
					} else {

					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	protected void getConsultarSeriesLoterias(int position) {
		if (position == 0) {
			select_position_flage = true;
		} else {
			select_position_flage = false;
			clDTO = (ConsultarLoteriasDTO) ldto.get(position);
		}
	}
	// Result of this method,calling ConsultarLoterias service to bind series
	private void consultarSeriesLoterias() {
		if (NetworkConnectivity.netWorkAvailability(LotterySalesActivity.this)) {
			type = true;
			new ConsultarSeriesLoterias(dto.getUserName(), dto.getPassword())
					.execute();
		} else {
			CommonMethods.showCustomToast(LotterySalesActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	private void loadUI() {

		btn_cellularSave.setOnClickListener(this);
		btn_cellularCancel.setOnClickListener(this);
		act_number.setOnClickListener(this);
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(15);
		ed_document.setFilters(FilterArray);
	}
	// Result of this method,clear activity list
	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.act_number:
			act_number.showDropDown();
			break;
		case R.id.btn_cellularSave:
			validateAndSave();

			break;
		case R.id.btn_cellularCancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);
			finish();
			break;

		default:
			break;
		}

	}
	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(LotterySalesActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else if (isValid) {
			LotterySalesDailog1 dialog = new LotterySalesDailog1(this, clDTO,
					cslDTO, edCellular, edDocument, NUMBER, numbers4);
			dialog.show();
			dialog.setCancelable(false);
			ed_cellular.setText(null);
			ed_document.setText(null);
			act_number.setText(null);
		}

	}
	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {
		isValid = true;

		actNumber = act_number.getText().toString().trim();
		edCellular = ed_cellular.getText().toString().trim();
		edDocument = ed_document.getText().toString().trim();

		if (select_position_flage) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources()
							.getString(R.string.select_operator_lottery));
		}

		if (actNumber.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_num));
		} else {

			for (DTO iterable_element : csldto) {
				ConsultarSeriesLoteriasDTO sdto = (ConsultarSeriesLoteriasDTO) iterable_element;
				if (sdto.getComparison_string().equals(actNumber)) {
					cslDTO = sdto;
				} else {

				}
			}
		}

		if (edCellular.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_celular));

		} else if (edCellular.length() == 10) {

		} else {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_cellular));
		}
		if (edDocument.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_document));

		}
		return isValid;
	}

	/* ConsultarSeriesLoterias */
	public class ConsultarSeriesLoterias extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json = getConsultarLoteriasJsonObj();
		private String encrypt_key;

		public ConsultarSeriesLoterias(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					LotterySalesActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(
								LotterySalesActivity.this,
								encrypt_key,
								ServiApplication.process_id_consultarSeriesLoterias,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
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
					csldto = new ParsingHandler()
							.getConsultarSeriesLoteriasDTO(
									new ParsingHandler().getString(
											new GetDocumentObject()
													.getDocumentObj(ht.responseDump),
											"response", "data"),
									act_number.getText().toString().trim(),
									getResources().getString(
											R.string.loteria_number),
									getResources().getString(
											R.string.loteria_serie),
									getResources().getString(
											R.string.loteria_fractions));
				} else {
					exception1 = true;
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
			List<String> values = new ArrayList<String>();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						LotterySalesActivity.this);
				dialog.show();
			} else if (exception1) {
				CommonMethods.showCustomToast(
						LotterySalesActivity.this,
						getResources().getString(
								R.string.slect_lotery_number_error));
			} else {
				try {
					if (csldto.size() > 0) {
						for (DTO iterable_element : csldto) {
							cslDTO = (ConsultarSeriesLoteriasDTO) iterable_element;
							values.add(getResources().getString(
									R.string.loteria_number)
									+ ":"
									+ act_number.getText().toString().trim()
									+ " "
									+ getResources().getString(
											R.string.loteria_serie)
									+ ":"
									+ cslDTO.getSerie()
									+ " "
									+ getResources().getString(
											R.string.loteria_fractions)
									+ ":"
									+ cslDTO.getFractions());
						}
					} else {
					}
				} catch (Exception e) {

				}
				NUMBER = act_number.getText().toString().trim();
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						LotterySalesActivity.this,
						R.layout.autocomplete_textview, values);
				act_number.setThreshold(1);
				act_number.setAdapter(adapter);
				act_number.setTextColor(Color.BLACK);
				act_number.showDropDown();
				// CommonMethods.hidden_softkey(LotterySalesActivity.this);
				// act_number.setFocusable(false);
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						0);
			}
		}

	}

	public class ConsultarLoterias extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getConsultarLoteriasJsonObj();
		private String encrypt_key, res_data;

		public ConsultarLoterias(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					LotterySalesActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(LotterySalesActivity.this, encrypt_key,
								ServiApplication.process_id_consultarLoterias,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
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
					ldto = new ParsingHandler().getConsultarLoterias(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"),
							LotterySalesActivity.this);
				} else {
					res_data = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
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
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						LotterySalesActivity.this);
				dialog.show();
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(res_data);
					CommonMethods.showCustomToast(LotterySalesActivity.this,
							json.getString("message"));
					moveMenuScreen();
				} catch (Exception e) {
				}
			} else {
				try {
					if (ldto.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(
								LotterySalesActivity.this,
								android.R.layout.simple_spinner_item,
								getProviders(ldto));
						providerListAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spn_select_lot.setAdapter(providerListAdapter);
					} else {
						spn_select_lot.setAdapter(null);
					}
				} catch (Exception e) {
				}
			}
		}
	}

	public String getConsultarLoteriasJsonObj() {
		JSONObject jsonobj = new JSONObject();
		try {
			if (type) {
				jsonobj.put("loteriaID", clDTO.getLoteID());
				jsonobj.put("sorteo", clDTO.getSorteo());
				jsonobj.put("number", act_number.getText().toString().trim());
				numbers4 = act_number.getText().toString().trim();

			}
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
	//  Result of this method navigate to the menu screen
	public void moveMenuScreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
	}

	public List<String> getProviders(List<DTO> ldto2) {

		List<String> list = new ArrayList<String>();
		for (DTO dto : ldto2) {
			ConsultarLoteriasDTO supplier = (ConsultarLoteriasDTO) dto;
			list.add(supplier.getLoteName());
			supplierTable.put(supplier.getLoteName(), supplier.getLoteID());
		}
		return list;
	}

}
