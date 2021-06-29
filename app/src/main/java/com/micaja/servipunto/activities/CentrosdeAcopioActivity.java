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
import android.os.Handler;
import android.os.Message;
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
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.CentrosdeAcopioDialog1;
import com.micaja.servipunto.dialog.Efectivo_MenuDialog;
import com.micaja.servipunto.dialog.OnDialogSaveClickListener2;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;

public class CentrosdeAcopioActivity extends BaseActivity implements
		OnClickListener, OnDialogSaveClickListener2 {
	ServiApplication appContext;
	private EditText ed_name_collection_center;
	private Spinner spn_storage_center;
	private Button btn_collectionCancel, btn_collectionsearch;
	private String collection_center;
	private boolean isValid;
	public List<DTO> collection_data;
	private ArrayAdapter<String> acopioListAdapter;
	private List<DTO> dato = new ArrayList<DTO>();
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	private SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;
	private String spnstorage = null;
	private boolean operation_select_event = false, type = false,
			select_position_flage = false;
	private String clave, value, idCentroAcopio;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, CentrosdeAcopioActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		InitUi();
	}

	private void InitUi() {
		setContentView(R.layout.centros_acopio_activity);
		spn_storage_center = (Spinner) findViewById(R.id.spn_storage_center);
		ed_name_collection_center = (EditText) findViewById(R.id.ed_name_collection_center);
		btn_collectionCancel = (Button) findViewById(R.id.btn_collectionCancel);
		btn_collectionsearch = (Button) findViewById(R.id.btn_collectionsearch);
		btn_collectionCancel.setOnClickListener(this);
		btn_collectionsearch.setOnClickListener(this);
		spn_storage_center
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (position > 0) {
							spnstorage = parent.getItemAtPosition(position)
									.toString();
							operation_select_event = true;
						} else {
							operation_select_event = false;
						}
						getStorageInfo(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		
		getConsultarCentrosAcopios();
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 142536) {
				InitUi();
			}
		}
	};

	private void getConsultarCentrosAcopios() {
		new ConsultarCentrosAcopio(dto.getUserName(), dto.getPassword())
				.execute();
	}

	protected void getStorageInfo(int position) {
		if (position == 0) {
			spnstorage = null;
			select_position_flage = true;
		} else {
			if (dato.size() > 0) {
				select_position_flage = false;
				aedto = (AcceptEfectivoDTO) dato.get(position);
				ed_name_collection_center.setText(aedto.getIdCentroAcopio());
				ed_name_collection_center.setEnabled(false);
			}

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
		case R.id.btn_collectionsearch:
			validateAndSave();
			break;
		case R.id.btn_collectionCancel:
			backButtonHandler();
			finish();
			break;
		default:
			break;
		}
	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(CentrosdeAcopioActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			CentrosdeAcopioDialog1 dialog = new CentrosdeAcopioDialog1(this,
					aedto, uiHandler);
			dialog.show();
			dialog.setCancelable(false);
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
		backButtonHandler();
		return;
	}

	private void backButtonHandler() {
		Efectivo_MenuDialog dialog = new Efectivo_MenuDialog(this);
		dialog.show();
	}

	private boolean validateFields(StringBuffer stringBuffer) {
		collection_center = ed_name_collection_center.getText().toString();
		isValid = true;
		if (collection_center.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.collection_id));
		}
//		if (null == spnstorage) {
//			isValid = false;
//			stringBuffer
//					.append("\n"
//							+ getResources().getString(
//									R.string.select_operator_acopio));
//		}
		
		if (ed_name_collection_center.getText().toString().length()>0) {
			boolean flage=false;
			for (DTO i : dato) {
				aedto=(AcceptEfectivoDTO) i;
				if (ed_name_collection_center.getText().toString().trim().equals(aedto.getIdCentroAcopio())) {
					flage=true;
					break;
				}else{
				}
			}
			
			if (flage) {
				
			}else{
				isValid = false;
				stringBuffer
						.append("\n"
								+ getResources().getString(
										R.string.select_operator_acopio));
			}
		}
		return isValid;
	}

	private List<String> getTipoDocuments(List<DTO> ldto2) {
		List<String> list = new ArrayList<String>();
		for (DTO dto : ldto2) {
			AcceptEfectivoDTO supplier = (AcceptEfectivoDTO) dto;
			list.add(supplier.getDistribuidor());
			supplierTable.put(supplier.getDistribuidor(),
					supplier.getIdCentroAcopio());
		}
		return list;
	}

	public class ConsultarCentrosAcopio extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, json_responds;

		public ConsultarCentrosAcopio(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					CentrosdeAcopioActivity.this);
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
								CentrosdeAcopioActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsultarCentrosAcopio,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("varahalababu", ht.responseDump);
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					dato = new ParsingHandler().getConsultarCentrosAcopioList(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"),
							CentrosdeAcopioActivity.this);
				} else {
					json_responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
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
						CentrosdeAcopioActivity.this);
				dialog.show();
				dialog.setCancelable(false);
				finish();
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(CentrosdeAcopioActivity.this,
							json.getString("message"));
					moveMenuScreen();
				} catch (Exception e) {
				}
			} else {
				try {
					if (dato.size() > 0) {
						acopioListAdapter = new ArrayAdapter<String>(
								CentrosdeAcopioActivity.this,
								android.R.layout.simple_spinner_item,
								getTipoDocuments(dato));
						acopioListAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spn_storage_center.setAdapter(acopioListAdapter);
					} else {
						spn_storage_center.setAdapter(null);
					}
				} catch (Exception e) {

				}
			}

		}

	}

	public void moveMenuScreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
	}

	private String getJsonObj() {
		JSONObject jsonobj = new JSONObject();
		try {
			if (type) {
				jsonobj.put("idCentroAcopio", aedto.getIdCentroAcopio());
				jsonobj.put("valor", aedto.getValor());
				jsonobj.put("clave", aedto.getClave());
			}
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	@Override
	public void onSaveClick() {
		appContext.setScreen(null);
		CommonMethods.startIntent(CentrosdeAcopioActivity.this,
				ProductsActivity.class);
		finish();
		appContext.popActivity();
	}

	@Override
	public void onCancelClick() {

	}
}
