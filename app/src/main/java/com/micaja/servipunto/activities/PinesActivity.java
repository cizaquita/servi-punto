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
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.dialog.PinesDailog1;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class PinesActivity extends Activity implements OnClickListener {

	ServiApplication appContext;
	private Spinner spn_select_provider, spn_select_product;
	Button btn_cellularSave, btn_cellularCancel;
	EditText ed_cellular;
	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	public List<DTO> dato = new ArrayList<DTO>();
	public List<DTO> pdato = new ArrayList<DTO>();
	private boolean isValid;
	private String cellular_nUmber;
	private Intent intent;
	private ArrayAdapter<String> providerListAdapter;
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	private Hashtable<String, String> p_supplierTable = new Hashtable<String, String>();
	CellularProviderDto cellular;
	CellularProviderDto p_cellular;
	CellularProviderDto PinesProvides;
	CellularProviderDto PinesProducts;
	private boolean selecte_providerflage = false,
			selecte_productflage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, PinesActivity.class);
		setContentView(R.layout.pines);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
		addListners();
		consultaPinesProveedores();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ed_cellular.setText(null);
	}
	// Result of this method,click listener
	private void addListners() {

		btn_cellularSave.setOnClickListener(this);
		btn_cellularCancel.setOnClickListener(this);
	}
	// Result of this method,registration for all UI views.
	private void initUI() {

		spn_select_provider = (Spinner) findViewById(R.id.spn_select_provider);
		spn_select_product = (Spinner) findViewById(R.id.spn_select_product);
		btn_cellularSave = (Button) findViewById(R.id.btn_cellularSave);
		btn_cellularCancel = (Button) findViewById(R.id.btn_cellularCancel);
		ed_cellular = (EditText) findViewById(R.id.ed_cellular);
		spn_select_product
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						if (pdato.size() > 0) {
							getTheproviders(position);
						} else {

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		spn_select_provider
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						bind_providers(0);
						if (dato.size() > 0) {
							bind_operatorvalues(position);
							ed_cellular.setText("");
						} else {
							selecte_providerflage = true;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

	}

	protected void getTheproviders(int position) {
		if (position == 0) {
			selecte_productflage = false;
		} else {
			selecte_productflage = true;
			cellular = (CellularProviderDto) pdato.get(position);
			PinesProducts = cellular;
		}
	}

	protected void bind_operatorvalues(int position) {
		if (position == 0) {
			selecte_providerflage = false;
		} else {
			selecte_providerflage = true;
			cellular = (CellularProviderDto) dato.get(position);
			PinesProvides = cellular;
			consultaPines();
		}
	}

	protected void bind_providers(int position) {
		Log.v("varahalababu", "varahalababu" + "bind_providers");
		if (pdato.size() > 0) {
			// cellular = (CellularProviderDto) pdato.get(0);
			// PinesProducts = cellular;

			providerListAdapter = new ArrayAdapter<String>(PinesActivity.this,
					android.R.layout.simple_spinner_item, getProviders(pdato));
			providerListAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spn_select_product.setAdapter(providerListAdapter);

			Log.v("varahalababu", "varahalababu" + "if");

		} else {
			Log.v("varahalababu", "varahalababu" + "else");
		}
	}

	private void consultaPines() {
		if (NetworkConnectivity.netWorkAvailability(PinesActivity.this)) {
			new ConsultaPines(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(PinesActivity.this, getResources()
					.getString(R.string.no_wifi_adhoc));
		}

	}

	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cellularSave:
			ValidAndSave();
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
	private void ValidAndSave() {
		StringBuffer stringBuffer = new StringBuffer();

		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(PinesActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			PinesDailog1 dialog = new PinesDailog1(this, PinesProvides,
					PinesProducts, ed_cellular.getText().toString().trim());
			dialog.show();
			dialog.setCancelable(false);
		}

	}
	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {
		cellular_nUmber = ed_cellular.getText().toString().trim();
		isValid = true;
		if (selecte_providerflage) {

		} else {
			isValid = false;
			stringBuffer
					.append("\n"
							+ getResources().getString(
									R.string.select_operator_option));

		}

		if (selecte_productflage) {

		} else {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_pin_product));

		}
		if (cellular_nUmber.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_valid_cell));
		} else {
			if (cellular_nUmber.length() > 9) {
			} else {
				stringBuffer.append("\n"
						+ getResources().getString(
								R.string.pines_invalidcellnumeber));
				isValid = false;
			}

		}
		return isValid;
	}

	// ConsultaPinesProveedores
	private void consultaPinesProveedores() {

		if (NetworkConnectivity.netWorkAvailability(PinesActivity.this)) {
			new ConsultaPinesProveedores(dto.getUserName(), dto.getPassword())
					.execute();
		} else {
			CommonMethods.showCustomToast(PinesActivity.this, getResources()
					.getString(R.string.no_wifi_adhoc));
		}

	}

	private String getConsultaPinesProveedoresJsonObj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("tipo",
					ServiApplication.Tipo_id_ConsultaPinesProveedores);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public class ConsultaPinesProveedores extends
			AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false;
		String json = getConsultaPinesProveedoresJsonObj();
		private String encrypt_key;

		public ConsultaPinesProveedores(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					PinesActivity.this);
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
						.makeHeader(PinesActivity.this, encrypt_key,
								ServiApplication.process_id_operators,
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
					dato = new ParsingHandler().getPinesProvidersList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"), true,
							PinesActivity.this);
				} else {
					exception = true;
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
				OopsErrorDialog dialog = new OopsErrorDialog(PinesActivity.this);
				dialog.show();
			} else {
				try {
					if (dato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(
								PinesActivity.this,
								android.R.layout.simple_spinner_item,
								getProviders(dato));
						providerListAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spn_select_provider.setAdapter(providerListAdapter);
					}
				} catch (Exception e) {

				}
			}
		}
	}

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
	// This method using for passing request to the getConsultaPinesProveedoresJsonObj
	private String getConsultaPinesJsonObj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("providerId", cellular.getProductNumber());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public class ConsultaPines extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json = getConsultaPinesJsonObj();
		private String encrypt_key, res_json;

		public ConsultaPines(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					PinesActivity.this);
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
						.makeHeader(PinesActivity.this, encrypt_key,
								ServiApplication.process_id_consultarPines,
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
					pdato = new ParsingHandler().getPinesProvidersList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"), false,
							PinesActivity.this);
				} else {
					res_json = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					pdato = new ParsingHandler().getPinesProvidersList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"), false,
							PinesActivity.this);
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
				OopsErrorDialog dialog = new OopsErrorDialog(PinesActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(res_json);
					CommonMethods.showCustomToast(PinesActivity.this,
							json.getString("message"));
				} catch (Exception e) {
				}
			} else {
				try {
					if (pdato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(
								PinesActivity.this,
								android.R.layout.simple_spinner_item,
								getProviders(pdato));
						providerListAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spn_select_product.setAdapter(providerListAdapter);
						bind_providers(0);
					}
				} catch (Exception e) {

				}
			}
		}
	}
}
