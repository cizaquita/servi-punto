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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.ComisionDTO;
import com.micaja.servipunto.database.dto.ConsultarCiudadesDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.DepositoDaviplataDialog1;
import com.micaja.servipunto.dialog.DepositoDaviplataDialog2;
import com.micaja.servipunto.dialog.DepositoDaviplataDialog3;
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

public class DepositoDaviplataActivity extends BaseActivity implements
		OnClickListener {
	private Context context;
	private EditText document, celular, name, dereccion, apellidos, ciudad,
			confirm_deposito, number_daviplata, destino, valor_de_gir,
			cmision_del_giro, total_efectivo;
	private AutoCompleteTextView departameno;;
	private Button btn_consultar, btn_create, btn_edit, btn_confirm,
			btn_cancel;
	private Spinner spnDiposit;
	private SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	private Intent intent;

	private String document_number, descripcion;
	private boolean operation_select_event = false, type = false,
			select_position_flage = false, ConsultarCiudadesDTO_flage = false;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	private ArrayAdapter<String> dipositListAdapter;
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	DaviplataNumberDeDocumento documetn = new DaviplataNumberDeDocumento();
	private List<DTO> dato = new ArrayList<DTO>();
	ConsultarCiudadesDTO ccDTO = new ConsultarCiudadesDTO();
	private List<DTO> ccDTOList = new ArrayList<DTO>();
	private String Daviplata_Destino_number, Amount, f_document,
			spn_diposit = null, comision, Total_Amont;
	private boolean isValid;
	private String etxtNumberDocument, nuberDoc;

	public ComisionDTO Comision_DTO = new ComisionDTO();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, AceptacionActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.depositante_daviplata);
		ServiApplication.setDocumetn(documetn);
		document = (EditText) findViewById(R.id.etxt_number_document);
		celular = (EditText) findViewById(R.id.edtxt_celular);
		name = (EditText) findViewById(R.id.etxt_numberDeposit);
		dereccion = (EditText) findViewById(R.id.etxt_dereccion);
		apellidos = (EditText) findViewById(R.id.etxt_apellidos);
		ciudad = (EditText) findViewById(R.id.etxt_ciudad_munisipio);
		confirm_deposito = (EditText) findViewById(R.id.etxt_confirmDeposito);
		number_daviplata = (EditText) findViewById(R.id.etxt_number_daviplate);
		valor_de_gir = (EditText) findViewById(R.id.etxt_valor_de_gir);
		cmision_del_giro = (EditText) findViewById(R.id.etxt_cmision_del_giro);
		total_efectivo = (EditText) findViewById(R.id.etxt_total_efectivo);
		departameno = (AutoCompleteTextView) findViewById(R.id.departameno);
		spnDiposit = (Spinner) findViewById(R.id.spn_select_diposit);
		btn_consultar = (Button) findViewById(R.id.btn_consultar);
		btn_create = (Button) findViewById(R.id.btn_create);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		btn_confirm = (Button) findViewById(R.id.btn_payment_confirm);
		btn_cancel = (Button) findViewById(R.id.btn_payment_cancel);
		btn_consultar.setOnClickListener(this);
		btn_create.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_edit.setOnClickListener(this);
		departameno.setInputType(InputType.TYPE_CLASS_TEXT);

		spnDiposit.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 0) {
					spn_diposit = parent.getItemAtPosition(position).toString();
					operation_select_event = true;

					aedto = (AcceptEfectivoDTO) dato.get(position);
					spn_diposit = aedto.getIdCentroAcopio();
					// getConsultaCedula();
				} else {
					operation_select_event = false;
				}
				getDipositInfo(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		departameno.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() == 3) {
					descripcion = departameno.getText().toString().toString()
							.toUpperCase();
					consultarCiudades();
				} else if (s.length() < 3) {
					departameno.setAdapter(null);
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

		// departameno.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// consultarCiudadesDataBind();
		// }
		// });

		getconsultartipoDocumento();

		valor_de_gir.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					total_efectivo.setEnabled(true);
					cmision_del_giro.setEnabled(true);
				} else {
					consultarComision();
				}
			}
		});
		loadUI();
	}

	protected void consultarCiudadesDataBind(String string2) {
		for (DTO string : ccDTOList) {
			ConsultarCiudadesDTO getval = (ConsultarCiudadesDTO) string;
			if (string2.equalsIgnoreCase(getval.getDescripcion())) {
				this.ccDTO = getval;
				ConsultarCiudadesDTO_flage = true;
				break;
			}
		}
	}

	private void loadUI() {
		documetn = ServiApplication.getDocumetn();

		try {
			celular.setText(documetn.getCelular());
			name.setText(documetn.getNombres());
			dereccion.setText(documetn.getDireccion());
			apellidos.setText(documetn.getApellidos());
			ciudad.setText(documetn.getCiudad());
			document.setText(documetn.getDocumento());
		} catch (Exception e) {
		}

		celular.setEnabled(false);
		name.setEnabled(false);
		dereccion.setEnabled(false);
		apellidos.setEnabled(false);
		ciudad.setEnabled(false);
	}

	protected void consultarCiudades() {
		if (NetworkConnectivity
				.netWorkAvailability(DepositoDaviplataActivity.this)) {
			new ConsultarCiudades().execute();
		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	protected void consultarComision() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!consultarComision_validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
			// valor_de_gir.requestFocus();
			// valor_de_gir.setCursorVisible(true);
		} else {

			if (NetworkConnectivity
					.netWorkAvailability(DepositoDaviplataActivity.this)) {
				new VonsultarComision().execute();
			} else {
				CommonMethods.showCustomToast(this,
						getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	private boolean consultarComision_validateFields(StringBuffer stringBuffer) {
		isValid = true;
		Amount = valor_de_gir.getText().toString();
		// 1. spn_diposit
		if (operation_select_event) {
		} else {
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_tipo_docu));
			isValid = false;
		}
		if (document.getText().toString().length() > 0) {
			f_document = document.getText().toString();
		} else {
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_docum));
			isValid = false;
		}

		if (number_daviplata.getText().toString().length() > 0) {

			if (confirm_deposito.getText().toString().length() > 0) {

				if (confirm_deposito.getText().toString().toString()
						.equals(number_daviplata.getText().toString())) {
					Daviplata_Destino_number = number_daviplata.getText()
							.toString();
				} else {
					stringBuffer.append("\n"
							+ getResources().getString(
									R.string.wrong_cnfm_daviplata));
					isValid = false;
				}

			} else {
				stringBuffer.append("\n"
						+ getResources().getString(
								R.string.enter_cnfm_daviplata));
				isValid = false;
			}

		} else {
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_daviplata));
			isValid = false;
		}

		return isValid;
	}

	private void getconsultartipoDocumento() {
		if (NetworkConnectivity
				.netWorkAvailability(DepositoDaviplataActivity.this)) {
			new ConsultarTipoDocumento(dto.getUserName(), dto.getPassword())
					.execute();
		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	private void getConsultaCedula() {
		if (NetworkConnectivity
				.netWorkAvailability(DepositoDaviplataActivity.this)) {
			new ConsultaCedula(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	protected void getDipositInfo(int position) {
		if (position == 0) {
			spn_diposit = null;
			select_position_flage = true;
		} else {
			if (dato.size() > 0) {
				select_position_flage = false;
				aedto = (AcceptEfectivoDTO) dato.get(position);
				spn_diposit = aedto.getIdCentroAcopio();
			}
		}
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 4516) {
				loadUI();
			}

		}
	};

	// spinner consultar Tipo Documento

	public class ConsultarTipoDocumento extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, json_responds;

		public ConsultarTipoDocumento(String email, String password) {
			mEmail = password;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					DepositoDaviplataActivity.this);
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
								DepositoDaviplataActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsultarTipoDocumento,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();

				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					dato = new ParsingHandler().getTipoDiposts(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"),
							DepositoDaviplataActivity.this);

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
		protected void onPostExecute(Boolean result) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						DepositoDaviplataActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(
							DepositoDaviplataActivity.this,
							json.getString("message"));
					moveMenuScreen();
				} catch (Exception e) {
				}
			} else {
				try {
					if (dato.size() > 0) {
						dipositListAdapter = new ArrayAdapter<String>(
								DepositoDaviplataActivity.this,
								android.R.layout.simple_spinner_item,
								getProviders(dato));
						dipositListAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spnDiposit.setAdapter(dipositListAdapter);
					} else {
						spnDiposit.setAdapter(null);
					}
				} catch (Exception e) {

				}
			}

		}
	}

	private List<String> getProviders(List<DTO> ldto2) {
		List<String> list = new ArrayList<String>();
		for (DTO dto : ldto2) {
			AcceptEfectivoDTO supplier = (AcceptEfectivoDTO) dto;
			list.add(supplier.getDistribuidor());
			supplierTable.put(supplier.getDistribuidor(),
					supplier.getIdCentroAcopio());
		}
		return list;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_edit:
			DepositoDaviplataDialog1 dialog = new DepositoDaviplataDialog1(
					this, documetn, uiHandler);
			dialog.show();
			break;
		case R.id.btn_payment_cancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);
			finish();
			break;
		case R.id.btn_create:
			DepositoDaviplataDialog2 dialog1 = new DepositoDaviplataDialog2(
					this, documetn, uiHandler);
			dialog1.show();
			break;
		case R.id.btn_payment_confirm:
			cnfrmBtnValidations();
			break;
		case R.id.btn_consultar:
			validation_document();
			break;
		default:
			break;
		}
	}

	private void cnfrmBtnValidations() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!cnfrmBtn_validateFields(stringBuffer)) {
			CommonMethods.displayAlert(true,
					getResources().getString(R.string.fail),
					stringBuffer.toString(),
					getResources().getString(R.string.ok), "",
					DepositoDaviplataActivity.this, null, false);
		} else {
			DepositoDaviplataDialog3 dialog = new DepositoDaviplataDialog3(
					DepositoDaviplataActivity.this, f_document, documetn,
					ccDTO, number_daviplata.getText().toString(), Comision_DTO,
					aedto);
			dialog.show();
			dialog.setCancelable(false);
		}

	}

	private boolean cnfrmBtn_validateFields(StringBuffer stringBuffer) {
		consultarCiudadesDataBind(departameno.getText().toString());
		nuberDoc = documetn.getCelular();
		isValid = true;

		if (null == spn_diposit) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_tipo_docu));
		}
		if (document.getText().toString().length() == 0) {
			stringBuffer.append("\n "
					+ getResources().getString(R.string.enter_docum));
			isValid = false;
		}
		if (number_daviplata.getText().toString().length() > 0) {

			if (confirm_deposito.getText().toString().length() > 0) {

				if (confirm_deposito.getText().toString().toString()
						.equals(number_daviplata.getText().toString())) {
					Daviplata_Destino_number = number_daviplata.getText()
							.toString();
				} else {
					stringBuffer.append("\n"
							+ getResources().getString(
									R.string.wrong_cnfm_daviplata));
					isValid = false;
				}

			} else {
				stringBuffer.append("\n"
						+ getResources().getString(
								R.string.enter_cnfm_daviplata));
				isValid = false;
			}

		} else {
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_daviplata));
			isValid = false;
		}

		if (valor_de_gir.getText().toString().length() > 0) {
			Amount = valor_de_gir.getText().toString();
			if (cmision_del_giro.getText().toString().length() > 0) {
				if (total_efectivo.getText().toString().length() > 0) {

				} else {
					stringBuffer.append("\n"
							+ getResources().getString(
									R.string.please_enter_amount));
					isValid = false;
				}

			} else {
				stringBuffer.append("\n"
						+ getResources()
								.getString(R.string.please_enter_amount));
				isValid = false;
			}
		} else {
			stringBuffer.append("\n"
					+ getResources().getString(R.string.please_enter_amount));
			isValid = false;

		}

		if (ConsultarCiudadesDTO_flage) {
		} else {
			stringBuffer.append("\n Please enter departament");
			isValid = false;
		}
		if (nuberDoc == null || nuberDoc.trim().equals("null")
				|| nuberDoc.trim().length() <= 0) {
			isValid = false;
			stringBuffer.append("\n Please enter Celular");

		}

		return isValid;
	}

	private void validation_document() {
		document_number = document.getText().toString().trim();
		if (document_number.length() == 0) {
			CommonMethods.showCustomToast(DepositoDaviplataActivity.this,
					getResources().getString(R.string.oops_errmsg));
		} else {
			getConsultaCedula();
		}
	}

	public void moveMenuScreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
	}

	public String getJsonObj() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());

			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();

	}

	public class ConsultaCedula extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObjCedula();
		private String encrypt_key, json_responds;

		public ConsultaCedula(String email, String password) {
			mEmail = password;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					DepositoDaviplataActivity.this);
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
						.makeHeader(DepositoDaviplataActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsultaCedula,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();

				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					json_responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");

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
		protected void onPostExecute(Boolean result) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						DepositoDaviplataActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(
							DepositoDaviplataActivity.this,
							json.getString("message"));
					documetn = new ParsingHandler().getDaviDocument(
							json_responds, document_number);
					celular.setText(documetn.getCelular());
					name.setText(documetn.getNombres());
					dereccion.setText(documetn.getDireccion());
					apellidos.setText(documetn.getApellidos());
					ciudad.setText(documetn.getCiudad());
					btn_create.setEnabled(true);
				} catch (Exception e) {

				}
			} else {
				try {
					documetn = new ParsingHandler().getDaviDocument(
							json_responds, document_number);
					ServiApplication.setDocumetn(documetn);
					loadUI();
					// DepositoDaviplataDialog1 dialog = new
					// DepositoDaviplataDialog1(DepositoDaviplataActivity.this,
					// documetn,uiHandler);
					// dialog.show();
					btn_create.setEnabled(false);
				} catch (Exception e) {

				}
			}
		}
	}

	public String getJsonObjCedula() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("cedula", document_number);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private class ConsultarCiudades extends AsyncTask<Void, Void, Void> {
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObjConsultarCiudades();
		private String encrypt_key, json_responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					DepositoDaviplataActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
		}

		private String getJsonObjConsultarCiudades() {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId());
				jsonobj.put("terminalId", dto.getTerminalId());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
				jsonobj.put("ciudadId", documetn.getCiudad());
				jsonobj.put("ciudadId", "");
				jsonobj.put("descripcion", descripcion);
				return jsonobj.toString();
			} catch (Exception e) {
			}
			return jsonobj.toString();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(DepositoDaviplataActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsulterCiudades,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					json_responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
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
			}
			return null;
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
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						DepositoDaviplataActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(
							DepositoDaviplataActivity.this,
							json.getString("message"));
				} catch (Exception e) {
				}
			} else {
				ccDTOList = new ParsingHandler()
						.ConsultarCiudadesdataget(json_responds);
				ArrayList<String> value = new ArrayList<String>();
				for (DTO string : ccDTOList) {
					ConsultarCiudadesDTO getval = (ConsultarCiudadesDTO) string;
					value.add(getval.getDescripcion());
				}
				@SuppressWarnings("unchecked")
				ArrayAdapter adapter = new ArrayAdapter(
						DepositoDaviplataActivity.this,
						android.R.layout.simple_list_item_1, value);
				departameno.setAdapter(adapter);
				departameno.showDropDown();
			}
		}
	}

	// Call to VonsultarComision
	public class VonsultarComision extends AsyncTask<Void, Void, Boolean> {
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObjforVonsultar();
		private String encrypt_key, json_responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					DepositoDaviplataActivity.this);
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
						.makeHeader(DepositoDaviplataActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsultarComision,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();

				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					json_responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");

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
		protected void onPostExecute(Boolean result) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						DepositoDaviplataActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(
							DepositoDaviplataActivity.this,
							json.getString("message"));

				} catch (Exception e) {
				}
			} else {
				try {
					JSONObject json = new JSONObject(json_responds);
					comision = "" + json.getLong("comision");
					cmision_del_giro.setText(comision);
					Total_Amont = ""
							+ (Long.parseLong(Amount) + Long
									.parseLong(comision));
					total_efectivo.setText("" + Total_Amont);
					total_efectivo.setEnabled(false);
					cmision_del_giro.setEnabled(false);

					Comision_DTO.setCodigoconvenio(json
							.getString("codigoconvenio"));
					Comision_DTO.setComision("" + json.getLong("comision"));
					Comision_DTO.setDocumento(json.getString("documento"));
					Comision_DTO.setNombre(json.getString("nombre"));
					Comision_DTO.setTipodocumento(json
							.getString("tipodocumento"));
					Comision_DTO.setTotal("" + Total_Amont);
					Comision_DTO.setValue(Amount);
				} catch (Exception e) {

				}
			}

		}
	}

	public String getJsonObjforVonsultar() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("typeDocument", spn_diposit);
			jsonobj.put("document", f_document);
			jsonobj.put("number", Daviplata_Destino_number);
			jsonobj.put("amount", "" + (Long.parseLong(Amount)));// Based on
																	// mauricio
																	// request i
																	// removed
																	// multiple
																	// bye 100
																	// functinality
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
