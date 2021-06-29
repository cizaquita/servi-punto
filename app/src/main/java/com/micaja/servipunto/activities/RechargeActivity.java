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

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.dialog.RechargeDialog1;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class RechargeActivity extends BaseActivity implements OnClickListener {

	ServiApplication appContext;
	private Intent intent;
	private Spinner spi_cel_id;
	private ArrayAdapter<String> providerListAdapter;
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	private RadioButton radio1, radio2, radio3, radio4, radio5, radio6, radio7,
			radio8, radio9, radio10, radio11;
	private EditText etxtRbtnAmount, etxtNumber;
	private Button btnCellularSave, btnCellularCancel;
	private String number, spncellular = null, productNumber,
			microwInsurenceAmount,microwInsurence_productId;
	private boolean isValid;
	private boolean value_flag = false, service_loan = false,
			loan_offer = false, operation_select_event = false;
	public List<DTO> dato = new ArrayList<DTO>();
	private List<DTO> microdto = new ArrayList<DTO>();
	CellularProviderDto cellular;

	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, RechargeActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		inItUI();
	}
	
	
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.recharge_activity);

		spi_cel_id = (Spinner) findViewById(R.id.spi_cel_id);

		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radio3 = (RadioButton) findViewById(R.id.radio3);
		radio4 = (RadioButton) findViewById(R.id.radio4);
		radio5 = (RadioButton) findViewById(R.id.radio5);
		radio6 = (RadioButton) findViewById(R.id.radio6);
		radio7 = (RadioButton) findViewById(R.id.radio7);
		radio8 = (RadioButton) findViewById(R.id.radio8);
		radio9 = (RadioButton) findViewById(R.id.radio9);
		radio10 = (RadioButton) findViewById(R.id.radio10);
		radio11 = (RadioButton) findViewById(R.id.radio11);
		radio1.setChecked(true);
		ServiApplication.setReg_amt((double) 1000);

		etxtRbtnAmount = (EditText) findViewById(R.id.etxt_rbtn_otheramount);
		etxtNumber = (EditText) findViewById(R.id.etxt_number);

		btnCellularSave = (Button) findViewById(R.id.btn_cellularSave);
		btnCellularCancel = (Button) findViewById(R.id.btn_cellularCancel);

		btnCellularSave.setOnClickListener(this);
		btnCellularCancel.setOnClickListener(this);

		spi_cel_id.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				bind_operatorvalues(position);
				if (position > 0) {
					spncellular = parent.getItemAtPosition(position).toString();
					operation_select_event = true;
					etxtNumber.setText("");
				} else {
					operation_select_event = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio1.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					ServiApplication.setReg_amt((double) 1000);
					etxtRbtnAmount.setVisibility(View.GONE);
					value_flag = false;

				}

			}
		});
		radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio2.setChecked(true);
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					ServiApplication.setReg_amt((double) 2000);
					value_flag = false;

				}

			}
		});
		radio3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio3.setChecked(true);
					radio2.setChecked(false);
					radio1.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					ServiApplication.setReg_amt((double) 3000);
					value_flag = false;

				}

			}
		});
		radio4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio4.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio1.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					ServiApplication.setReg_amt((double) 5000);
					value_flag = false;

				}

			}
		});
		radio5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio5.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio1.setChecked(false);
					radio6.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					ServiApplication.setReg_amt((double) 10000);
					value_flag = false;

				}

			}
		});
		radio6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio6.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio1.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					ServiApplication.setReg_amt((double) 15000);
					value_flag = false;

				}

			}
		});
		radio7.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio7.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio1.setChecked(false);
					radio10.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setText(null);
					etxtRbtnAmount.setVisibility(View.VISIBLE);
					etxtRbtnAmount.requestFocus();
					value_flag = true;

				}

			}
		});
		radio10.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio10.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio1.setChecked(false);
					radio7.setChecked(false);
					radio11.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					etxtRbtnAmount.requestFocus();
					value_flag = false;
					ServiApplication.setReg_amt((double) 20000);

				}

			}
		});
		radio11.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio11.setChecked(true);
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					radio1.setChecked(false);
					radio7.setChecked(false);
					radio10.setChecked(false);
					etxtRbtnAmount.setVisibility(View.GONE);
					etxtRbtnAmount.requestFocus();
					value_flag = false;
					ServiApplication.setReg_amt((double) 50000);
				}

			}
		});
		radio8.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio8.setChecked(true);
					radio9.setChecked(false);
					loan_offer = false;
					service_loan = true;
				}

			}
		});
		radio9.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					radio9.setChecked(true);
					radio8.setChecked(false);
					loan_offer = true;
					service_loan = false;

				}

			}
		});
		getoperatorsList();
	}
	// Result of this method,bind position as recharge operator
	public void bind_operatorvalues(int position) {
		etxtRbtnAmount.setText(null);
		if (position == 0) {
			spncellular = null;
			operation_select_event = false;
		} else {
			if (dato.size() > 0) {
				cellular = (CellularProviderDto) dato.get(position);
				spncellular = cellular.getProductName();
				productNumber = cellular.getProductNumber();
				if (cellular.getProductName().equalsIgnoreCase("CELLVOZIP")) {
					setContactNumberLength(true);
				} else if (cellular.getProductName()
						.equalsIgnoreCase("DIRECTV")) {
					setContactNumberLength(true);
				} else {
					setContactNumberLength(false);
				}
			}
		}

	}
	// Result of this method,get supplier list
	private List<String> getProviders(List<DTO> dato) {
		List<String> list = new ArrayList<String>();
		for (DTO dto : dato) {
			CellularProviderDto supplier = (CellularProviderDto) dto;
			list.add(supplier.getProductName());
			supplierTable.put(supplier.getProductNumber(),
					supplier.getProductName());
		}

		return list;
	}

	private void getoperatorsList() {

		if (NetworkConnectivity.netWorkAvailability(RechargeActivity.this)) {
			new GetOperators(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(RechargeActivity.this, getResources()
					.getString(R.string.no_wifi_adhoc));
		}

	}

	private String getJsonObj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("tipo", ServiApplication.Tipo_id_refills);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private String getJsonObjformicro() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("tipo", ServiApplication.Tipo_id_microinsurance);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cellularSave:
			validateAndSave();

			break;
		case R.id.btn_cellularCancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);

			break;

		default:
			break;
		}

	}
	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(RechargeActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else if (isValid) {
			if (service_loan) {
				UserDetailsDTO dto = UserDetailsDAO.getInstance()
						.getRecordsuser_name(
								DBHandler.getDBObj(Constants.READABLE),
								sharedpreferences.getString("user_name", ""));
				if (NetworkConnectivity
						.netWorkAvailability(RechargeActivity.this)) {
					new CallMicrowInsurence(dto.getUserName(),
							dto.getPassword()).execute();
				} else {
					CommonMethods.showCustomToast(RechargeActivity.this,
							getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				showRechargeDetailsDialog();
			}
		}
	}
	// Result of this method, it's move to Recharge dialog1
	private void showRechargeDetailsDialog() {
		appContext.pushActivity(intent);
		RechargeDialog1 dialog = new RechargeDialog1(this, spncellular, number,
				ServiApplication.getReg_amt(), service_loan, productNumber,
				microwInsurenceAmount, cellular,microwInsurence_productId);
		dialog.show();
		dialog.setCancelable(false);
	}
	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {
		number = etxtNumber.getText().toString().trim();
		isValid = true;

		if (null == spncellular) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_operator));
		}

		if (number.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_phonenum));

		} else {
			if (number.length() > 9) {
			} else {
				stringBuffer.append("\n"
						+ getResources()
								.getString(R.string.enter_validphonenum));
				isValid = false;
			}
		}
		if (value_flag) {
			if (etxtRbtnAmount.getText().toString().trim().length() == 0) {
				isValid = false;
				stringBuffer.append("\n"
						+ getResources().getString(R.string.enter_amount));
			} else {
				if (Double.parseDouble(etxtRbtnAmount.getText().toString()) > 1000
						&& Double.parseDouble(etxtRbtnAmount.getText()
								.toString()) <= 99999) {
					ServiApplication.setReg_amt(Double
							.parseDouble(etxtRbtnAmount.getText().toString()
									.trim()));
				} else {
					isValid = false;
					stringBuffer.append("\n"
							+ getResources().getString(
									R.string.enter_more_amount));
				}
			}
		}
