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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.dialog.PaguetigoDetailsDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;

public class PaquetigosActivity extends BaseActivity implements OnClickListener {
	private SharedPreferences sharedpreferences;
	ServiApplication appContext;
	private Intent intent;
	private ArrayAdapter<String> providerListAdapter;
	private Spinner spi_cel_id;
	private String number, spncellular = null;
	private boolean isValid;
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	private EditText etxt_DebtAmount;
	private Button btn_EndSaleSave, btn_EndSaleCancel;
	private boolean service_loan = false, opration_select_event = false;
	private List<DTO> dato = new ArrayList<DTO>();
	public CellularProviderDto cellular;
	UserDetailsDTO dto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, PaquetigosActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));

		inItUI();
	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.paquetigos_activity);
		spi_cel_id = (Spinner) findViewById(R.id.spi_cel_id);
		etxt_DebtAmount = (EditText) findViewById(R.id.etxt_DebtAmount_portugos);
		btn_EndSaleSave = (Button) findViewById(R.id.btn_EndSaleSave);
		btn_EndSaleCancel = (Button) findViewById(R.id.btn_EndSaleCancel);
		btn_EndSaleCancel.setOnClickListener(this);
		btn_EndSaleSave.setOnClickListener(this);
		spi_cel_id.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 0) {
					spncellular = parent.getItemAtPosition(position).toString();
					opration_select_event = true;
					etxt_DebtAmount.setText("");
				} else {
					opration_select_event = false;
				}
				getPaquetigosInfo(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		getoperatorsList();
		setContactNumberLength();
	}

	public void getPaquetigosInfo(int position) {
		if (position == 0) {
			spncellular = null;
		} else {

			if (dato.size() > 0) {
				cellular = (CellularProviderDto) dato.get(position);
				spncellular = cellular.getProductName();

			}

		}
	}

	private void getoperatorsList() {

		new UserLoginTask(dto.getUserName(), dto.getPassword()).execute();
	}
	// Result of this method,getting supplier
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
	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(PaquetigosActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else if (isValid) {
			if (dato.size() > 0) {
				appContext.pushActivity(intent);
				PaguetigoDetailsDialog dialog = new PaguetigoDetailsDialog(
						PaquetigosActivity.this, spncellular, number,
						ServiApplication.getReg_amt(), service_loan, cellular);
				dialog.show();
				dialog.setCancelable(false);
			} else {

			}
		}

	}
	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {
		number = etxt_DebtAmount.getText().toString().trim();
		isValid = true;
		if (null == spncellular) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(
							R.string.select_operator_paquetigos));
		}
		if (number.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_validphonenum));
		} else {
			if (number.length() > 9) {
			} else {
				stringBuffer.append("\n"
						+ getResources()
								.getString(R.string.enter_validphonenum));
				isValid = false;
			}
		}
		return isValid;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_EndSaleSave:
			validateAndSave();
			break;
		case R.id.btn_EndSaleCancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);

			break;
		default:
			break;
		}
	}
	// Result of this method,Getting Paqutigos Id's
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, json_responds;

		UserLoginTask(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					PaquetigosActivity.this);
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
								PaquetigosActivity.this,
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
					dato = new ParsingHandler().getProvidersList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"),
							PaquetigosActivity.this, 2);
				} else {
					json_responds = new ParsingHandler().getString(
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
						PaquetigosActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(PaquetigosActivity.this,
							json.getString("message"));
					moveMenuScreen();
				} catch (Exception e) {

				}
			} else {
				try {
					if (dato.size() > 0) {
						providerListAdapter = new ArrayAdapter<String>(
								PaquetigosActivity.this,
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
	// This method using for paquetigos data passing  to the json objects
	private String getJsonObj() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("tipo", ServiApplication.Tipo_id_Paquetigos);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
	// Result of this method,navigate menu screen
	public void moveMenuScreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
	}

	private void setContactNumberLength() {
		InputFilter maxLengthFilter = new InputFilter.LengthFilter(10);
		etxt_DebtAmount.setFilters(new InputFilter[] { maxLengthFilter });

	}
}