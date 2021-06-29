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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ConsultaConvenioDTO;
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

public class ConventionSecondCnfmDetailsDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {
	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;
	private Intent intent;
	private TextView txt_operator, txt_number, txt_reg_val, txt_ass_val,
			txt_keyvalue, editTxtkey, editTxtcellnumber, txtDialogTitle;
	private EditText etxtclavee, cellNumber;

	private TextView txtPaquetigo, txtNumber, regValue, asstValue, txtKey,
			txtCelular;

	private LinearLayout layoutKey, layoutEditkey, layoutAssistance1,
			layoutCellNo, layoutCelular;
	private ImageView imgClose;
	private Button btnRegcancel, btnRegcnfm;
	private boolean isValid;
	private String valueKey;
	private String cnfmValue, cnfmValue1, cellPhoneno;
	private ConsultaConvenioDTO consultaConvenioDTO;
	public SharedPreferences sharedpreferences;
	private UserDetailsDTO dto;

	private TextView convenio, cedula_nit, cedula_pago, valor, feach,
			num_cedular;// if bind data use this textviews only

	public ConventionSecondCnfmDetailsDialog(Context context,
			ConsultaConvenioDTO consultaConvenioDTO, String cnfmValue,
			String cellPhoneno) {
		super(context);
		this.context = context;
		this.cnfmValue = cnfmValue ;
		this.cellPhoneno = cellPhoneno;
		this.consultaConvenioDTO = consultaConvenioDTO;
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		this.cnfmValue1 = cnfmValue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ininUI();

	}

	private void ininUI() {
		setContentView(R.layout.recharge_dialog1);

		txtPaquetigo = (TextView) findViewById(R.id.txt_paquetigo);
		txtPaquetigo.setText(getContext().getString(R.string.converaion));

		txtNumber = (TextView) findViewById(R.id.txtnumber);
		txtNumber.setText(getContext().getString(R.string.refer1));
		regValue = (TextView) findViewById(R.id.regvalue);
		regValue.setText(getContext().getString(R.string.fecha));

		asstValue = (TextView) findViewById(R.id.asstvalue);
		asstValue.setText(getContext().getString(R.string.report_value));

		layoutKey = (LinearLayout) findViewById(R.id.layout_key);
		layoutKey.setVisibility(View.VISIBLE);
		txtKey = (TextView) findViewById(R.id.txtkey);
		txtKey.setText(getContext().getString(R.string.fecha));

		layoutCelular = (LinearLayout) findViewById(R.id.layout_key);
		layoutCelular.setVisibility(View.VISIBLE);

		txtCelular = (TextView) findViewById(R.id.txtcelular);
		txtKey.setText(getContext().getString(R.string.numero_celular));

		editTxtkey = (TextView) findViewById(R.id.edit_txtkey);
		editTxtkey.setText(getContext().getString(R.string.key));

		etxtclavee = (EditText) findViewById(R.id.etxt_key_value);

		btnRegcancel = (Button) findViewById(R.id.btn_reg_cancel);
		btnRegcnfm = (Button) findViewById(R.id.btn_reg_cnfm);
		btnRegcnfm.setOnClickListener(this);
		btnRegcancel.setOnClickListener(this);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);

		// binding these textviews id's
		convenio = (TextView) findViewById(R.id.txt_operator);
		cedula_nit = (TextView) findViewById(R.id.txt_number);
		cedula_pago = (TextView) findViewById(R.id.txt_reg_val);
		valor = (TextView) findViewById(R.id.txt_ass_val);
		feach = (TextView) findViewById(R.id.txt_keyvalue);
		num_cedular = (TextView) findViewById(R.id.txt_keyvalue);

		txtDialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtDialogTitle.setText(getContext()
				.getString(R.string.convenio_dialog2));

		loadUI();

	}

	private void loadUI() {
		convenio.setText(consultaConvenioDTO.getDescripcion());
		cedula_nit.setText(consultaConvenioDTO.getRef1());
		cedula_pago.setText(Dates.getMyCashBoxFechaforbar(consultaConvenioDTO
				.getDuedate()));
		feach.setText(consultaConvenioDTO.getDuedate());
		Log.v("varahalababu", "varahalababu" + consultaConvenioDTO.getFecha());
		num_cedular.setText(cellPhoneno);
		valor.setText("$  " + (new Double(cnfmValue1)).longValue());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			this.dismiss();

			break;

		case R.id.btn_reg_cnfm:
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
			this.dismiss();
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new EjemploConsultaConvenio(dto.getUserName(),
						dto.getPassword()).execute();

			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}

	}

	private void moveTosuccessPage(String jsonformate) {
		ConventionTransactionResultsDialog dialog = new ConventionTransactionResultsDialog(
				context, jsonformate, consultaConvenioDTO, cnfmValue);
		dialog.show();
		dialog.setCancelable(false);
	}

	private boolean validateFields(StringBuffer stringBuffer) {

		valueKey = etxtclavee.getText().toString().trim();
		isValid = true;

		if (valueKey.length() == 0) {
			isValid = false;
			stringBuffer.append(""
					+ getContext().getString(R.string.Please_enter_key));
		} else {
			if (valueKey.length() >= 6) {
				isValid = true;
			} else {
				stringBuffer.append("\n"
						+ getContext().getString(R.string.invalid_lotkey));
				isValid = false;
			}
		}

		return isValid;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	public class EjemploConsultaConvenio extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json;
		private String encrypt_key, jsonformate;

		public EjemploConsultaConvenio(String email, String password) {
			mEmail = email;
			mPassword = password;
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
			Log.v("varahalababu", "==============="+getJsonObj());
			json = AESTEST.AESCrypt(getJsonObj(), key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(
								context,
								encrypt_key,
								ServiApplication.process_id_get_pagoCodigoBarras,
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
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					jsonformate = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
					Log.v("varahalababu", "varahalababu123" + jsonformate);
				} else {
					jsonformate = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
					Log.v("varahalababu", "varahalababu123" + jsonformate);
					exception1 = true;
					exception = false;
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
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(jsonformate);
					CommonMethods.showCustomToast(context,
							json.getString("message"));
				} catch (Exception e) {

				}
			} else {
				moveTosuccessPage(jsonformate);
			}
		}
	}

	public String getJsonObj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("codigoConvenio",
					consultaConvenioDTO.getCodigoConvenio());
			jsonobj.put("password", valueKey);
			jsonobj.put("amount", cnfmValue);
			jsonobj.put("productoID", consultaConvenioDTO.getProdCodigo());
			jsonobj.put("codigoConvenio",consultaConvenioDTO.getCodigoConvenio());
			jsonobj.put("codigoEntidad", consultaConvenioDTO.getCodigoEntidad());
			jsonobj.put("codigoIAC", consultaConvenioDTO.getIac());
			jsonobj.put("cuentaDestino", consultaConvenioDTO.getCuentaDestino());
			jsonobj.put("tipoCuentaDestino",
					consultaConvenioDTO.getTipoCuentaDestino());
			jsonobj.put("referencia1", consultaConvenioDTO.getRef1());
			jsonobj.put("referencia2", consultaConvenioDTO.getRef2());
			jsonobj.put("cuentaRecaudadora",
					consultaConvenioDTO.getCuentaRecaudadora());
			jsonobj.put("tipoCuentaRecaudadora",
					consultaConvenioDTO.getTipoCuentaRecaudadora());
			jsonobj.put("convID", consultaConvenioDTO.getConvId());
			jsonobj.put("msisdn", cellPhoneno);
			jsonobj.put("descripcion", consultaConvenioDTO.getDescripcion());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

}
