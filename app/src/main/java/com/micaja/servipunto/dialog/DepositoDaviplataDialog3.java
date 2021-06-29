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
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.ComisionDTO;
import com.micaja.servipunto.database.dto.ConsultarCiudadesDTO;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
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

public class DepositoDaviplataDialog3 extends Dialog implements
		android.view.View.OnClickListener {

	private Button btnCnfrm, btnCancel;
	private ImageView imgClose;
	private EditText cnfrmEdttxtDaviplata1;
	private boolean isValid;
	private String cnfrmDaviplatatxt, Number_daviplata;
	private Context context;
	public DaviplataNumberDeDocumento documetn;
	public ConsultarCiudadesDTO ccDTO;
	public TextView cnfrm_daviplata_textView1, cnfrm_daviplata_textView2,
			cnfrm_daviplata_textView3, cnfrm_daviplata_textView4,
			cnfrm_daviplata_textView5, cnfrm_daviplata_textView6,
			cnfrm_daviplata_textView8, cnfrm_daviplata_textView9,
			cnfrm_daviplata_textView10;

	public ComisionDTO comision_DTO;
	public AcceptEfectivoDTO aedto;

	private SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	public DepositoDaviplataDialog3(Context context, String f_document,
			DaviplataNumberDeDocumento documetn, ConsultarCiudadesDTO ccDTO,
			String Number_daviplata, ComisionDTO comision_DTO,
			AcceptEfectivoDTO aedto) {
		super(context);
		this.context = context;
		this.documetn = documetn;
		this.ccDTO = ccDTO;
		this.Number_daviplata = Number_daviplata;
		this.comision_DTO = comision_DTO;
		this.aedto = aedto;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	private void initUI() {
		setContentView(R.layout.deposito_daviplata_confirm1);
		btnCnfrm = (Button) findViewById(R.id.btn_cnfrm);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		imgClose = (ImageView) findViewById(R.id.img_close);

		cnfrmEdttxtDaviplata1 = (EditText) findViewById(R.id.cnfrm_edttxt_daviplata1);

		cnfrm_daviplata_textView1 = (TextView) findViewById(R.id.cnfrm_daviplata_textView1);
		cnfrm_daviplata_textView2 = (TextView) findViewById(R.id.cnfrm_daviplata_textView2);
		cnfrm_daviplata_textView3 = (TextView) findViewById(R.id.cnfrm_daviplata_textView3);
		cnfrm_daviplata_textView4 = (TextView) findViewById(R.id.cnfrm_daviplata_textView4);
		cnfrm_daviplata_textView5 = (TextView) findViewById(R.id.cnfrm_daviplata_textView5);
		cnfrm_daviplata_textView6 = (TextView) findViewById(R.id.cnfrm_daviplata_textView6);

		cnfrm_daviplata_textView8 = (TextView) findViewById(R.id.cnfrm_daviplata_textView8);
		cnfrm_daviplata_textView9 = (TextView) findViewById(R.id.cnfrm_daviplata_textView9);
		cnfrm_daviplata_textView10 = (TextView) findViewById(R.id.cnfrm_daviplata_textView10);
		btnCnfrm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);
		loadUI();
	}

	private void loadUI() {
		cnfrm_daviplata_textView1.setText(documetn.getDocumento());
		cnfrm_daviplata_textView2.setText(documetn.getNombres() + " "+ documetn.getApellidos());
		cnfrm_daviplata_textView3.setText(documetn.getCelular());
		cnfrm_daviplata_textView4.setText(ccDTO.getDescripcion());
		cnfrm_daviplata_textView5.setText(Number_daviplata);
		cnfrm_daviplata_textView6.setText(comision_DTO.getNombre());
		cnfrm_daviplata_textView8.setText(comision_DTO.getValue());
		cnfrm_daviplata_textView9.setText(comision_DTO.getComision());
		cnfrm_daviplata_textView10.setText(comision_DTO.getTotal());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cnfrm:
			saveAndValidate();
			break;
		case R.id.btn_cancel:
			this.dismiss();
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}

	private void saveAndValidate() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!cnfrm_validate(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context, context.getResources()
					.getString(R.string.oops_errmsg), stringBuffer.toString(),
					null);
		} else {
			if (NetworkConnectivity.netWorkAvailability(context)) {
				this.dismiss();
				new GiroDaviplata().execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}

		}

	}

	private boolean cnfrm_validate(StringBuffer stringBuffer) {
		isValid = true;
		cnfrmDaviplatatxt = cnfrmEdttxtDaviplata1.getText().toString().trim();

		if (cnfrmDaviplatatxt.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.enter_cnfm_daviplata));

		}
		return isValid;
	}

	public class GiroDaviplata extends AsyncTask<Void, Void, Boolean> {
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json;
		private String encrypt_key, json_responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjCedula(), key);
			System.out.println(getJsonObjCedula());
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_girodaviplata,
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
					json_responds = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
				} else {
					json_responds = AESTEST.AESDecrypt(new ParsingHandler()
							.getString(new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
									"response", "data"), AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));

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
		protected void onPostExecute(Boolean result) {
			CommonMethods.dismissProgressDialog();
			Log.v("varahalababu", "===========>" + json_responds);
			if (exception) {
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(context,
							json.getString("message"));
				} catch (Exception e) {
				}
			} else {
				DepositoDaviplataDialog4 dialog = new DepositoDaviplataDialog4(
						context, documetn, ccDTO, Number_daviplata,
						comision_DTO, aedto, json_responds);
				dialog.show();
				dialog.setCancelable(false);
			}
		}
	}

	public String getJsonObjCedula() {
		JSONObject json = new JSONObject();
		try {
			json.put("password", cnfrmDaviplatatxt);
			json.put("comercioId", dto.getComercioId());
			json.put("terminalId", dto.getTerminalId());
			json.put("puntoDeVentaId", dto.getPuntoredId());
			json.put("typeDocument", aedto.getIdCentroAcopio());
			json.put("document", documetn.getDocumento());
			json.put("celular", documetn.getCelular());
			json.put("codigoConvenio", comision_DTO.getCodigoconvenio());
			json.put("idCiudad", ccDTO.getDepto_id());
			json.put("nombres", documetn.getNombres());
			json.put("apellidos", documetn.getApellidos());
			json.put("direccion", documetn.getDireccion());
			json.put("comisionGiro", comision_DTO.getComision());
			json.put("numeroDaviplata", Number_daviplata);
			json.put("valorGiro", comision_DTO.getTotal());
			try {
				if (documetn.getFechaExpedicion().length()>0) {
					json.put("fechaExpedicion", Dates.get_2bpunthodate(documetn.getFechaExpedicion()));//
				}else{
				json.put("fechaExpedicion", "2015-12-15");
				}
			} catch (Exception e) {
				json.put("fechaExpedicion", "2015-12-15");
			}
		} catch (Exception e) {
		}
		return json.toString();
	}

}
