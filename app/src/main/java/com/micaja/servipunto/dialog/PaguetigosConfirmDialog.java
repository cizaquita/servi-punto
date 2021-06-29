package com.micaja.servipunto.dialog;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.activities.PaquetigosActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.service.SincronizarTransacciones;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesPrint;

public class PaguetigosConfirmDialog extends Dialog implements android.view.View.OnClickListener {
	private String spnKey, idTransaccion, msisdn, productNumber, authorization_number;
	private LinearLayout Linearlayout_number1, linearlayout_assistance1, linearlayout_billno, last_linearlayout;
	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;
	private String authNumber, authValue, saleBill, vaLue, number;

	private TextView auth_number, auth_value, sale_bill, value, authnumber, txtProductName;

	private Button btnPrint, btnLeave;
	private Double double1;
	private boolean service_loan;
	private Intent intent;
	private RelativeLayout layoutPopupHeader;
	private ImageView imgClose;
	private TextView txtAuthorization, txtProduct;
	CellularProviderDto mCellular;
	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	// ESCPOSPrinter printer = new ESCPOSPrinter();
	// UsbDevice usbdevice = null;
	int result;

	public PaguetigosConfirmDialog(Context context, String spnKey, String number, Double double1, boolean service_loan,
			CellularProviderDto cellular) {

		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		this.spnKey = spnKey;
		this.number = number;
		this.double1 = double1;
		this.service_loan = service_loan;
		this.mCellular = cellular;
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		// printer.setContext(context);
		// result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		ServiApplication.himp_print_flage = true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.recharge_dialog2);
		ServiApplication.setUiHandler(uiHandler);
		loadUI();

	}

	@SuppressLint("UseValueOf")
	private void loadUI() {

		auth_number = (TextView) findViewById(R.id.auth_number);
		authnumber = (TextView) findViewById(R.id.number);

		auth_value = (TextView) findViewById(R.id.auth_value);

		sale_bill = (TextView) findViewById(R.id.sale_bill);

		value = (TextView) findViewById(R.id.value);

		btnPrint = (Button) findViewById(R.id.btn_print);
		btnLeave = (Button) findViewById(R.id.btn_leave);
		btnLeave.setText(getContext().getString(R.string.pines_leave));

		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);

		btnPrint.setOnClickListener(this);
		btnLeave.setOnClickListener(this);

		txtProductName = (TextView) findViewById(R.id.txt_ProductName);
		txtProductName.setText(getContext().getString(R.string.paquetigos_dialog2));

		linearlayout_assistance1 = (LinearLayout) findViewById(R.id.linearlayout_assistance1);
		linearlayout_billno = (LinearLayout) findViewById(R.id.linearlayout_billno);
		last_linearlayout = (LinearLayout) findViewById(R.id.last_linearlayout);
		layoutPopupHeader = (RelativeLayout) findViewById(R.id.layout_popup_header);

		linearlayout_assistance1.setVisibility(View.GONE);
		linearlayout_billno.setVisibility(View.GONE);
		last_linearlayout.setVisibility(View.GONE);

		txtAuthorization = (TextView) findViewById(R.id.txt_authorization);
		txtAuthorization.setText(getContext().getString(R.string.auth_number));

		txtProduct = (TextView) findViewById(R.id.txtproduct);
		txtProduct.setText(getContext().getString(R.string.reg_product));

