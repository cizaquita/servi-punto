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
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ConsultarLoteriasDTO;
import com.micaja.servipunto.database.dto.ConsultarSeriesLoteriasDTO;
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

public class LotterySalesDailog1 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;

	private ImageView img_close;
	private Button btn_reg_cancel, btn_reg_cnfm;

	private TextView tv_lottery, tv_number, tv_series, tv_fraction, tv_amount,
			tv_number_celular, tv_document;
	private EditText ed_key;
	private boolean isValid;
	private String edKey;
	ConsultarLoteriasDTO clDTO;
	ConsultarSeriesLoteriasDTO cslDTO;
	private String edCellular, edDocument, nUMBER, numbers4;

	public SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;

	public LotterySalesDailog1(Context context, ConsultarLoteriasDTO clDTO,
			ConsultarSeriesLoteriasDTO cslDTO, String edCellular,
			String edDocument, String nUMBER, String numbers4) {
		super(context);
		this.context = context;
		this.clDTO = clDTO;
		this.cslDTO = cslDTO;
		this.edCellular = edCellular;
		this.edDocument = edDocument;
		this.nUMBER = nUMBER;
		this.numbers4 = numbers4;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.lottery_sales_dailog1);
		img_close = (ImageView) findViewById(R.id.img_close);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btn_reg_cnfm = (Button) findViewById(R.id.btn_reg_cnfm);

		tv_lottery = (TextView) findViewById(R.id.tv_lottery);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_series = (TextView) findViewById(R.id.tv_series);

		tv_fraction = (TextView) findViewById(R.id.tv_fraction);
		tv_amount = (TextView) findViewById(R.id.tv_amount);
		tv_number_celular = (TextView) findViewById(R.id.tv_number_celular);
		tv_document = (TextView) findViewById(R.id.tv_document);
		ed_key = (EditText) findViewById(R.id.ed_key);
		loadUI();

	}

	private void loadUI() {

		img_close.setOnClickListener(this);
		btn_reg_cancel.setOnClickListener(this);
		btn_reg_cnfm.setOnClickListener(this);

		tv_lottery.setText(clDTO.getLoteName());
		tv_number.setText(nUMBER);
		try {
			tv_series.setText(cslDTO.getSerie());

			tv_fraction.setText(cslDTO.getFractions());
		} catch (Exception e) {
			tv_series.setText(null);
			tv_fraction.setText(null);
		}
		tv_amount.setText("$ " + (new Double(clDTO.getValor()).longValue()));
		tv_number_celular.setText(edCellular);
		tv_document.setText(edDocument);

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

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
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new VentaLoteria(dto.getUserName(), dto.getPassword())
						.execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
		}
	}

	private void moveToLotterySalesDailog2(String fjson) {
		this.dismiss();
		LotterySalesDailog2 dialog = new LotterySalesDailog2(context, fjson,
				nUMBER, clDTO, cslDTO, numbers4);
		dialog.show();
		dialog.setCancelable(false);
	}

	private boolean validateFields(StringBuffer stringBuffer) {
		isValid = true;
		edKey = ed_key.getText().toString().trim();
		if (edKey.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getContext().getString(R.string.enter_key));
		} else {
			if (edKey.length() >= 6) {
				isValid = true;
			} else {
				stringBuffer.append("\n"
						+ getContext().getString(R.string.invalid_lotkey));
				isValid = false;
			}
		}

		return isValid;
	}

	/* call ventaLoteria confirmation */
	public class VentaLoteria extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean flage = false, exception = false, exception1 = false;;
		private String microwInsurenceAmount_value;
		private String encrypt_key;
		private String json, server_responds;

		public VentaLoteria(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjformicro(), key);
			System.out.println(getJsonObjformicro());
		}

		@Override
		public Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_VentaLoterias,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("DUMP REQUEST", "varahalababu" + ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.v("SOAP", "varahalababu" + resultsString.toString());
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {

					server_responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
				} else {
					server_responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				exception = true;
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
		public void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(
							server_responds, AESTEST
									.AESkeyFromString(sharedpreferences
											.getString("AutoGenKey", ""))));
					CommonMethods.showCustomToast(context,
							json.getString("message"));
				} catch (Exception e) {
					// TODO: handle exception
				}

				// moveToLotterySalesDailog2(AESTEST.AESDecrypt(server_responds,
				// AESTEST
				// .AESkeyFromString(sharedpreferences.getString(
				// "AutoGenKey", ""))));
			} else {
				moveToLotterySalesDailog2(AESTEST.AESDecrypt(server_responds,
						AESTEST.AESkeyFromString(sharedpreferences.getString(
								"AutoGenKey", ""))));
			}
		}
	}

	public String getJsonObjformicro() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("password", edKey);
			// jsonobj.put("valor",""+Long.parseLong(""+Double.parseDouble(clDTO.getValor())
			// * 100));
			jsonobj.put(
					"valor",
					""
							+ ((new Double(Double.parseDouble(clDTO.getValor())))
									.longValue()));
			jsonobj.put("numero", nUMBER);
			jsonobj.put("loteId", clDTO.getLoteID());
			jsonobj.put("cedula", edDocument);
			jsonobj.put("sorteo", clDTO.getSorteo());
			jsonobj.put("msisdn", edCellular);
			jsonobj.put("serie", cslDTO.getSerie());
			jsonobj.put("loteName", clDTO.getLoteName());
			jsonobj.put("fechaSorteo", clDTO.getLoteName());

			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
