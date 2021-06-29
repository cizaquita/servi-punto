package com.micaja.servipunto.dialog;

import java.net.Proxy;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.PagoDocumentDTO;
import com.micaja.servipunto.database.dto.PagoProductsDTO;
import com.micaja.servipunto.database.dto.PagoProviderDTO;
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

import android.app.Dialog;
import android.content.Context;
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

public class PagoPaymentDialog1 extends Dialog implements android.view.View.OnClickListener {

	private Context context;
	ServiApplication appContext;
	private ImageView imgClose;
	private Button btnPrint, btnCancel;
	private Intent intent;
	private EditText key;
	private boolean isValid;

	private String spnProveder, spnProduct, spnProgrm, spnDocum, cDocument, cOtp, cCellular, cEmail, dynamicFields,s_key,giroId,codigoDavivienda,gIRO_VALOR,server_res;

	private TextView txt_proveedor, txt_product, txt_programa, txt_tripo_de_docum, txt_num_de_docu, txt_otp,
			txt_celular, txt_email, txt_numero_de_cuenta, txt_value;
	PagoProviderDTO pagoprovider_dto = new PagoProviderDTO();
	PagoProductsDTO pagoproduct_dto = new PagoProductsDTO();
	PagoDocumentDTO pagoprogram_dto = new PagoDocumentDTO();// Programs DTO
	AcceptEfectivoDTO documetn = new AcceptEfectivoDTO();// this is Selected
															// Document
	private LinearLayout last_linearlayout_numero_de_cuenta;
	UserDetailsDTO dto;
	public SharedPreferences sharedpreferences;

	public PagoPaymentDialog1(Context context, String spnProv, String sProduct, String sProgram, String sDocumentType2,
			String eMAIL, String cELLNUMBER, String oTP, String dOCUMENT_NUMBER, String dynamicFields,
			PagoProviderDTO pagoprovider_dto, PagoProductsDTO pagoproduct_dto, PagoDocumentDTO pagoprogram_dto,
			AcceptEfectivoDTO documetn, String giroId, String codigoDavivienda, String gIRO_VALOR) {

		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		this.spnProveder = spnProv;
		this.spnProduct = sProduct;
		this.spnProgrm = sProgram;
		this.spnDocum = sDocumentType2;
		this.cOtp = oTP;
		this.cCellular = cELLNUMBER;
		this.cEmail = eMAIL;
		this.cDocument = dOCUMENT_NUMBER;
		this.dynamicFields = dynamicFields;

		this.pagoprovider_dto = pagoprovider_dto;
		this.pagoproduct_dto = pagoproduct_dto;
		this.pagoprogram_dto = pagoprogram_dto;
		this.documetn = documetn;
		this.giroId=giroId;
		this.codigoDavivienda=codigoDavivienda;
		this.gIRO_VALOR=gIRO_VALOR;

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.pago_payment_dialog1);

		txt_proveedor = (TextView) findViewById(R.id.txt_proveedor);
		txt_product = (TextView) findViewById(R.id.txt_product);
		txt_programa = (TextView) findViewById(R.id.txt_programa);
		txt_tripo_de_docum = (TextView) findViewById(R.id.txt_tripo_de_docum);
		txt_num_de_docu = (TextView) findViewById(R.id.txt_num_de_docu);
		txt_otp = (TextView) findViewById(R.id.txt_otp);
		txt_celular = (TextView) findViewById(R.id.txt_celular);
		txt_email = (TextView) findViewById(R.id.txt_email);
		txt_numero_de_cuenta = (TextView) findViewById(R.id.txt_numero_de_cuenta);
		txt_value = (TextView) findViewById(R.id.txt_value);
		last_linearlayout_numero_de_cuenta=(LinearLayout)findViewById(R.id.last_linearlayout_numero_de_cuenta);
		last_linearlayout_numero_de_cuenta.setVisibility(View.GONE);
		key = (EditText) findViewById(R.id.Etxtdlg_key);