		try {
			JSONObject json = new JSONObject(AESTEST.AESDecrypt(ServiApplication.getRecharge_conform_data(),
					AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));

			/* Add sale info to sales table */

			List<DTO> clientList = new ArrayList<DTO>();
			ClientPaymentsDTO cdto = new ClientPaymentsDTO();

			cdto.setAmountPaid("" + mCellular.getValor());
			cdto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
			cdto.setIncomeType(ConstantsEnum.INCOME_TYPE_SALE.code());
			cdto.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
			cdto.setSaleId(String.valueOf(json.getString("authorizationNumber")));
			cdto.setSyncStatus(1);
			clientList.add(cdto);
			ClientPaymentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), clientList);

			auth_number.setText(json.getString("authorizationNumber"));
			authnumber.setText(json.getString("msisdn"));
			auth_value.setText("$" + (new Double(mCellular.getValor()).longValue()));

			authorization_number = json.getString("authorizationNumber");
			idTransaccion = json.getString("transactionID");
			msisdn = json.getString("msisdn");

			// call sincronizartransaccion
			SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
			dto.setTipo_transaction(json.getString("transactionID"));
			dto.setCreation_date(Dates.currentdate());
			dto.setId_pdb_servitienda("");
			dto.setModule("Paquetigo");
			dto.setAuthorization_number(json.getString("msisdn"));
			dto.setTransaction_value(mCellular.getValor());
			dto.setCreated_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_date(Dates.currentdate());
			dto.setPunthored_synck_status("0");
			dto.setServitienda_synck_status("0");
			dto.setStatus("true");
			dto.setTransaction_datetime(Dates.currentdate());
			dto.setRowid("");
			dto.setModule_tipo_id(ServiApplication.tipo_paquetigo_id);
			List<DTO> list = new ArrayList<DTO>();
			list.add(dto);
			SincronizarTransaccionesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
			Intent intent = new Intent(context, SincronizarTransacciones.class);
			intent.putExtra("ModuleName", "Paquetigo");
			intent.putExtra("Module_Tipo_id", "2");
			context.startService(intent);

		} catch (Exception e) {
		}
		if (mCellular.getPrintable()) {
			btnPrint.setVisibility(View.VISIBLE);
		} else {
			btnPrint.setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_print:
			printOperation();
			break;

		case R.id.btn_leave:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, MenuActivity.class);
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}

	private void printOperation() {

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
				// appContext.pushActivity(intent);
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
		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, fecha = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto =CommonMethods.removeAsrics(String.format(json.getString("encabezadoProducto"), "UTF-8")); 
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//String.format(json.getString("pieProducto"), "UTF-8");
			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
		} catch (Exception e) {
		}
		String[] valArray = Dates.printdate(fecha).split(" ");
		// if (ESCPOSConst.CMP_SUCCESS == result) {
		// Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.print_logo);
		// printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText(mCellular.getProductName(),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + context.getString(R.string.fecha_printer) +
		// ":", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\t" + valArray[0],
		// ESCPOSConst.CMP_ALIGNMENT_RIGHT, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\t\t" + valArray[1],
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + context.getString(R.string.number_de_celuar)
		// + ":", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText(" " + number, ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + context.getString(R.string.valor_printer) +
		// ":", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText(" $" + (new
		// Double(mCellular.getValor()).longValue()),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + context.getString(R.string.num_auth_printer),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + authorization_number,
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(context.getString(R.string.id_transacction) + " " +
		// idTransaccion,
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(context.getString(R.string.tear),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
		// printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
		//
		// printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
		// result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
		// printer.disconnect();
		// if (result == 0) {
		// this.dismiss();
		// appContext.clearActivityList();
		// CommonMethods.openNewActivity(context, MenuActivity.class);
		// }
		// } else {
		ServiApplication.print_flage_eps = mCellular.getProductName() + "\n" + context.getString(R.string.fecha_printer)
				+ ":" + "      " + valArray[0] + " " + valArray[1] + "\n" + context.getString(R.string.number_de_celuar)
				+ ":        " + number + "\n" + context.getString(R.string.valor_printer) + ":" + "  $"
				+ (new Double(mCellular.getValor()).longValue()) + "\n" + context.getString(R.string.num_auth_printer)
				+ "           " + authorization_number + "\n" + context.getString(R.string.id_transacction) + "   "
				+ idTransaccion;
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			this.dismiss();
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, PaquetigosActivity.class);
		} else {
			ServiApplication.print_flage = mCellular.getProductName() + "\n" + context.getString(R.string.fecha_printer)
					+ ":" + "      " + valArray[0] + " " + valArray[1] + "\n"
					+ context.getString(R.string.number_de_celuar) + ":        " + number + "\n"
					+ context.getString(R.string.valor_printer) + ":" + "  $"
					+ (new Double(mCellular.getValor()).longValue()) + "\n"
					+ context.getString(R.string.num_auth_printer) + "           " + authorization_number + "\n"
					+ context.getString(R.string.id_transacction) + "   " + idTransaccion;
			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			appContext.pushActivity(serverIntent);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);

		}
	}
	// }

	public String getJsonObj() {
		try {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId().trim());
				jsonobj.put("terminalId", dto.getTerminalId().trim());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId().trim());
				jsonobj.put("idTransaccion", idTransaccion.trim());
				jsonobj.put("msisdn", msisdn.trim());
				jsonobj.put("valor", "" + (Long.parseLong(mCellular.getValor()) * 100));
				jsonobj.put("productNumber", mCellular.getProductNumber().trim());
				return jsonobj.toString();
			} catch (Exception e) {
				return null;
			}

		} catch (Exception e) {
		}
		return null;
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				appContext.clearActivityList();
				CommonMethods.openNewActivity(context, PaquetigosActivity.class);
			} else {

			}
		}
	};

}