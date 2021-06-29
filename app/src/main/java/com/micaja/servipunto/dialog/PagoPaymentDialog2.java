package com.micaja.servipunto.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.Proxy;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.PagoPaymentActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.PagoDocumentDTO;
import com.micaja.servipunto.database.dto.PagoProductsDTO;
import com.micaja.servipunto.database.dto.PagoProviderDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesPrint;

public class PagoPaymentDialog2 extends Dialog implements android.view.View.OnClickListener {
	private ImageView imgClose;
	private Button btnPrint, btnCancel;
	private Context context;
	ServiApplication appContext;
	public SharedPreferences sharedpreferences;

	private TextView txtProveedor, txtProduct, txtPrograma, txt_num_document, txtOtp, txtCellular, txtEmail, txtValore;

	private String spnProveder, spnProduct, spnProgrm, spnDocum, cDocument, cOtp, cCellular, cEmail, dynamicFields,
			gIRO_VALOR, server_res;
	String idTransaccion = null;
	PagoProviderDTO pagoprovider_dto = new PagoProviderDTO();
	PagoProductsDTO pagoproduct_dto = new PagoProductsDTO();
	PagoDocumentDTO pagoprogram_dto = new PagoDocumentDTO();
	AcceptEfectivoDTO documetn = new AcceptEfectivoDTO();
	UserDetailsDTO dto;

	public PagoPaymentDialog2(Context context, String spnProv, String sProduct, String sProgram, String sDocumentType2,
			String eMAIL, String cELLNUMBER, String oTP, String dOCUMENT_NUMBER, String dynamicFields,
			PagoProviderDTO pagoprovider_dto, PagoProductsDTO pagoproduct_dto, PagoDocumentDTO pagoprogram_dto,
			AcceptEfectivoDTO documetn, String server_res, String gIRO_VALOR, String giroId) {

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

		this.pagoprovider_dto = pagoprovider_dto;
		this.pagoproduct_dto = pagoproduct_dto;
		this.pagoprogram_dto = pagoprogram_dto;
		this.documetn = documetn;
		this.gIRO_VALOR = gIRO_VALOR;
		this.server_res = server_res;

		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = (ServiApplication) context.getApplicationContext();

		initUI();

	}

	private void initUI() {
		setContentView(R.layout.pago_payment_dialog2);

		txtProveedor = (TextView) findViewById(R.id.txtProveedor);
		txtProduct = (TextView) findViewById(R.id.txtProduct);
		txtPrograma = (TextView) findViewById(R.id.txtPrograma);
		txt_num_document = (TextView) findViewById(R.id.txt_num_document);
		txtOtp = (TextView) findViewById(R.id.txtOtp);
		txtCellular = (TextView) findViewById(R.id.txtCellular);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtValore = (TextView) findViewById(R.id.txtValore);

		btnCancel = (Button) findViewById(R.id.btn_leave);
		btnPrint = (Button) findViewById(R.id.btn_print);
		btnPrint.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);
		try {
			JSONObject json = new JSONObject(server_res);
			idTransaccion = json.getString("transaccionId");
		} catch (Exception e) {
			// TODO: handle exception
		}
		loadUi();
	}

	private void loadUi() {

		txtProveedor.setText(spnProveder);
		txtProduct.setText(spnProduct);
		txtPrograma.setText(spnProgrm);
		txt_num_document.setText(spnDocum);
		txtOtp.setText(cOtp);
		txtCellular.setText(cCellular);
		txtEmail.setText(cEmail);
		txtValore.setText(gIRO_VALOR);
		if (pagoproduct_dto.getTicket().equals("1")) {
			btnPrint.setVisibility(View.VISIBLE);
		} else {
			btnPrint.setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_close:
			this.dismiss();
			movetoMenuActivity();
			break;
		case R.id.btn_leave:
			this.dismiss();
			movetoMenuActivity();
			break;
		case R.id.btn_print:
			callPrintMethod();
			break;
		default:
			break;
		}
	}

	private void movetoMenuActivity() {
		this.dismiss();
		appContext.clearActivityList();
		CommonMethods.openNewActivity(context, PagoPaymentActivity.class);
	}

	private void callPrintMethod() {
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new ImpresionTirilla(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	public class ImpresionTirilla extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public ImpresionTirilla(String email, String password) {
			mEmail = email;
			mPassword = password;
			Log.d("varahalababu", "varahalababu" + getJsonObj());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(context, encrypt_key,
						ServiApplication.process_id_mpresionTirilla, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", "MyCashBox" + ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
				} else {
					responds = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
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
		protected void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(context, json.getString("message"));
				} catch (Exception e) {
				}

			} else {
				printfinalOperation(responds);
			}
		}

	}

	public String getJsonObj() {

		try {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId().trim());
				jsonobj.put("terminalId", dto.getTerminalId().trim());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId().trim());
				jsonobj.put("idTransaccion", idTransaccion.trim());
				jsonobj.put("msisdn", "1");
				jsonobj.put("valor", "" + Long.parseLong(gIRO_VALOR) * 100);// gIRO_VALOR
				jsonobj.put("productNumber", pagoproduct_dto.getProdCode());

				// jsonobj.put("comercioId", "240137");
				// jsonobj.put("terminalId", "153825");
				// jsonobj.put("puntoDeVentaId", "240138");
				// jsonobj.put("idTransaccion", "000967240665");
				// jsonobj.put("msisdn", "1");
				// jsonobj.put("valor", "49300");//gIRO_VALOR
				// jsonobj.put("productNumber", "340");
				return jsonobj.toString();
			} catch (Exception e) {
				return null;
			}

		} catch (Exception e) {
		}
		return null;
	}

	public void printfinalOperation(String responds) {
		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, fecha = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto = CommonMethods.removeAsrics(json.getString("encabezadoProducto"));
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//String.format(json.getString("pieProducto"), "UTF-8");
			// CommonMethods.utf8string(pieProducto);

			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
		} catch (Exception e) {
		}

		ServiApplication.himp_print_flage = false;
		ServiApplication.flage_for_log_print = true;
		ServiApplication.setUiHandler(uuiHandler);
		String[] valArray = Dates.printdate(fecha).split(" ");

		ServiApplication.print_flage = encabezadoProducto + "\n" + "TRANSACCION EXITOSA" + "\n" + "DOCUMENTO" + "   "
				+ cDocument + "\n" + "FECHA DE PAGO" + "   " + valArray[0] + " " + valArray[1] + "\n" + "VALOR"
				+ "  $   " + gIRO_VALOR + "\n" + "TRANSACCION" + "   " + idTransaccion + "\n" + "USUARIO" + "   "
				+ sharedpreferences.getString("name_of_store", "") + "\n" + pieProducto;

		Log.v("varahalababu", ServiApplication.print_flage);

		ServiApplication.print_flage_eps = ServiApplication.print_flage;

		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			movetoMenuActivity();
		} else {
			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			appContext.pushActivity(serverIntent);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);
		}
	}

	private final Handler uuiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				movetoMenuActivity();
			} else {
			}
		}
	};

}
