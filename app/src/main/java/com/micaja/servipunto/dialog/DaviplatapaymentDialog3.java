package com.micaja.servipunto.dialog;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.DaviPlataPaymentActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.service.SincronizarTransacciones;
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DaviplatapaymentDialog3 extends Dialog
		implements android.view.View.OnClickListener, android.content.DialogInterface.OnClickListener {
	private Context context;
	private ImageView img_close;
	private Button btn_reg_cancel, btn_reg_cnfm;

	private TextView authorizationNumber, txt_paquetigo, txtnumber, value, txtDialogTitle;
	private LinearLayout valueRemove;
	private String jsonformate, valorPago;
	public SharedPreferences sharedpreferences;

	ESCPOSPrinter printer = new ESCPOSPrinter();
	UserDetailsDTO dto;
	UsbDevice usbdevice = null;
	int result;
	ServiApplication appContext;
	private String idTransaccion, productNumber, authorization_number;

	public DaviplatapaymentDialog3(Context context, String jsonformate, String valorPago) {
		super(context);
		this.context = context;
		this.jsonformate = jsonformate;
		this.valorPago = valorPago;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		appContext = (ServiApplication) context.getApplicationContext();
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.daviplata_data_confirm_dailog);

		loadUI();
	}

	private void loadUI() {

		valueRemove = (LinearLayout) findViewById(R.id.valueremove);
		valueRemove.setVisibility(View.GONE);
		txt_paquetigo = (TextView) findViewById(R.id.txt_paquetigo);
		txt_paquetigo.setText(getContext().getResources().getString(R.string.daviplata_authorization_number));

		txtnumber = (TextView) findViewById(R.id.txtnumber);
		txtnumber.setText(getContext().getResources().getString(R.string.daviplata_value));

		img_close = (ImageView) findViewById(R.id.img_close);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btn_reg_cnfm = (Button) findViewById(R.id.btn_reg_cnfm);

		btn_reg_cancel.setText(getContext().getResources().getString(R.string.close));
		btn_reg_cnfm.setText(getContext().getResources().getString(R.string.print));
		img_close.setOnClickListener(this);
		btn_reg_cancel.setOnClickListener(this);
		btn_reg_cnfm.setOnClickListener(this);

		authorizationNumber = (TextView) findViewById(R.id.txt_remove_the_silver);
		value = (TextView) findViewById(R.id.txt_get_the_money);
		txtDialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		txtDialogTitle.setText(context.getResources().getString(R.string.DaviPlata_dialog3));

		getParsingHandler();

	}

	private void getParsingHandler() {
		try {
			JSONObject json = new JSONObject(jsonformate);
			authorizationNumber.setText(json.getString("authorizationNumber"));
			authorization_number = json.getString("authorizationNumber");
			value.setText("$ " + (new Double(valorPago)).longValue());
			idTransaccion = json.getString("transactionID");
			try {
				productNumber = json.getString("productNumber");
			} catch (Exception e) {

			}
			SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
			dto.setTipo_transaction(json.getString("transactionID"));
			dto.setCreation_date(Dates.currentdate());
			dto.setId_pdb_servitienda("");
			dto.setModule("cashout daviplata");
			dto.setAuthorization_number(json.getString("authorizationNumber"));
			dto.setTransaction_value(valorPago);
			dto.setCreated_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_date(Dates.currentdate());
			dto.setPunthored_synck_status("0");
			dto.setServitienda_synck_status("0");
			dto.setStatus("true");
			dto.setTransaction_datetime(Dates.currentdate());
			dto.setRowid("");
			dto.setModule_tipo_id(ServiApplication.tipo_cashout_daviplata_id);
			List<DTO> list = new ArrayList<DTO>();
			list.add(dto);
			SincronizarTransaccionesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
			Intent intent = new Intent(context, SincronizarTransacciones.class);
			// intent.putExtra("ModuleName", "cashout daviplata");
			// intent.putExtra("Module_Tipo_id", "5");
			context.startService(intent);

		} catch (Exception e) {
			// TODO: handle exception
		}
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
			printOperation();
			break;

		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void printOperation() {

		// if (NetworkConnectivity.netWorkAvailability(context)) {
		// new ImpresionTirilla(dto.getUserName(), dto.getPassword())
		// .execute();
		// } else {
		// CommonMethods.showCustomToast(context, context.getResources()
		// .getString(R.string.no_wifi_adhoc));
		// }

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

	private void printfinalOperation(String responds) {
		printer.setContext(context);
		result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, fecha = null;
		String[] valArray = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto = CommonMethods
					.removeAsrics(String.format(json.getString("encabezadoProducto"), "UTF-8"));
			pieProducto =CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));// String.format(json.getString("pieProducto"), "UTF-8");
			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
			valArray = Dates.printdate(fecha).split(" ");
			Log.v("varahlababu", "varahlababu" + json.getString("encabezadoProducto") + "" + encabezadoProducto + "  "
					+ json.getString("pieProducto") + " " + pieProducto);
		} catch (Exception e) {
		}

		// if (PrinterErrorMessages.printerStatus(context)) {
		//
		// Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.print_logo);
		// printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" + encabezadoProducto,
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" + context.getString(R.string.fecha_printer) +
		// ":", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_2WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText(" " + fecha, ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		//
		// printer.printText(
		// "\n" + context.getString(R.string.valor_printer) + ": $ " + (new
		// Double(valorPago)).longValue(),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" +
		// context.getString(R.string.num_auth_printer),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_2WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" + authorization_number,
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n\n\n" +
		// context.getString(R.string.id_transacction),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n" + idTransaccion,
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n\n\n\n\n\n" + context.getString(R.string.tear),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
		// printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
		// printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
		// result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
		// printer.disconnect();
		// } else {
		// CommonMethods.showCustomToast(context,
		// context.getResources().getString(R.string.printer_errmsg));
		// }

		ServiApplication.allprinters_flage = true;
		ServiApplication.flage_for_log_print = false;
		ServiApplication.setUiHandler(uuiHandler);

		ServiApplication.print_flage_eps = "\n 	   " + "DAVIVIENDA  PUNTORED" + "\n\n" + " 		" + encabezadoProducto
				+ "\n" + "     " + "\n" + "Pago por Daviplata" + "\n"
				+ context.getString(R.string.reference_de_pago_fecha_trans) + "     " + "\n" + valArray[0] + "\n"
				+ context.getString(R.string.reference_de_pago_hora_trans) + "      " + valArray[1] + "\n";
		// + context.getString(R.string.number_) + " " + valArray[1] + "\n";

		ServiApplication.print_flage = ServiApplication.print_flage_eps;
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			homeactivityLoad();
		} else {
			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);
		}
	}

	public String getJsonObj() {
		try {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId());
				jsonobj.put("terminalId", dto.getTerminalId());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
				jsonobj.put("idTransaccion", idTransaccion);
				jsonobj.put("msisdn", "");
				jsonobj.put("valor", "" + ((Long.parseLong(valorPago) * 100)));
				jsonobj.put("productNumber", productNumber);
				return jsonobj.toString();
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}

	private final Handler uuiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				homeactivityLoad();
			} else {

			}
		}
	};

	protected void homeactivityLoad() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(context, DaviPlataPaymentActivity.class);
	}
}