//		if (loan_offer) {
//			isValid = false;
//			stringBuffer.append("\n"
//					+ getResources().getString(R.string.loan_servition_alert));
//		}
		return isValid;
	}
	// Result of this method,getting recharge operator from server
	public class GetOperators extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public GetOperators(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					RechargeActivity.this);
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
						.makeHeader(RechargeActivity.this, encrypt_key,
								ServiApplication.process_id_operators,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					dato = new ParsingHandler().getProvidersList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"), RechargeActivity.this,
							1);
				} else {

					responds = new ParsingHandler().getString(
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
						RechargeActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {

				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(RechargeActivity.this,
							json.getString("message"));
					moveMenuScreen();

				} catch (Exception e) {

				}

			} else {
				try {
					if (dato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(
								RechargeActivity.this,
								android.R.layout.simple_spinner_item,
								getProviders(dato));
						providerListAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spi_cel_id.setAdapter(providerListAdapter);
					}
				} catch (Exception e) {

				}
			}
		}
	}

	/* call microseguros */
	private class CallMicrowInsurence extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean flage = false, exception = false, exception1 = false;
		private String microwInsurenceAmount_value;
		private String json = getJsonObjformicro();
		private String encrypt_key, responds_data;

		CallMicrowInsurence(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					RechargeActivity.this);
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
								RechargeActivity.this,
								encrypt_key,
								ServiApplication.process_id_get_QueryrPaquetigos_Key,
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

					microdto = new ParsingHandler().getProvidersList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"), RechargeActivity.this,
							0);
					responds_data = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
				} else {
					exception1 = true;
					responds_data = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
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
						RechargeActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(responds_data);
					CommonMethods.showCustomToast(RechargeActivity.this,
							json.getString("message"));
				} catch (Exception e) {

				}
			} else {
				try {
					for (DTO mdto : microdto) {
						CellularProviderDto microinsurence = (CellularProviderDto) mdto;
						microwInsurenceAmount_value = microinsurence.getValor();
						microwInsurence_productId=microinsurence.getProductNumber();
						flage = true;
					}
					if (flage) {
						microwInsurenceAmount = microwInsurenceAmount_value;
						showRechargeDetailsDialog();
					} else {
						microwInsurenceAmount = "0";
						showRechargeDetailsDialog();
					}
				} catch (Exception e) {
					CommonMethods.dismissProgressDialog();
				}
			}

		}
	}

	/* edt text length */
	private void setContactNumberLength(boolean flage) {
		if (flage) {
			InputFilter maxLengthFilter = new InputFilter.LengthFilter(12);
			etxtNumber.setFilters(new InputFilter[] { maxLengthFilter });
		} else {
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(10);
			etxtNumber.setFilters(FilterArray);
		}
	}

	public void moveMenuScreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
	}
}