		imgClose = (ImageView) findViewById(R.id.img_close);
		btnPrint = (Button) findViewById(R.id.btn_print);
		btnCancel = (Button) findViewById(R.id.btn_leave);

		imgClose.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnPrint.setOnClickListener(this);
		loadUI();
		
	}

	private void loadUI() {

		txt_proveedor.setText(spnProveder);
		txt_product.setText(spnProduct);
		txt_programa.setText(spnProgrm);
		txt_tripo_de_docum.setText(spnDocum);
		txt_otp.setText(cOtp);
		txt_celular.setText(cCellular);
		txt_email.setText(cEmail);
		txt_numero_de_cuenta.setText("");
		txt_value.setText("$ "+gIRO_VALOR);
		txt_num_de_docu.setText(cDocument);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_close:
			this.dismiss();
			break;
		case R.id.btn_print:
			validateAndSave();
			break;
		case R.id.btn_leave:
			this.dismiss();

			break;
		default:
			break;
		}
	}

	private void validateAndSave() {

		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context, getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new RealizarPagoConProcessId(dto.getUserName(), dto.getPassword()).execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
			}

		}
	}

	/* call Recharges confirmation */
	public class RealizarPagoConProcessId extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean exception = false, exception1 = false;
		private String encrypt_key;
		private String json;

		public RealizarPagoConProcessId(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
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
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(context, encrypt_key,
						ServiApplication.process_id_RealizarPago_con_processId, ServiApplication.username, json)));
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
					server_res=AESTEST.AESDecrypt(new ParsingHandler().getString(
							new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
					AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));
					Log.v("varahalababu",
							"varahalababu rec" + AESTEST.AESDecrypt(new ParsingHandler().getString(
									new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
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
			Log.v("varahalababu", AESTEST.AESDecrypt(server_res,
					AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(server_res,
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(context, json.getString("message"));
//					movetoNextDialog();
				} catch (Exception e) {
					appContext.pushActivity(intent);
					OopsErrorDialog dialog = new OopsErrorDialog(context);
					dialog.show();
					dialog.setCancelable(false);
				}
			} else {
				movetoNextDialog();
			}

		}
	}

	public void movetoNextDialog() {
		this.dismiss();
		PagoPaymentDialog2 dialog = new PagoPaymentDialog2(context, spnProveder, spnProduct, spnProgrm, spnDocum,
				cEmail, cCellular, cOtp, cDocument, dynamicFields, pagoprovider_dto, pagoproduct_dto, pagoprogram_dto,
				documetn,server_res,gIRO_VALOR,giroId);
		dialog.show();
		dialog.setCancelable(false);
	}

	public String getJsonObjValidarInfopago() {
		JSONObject jsonobj = new JSONObject();
		try {
//			 jsonobj.put("comercioId", dto.getComercioId());
//			 jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			 
			 jsonobj.put("terminalId", dto.getTerminalId());
			 jsonobj.put("clave", s_key);
			 jsonobj.put("amount", gIRO_VALOR);
			 jsonobj.put("prodCode", pagoproduct_dto.getProdCode());
			 jsonobj.put("autorizer", pagoproduct_dto.getAutorizer());
			 jsonobj.put("codigoDavivienda", codigoDavivienda);
			 jsonobj.put("giroId", giroId);
			 jsonobj.put("celular", cCellular);
			
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private boolean validateFields(StringBuffer stringBuffer) {
		s_key = key.getText().toString().trim();
		isValid = true;

		if (s_key.length() == 0) {
			isValid = false;
			stringBuffer.append("\n" + getContext().getString(R.string.enter_key));

		} else {
			if (CommonMethods.keyvalidation(s_key)) {
				stringBuffer.append(" " + getContext().getString(R.string.wrong_key));
				isValid = false;
			} else {
				isValid = true;
			}
		}
		return isValid;
	}

}
