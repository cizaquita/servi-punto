package com.micaja.servipunto.dialog;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.RechargeActivity;
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
import android.widget.TextView;

public class RechargeDialog2 extends Dialog implements android.view.View.OnClickListener {

	private String spnKey, idTransaccion, msisdn, productNumber, authorization_number, microwInsurence_productId,
			midTransaccion, mmsisdn, mproductNumber;
	private Context context;
	ServiApplication appContext;
	private String authNumber, authValue, saleBill, number, microwInsurenceAmount;

	private TextView auth_number, auth_value, sale_bill, value, authnumber, txtProductName;

	private TextView txtauthorization, txtProduct, txtRegvalue, txtRegassst, txtBills, txtValues;
	final Handler handler = new Handler();
	private Button btnPrint, btnLeave;
	private Double double1;
	private boolean service_loan;
	private Intent intent;
	private ImageView imgClose;
	public SharedPreferences sharedpreferences;
	private LinearLayout assitence_layout;
	CellularProviderDto mCellular;

	// ESCPOSPrinter printer = new ESCPOSPrinter();
	UserDetailsDTO dto;
	// UsbDevice usbdevice = null;
	int result, print_result = 1;

	public RechargeDialog2(Context context, String spnKey, String number, Double double1, boolean service_loan,
			String microwInsurenceAmount, CellularProviderDto cellular, String productNumber,
			String microwInsurence_productId) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		this.spnKey = spnKey;
		this.number = number;
		this.double1 = double1;
		this.service_loan = service_loan;
		this.microwInsurenceAmount = microwInsurenceAmount;
		this.productNumber = productNumber;
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		this.mCellular = cellular;
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		// printer.setContext(context);
		// result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		this.microwInsurence_productId = microwInsurence_productId;
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
		loadUI();
	}

	private void loadUI() {
		ServiApplication.setUiHandler(uiHandler);
		auth_number = (TextView) findViewById(R.id.auth_number);
		authnumber = (TextView) findViewById(R.id.number);

		auth_value = (TextView) findViewById(R.id.auth_value);
		sale_bill = (TextView) findViewById(R.id.sale_bill);
		value = (TextView) findViewById(R.id.value);

		btnPrint = (Button) findViewById(R.id.btn_print);
		btnLeave = (Button) findViewById(R.id.btn_leave);
		btnLeave.setText(getContext().getString(R.string.close));

		imgClose = (ImageView) findViewById(R.id.img_close);

		txtauthorization = (TextView) findViewById(R.id.txt_authorization);
		txtProduct = (TextView) findViewById(R.id.txtproduct);
		txtRegvalue = (TextView) findViewById(R.id.txt_regvalue);
		txtRegassst = (TextView) findViewById(R.id.txt_regassst);
		txtBills = (TextView) findViewById(R.id.txt_bills);
		txtValues = (TextView) findViewById(R.id.txt_values);
		txtProductName = (TextView) findViewById(R.id.txt_ProductName);
		txtProductName.setText(context.getResources().getString(R.string.rechrge_dialog2));

		imgClose.setOnClickListener(this);
		btnPrint.setOnClickListener(this);
		btnLeave.setOnClickListener(this);
		assitence_layout = (LinearLayout) findViewById(R.id.assitence_layout);

		if (service_loan) {
			try {
				JSONObject json = new JSONObject(AESTEST.AESDecrypt(ServiApplication.getMicroseguros_conform_data(),
						AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
				sale_bill.setText(json.getString("authorizationNumber"));
				value.setText("$" + (new Double(microwInsurenceAmount)).longValue());
				midTransaccion = json.getString("transactionID");
				mmsisdn = json.getString("msisdn");
				mproductNumber = json.getString("productNumber");
				insertpayamount("" + new Double(microwInsurenceAmount).longValue(), midTransaccion);
				
				
				SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
				dto.setTipo_transaction(json.getString("authorizationNumber"));
				dto.setCreation_date(Dates.currentdate());
				dto.setId_pdb_servitienda("");
				dto.setModule("microwInsurence");
				dto.setAuthorization_number(json.getString("msisdn"));
				dto.setTransaction_value(CommonMethods.getNumSeparator(Double.parseDouble(microwInsurenceAmount)));
				dto.setCreated_by(sharedpreferences.getString("user_name", ""));
				dto.setModified_by(sharedpreferences.getString("user_name", ""));
				dto.setModified_date(Dates.currentdate());
				dto.setPunthored_synck_status("0");
				dto.setServitienda_synck_status("0");
				dto.setStatus("true");
				dto.setTransaction_datetime(Dates.currentdate());
				dto.setRowid("");
				dto.setModule_tipo_id(ServiApplication.tipo_recarga_microinsurence);
				List<DTO> list = new ArrayList<DTO>();
				list.add(dto);
				SincronizarTransaccionesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
			
				
				Intent intent = new Intent(context, SincronizarTransacciones.class);
				context.startService(intent);
				
				
			} catch (Exception e) {
				Log.v("varun", "exception");
			}

		} else {
			assitence_layout.setVisibility(View.GONE);
		}
		try {
			JSONObject json = new JSONObject(AESTEST.AESDecrypt(ServiApplication.getRecharge_conform_data(),
					AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
			auth_number.setText(json.getString("authorizationNumber"));
			authnumber.setText(json.getString("msisdn"));
			auth_value.setText("$" + (new Double(double1)).longValue());
			authorization_number = json.getString("authorizationNumber");
			idTransaccion = json.getString("transactionID");
			msisdn = json.getString("msisdn");
			testst();
		} catch (Exception e) {

		}
		printButtonevent();
		

	}
	
	public void testst() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(12000);
				} catch (Exception e) {
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject json = new JSONObject(AESTEST.AESDecrypt(ServiApplication.getRecharge_conform_data(),
									AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
							auth_number.setText(json.getString("authorizationNumber"));
							authnumber.setText(json.getString("msisdn"));
							auth_value.setText("$" + (new Double(double1)).longValue());
							authorization_number = json.getString("authorizationNumber");
							idTransaccion = json.getString("transactionID");
							msisdn = json.getString("msisdn");

							insertpayamount("" + new Double(double1).longValue(), idTransaccion);

							SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
							dto.setTipo_transaction(json.getString("transactionID"));
							dto.setCreation_date(Dates.currentdate());
							dto.setId_pdb_servitienda("");
							dto.setModule("recarga");
							dto.setAuthorization_number(json.getString("msisdn"));
							dto.setTransaction_value(CommonMethods.getNumSeparator(double1));
							dto.setCreated_by(sharedpreferences.getString("user_name", ""));
							dto.setModified_by(sharedpreferences.getString("user_name", ""));
							dto.setModified_date(Dates.currentdate());
							dto.setPunthored_synck_status("0");
							dto.setServitienda_synck_status("0");
							dto.setStatus("true");
							dto.setTransaction_datetime(Dates.currentdate());
							dto.setRowid("");
							dto.setModule_tipo_id(ServiApplication.tipo_recarge_id);
							List<DTO> list = new ArrayList<DTO>();
							list.add(dto);
							SincronizarTransaccionesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
							Intent intent = new Intent(context, SincronizarTransacciones.class);
							context.startService(intent);
						} catch (Exception e) {
						}
					}
				});
			}
		}).start();
	}

	private void printButtonevent() {
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
			CommonMethods.openNewActivity(context, RechargeActivity.class);
			break;

		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("static-access")
	private void printOperation() {

		// new ImpresionTirilla(dto.getUserName(), dto.getPassword()).execute();

		// if (ESCPOSConst.CMP_SUCCESS == result) {

		if (NetworkConnectivity.netWorkAvailability(context)) {
			new ImpresionTirilla(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
		}
		// } else {
		// CommonMethods.showCustomToast(context,
		// new PrinterErrorMessages().printerErrorMsg(context));
		//
		// }
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
				appContext.pushActivity(intent);
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

	@SuppressWarnings("static-access")
	private void printfinalOperation(String responds) {

		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, fecha = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto = CommonMethods.removeAsrics(String.format(json.getString("encabezadoProducto"), "UTF-8"));
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//String.format(json.getString("pieProducto"), "UTF-8");
			// CommonMethods.utf8string(pieProducto);

			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
		} catch (Exception e) {
		}
		String[] valArray = Dates.printdate(fecha).split(" ");
		// if (ESCPOSConst.CMP_SUCCESS == result) {
		//
		// Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.print_logo);
		// printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
		// printer.printText("\n\n" +
		// CommonMethods.utf8string(encabezadoProducto),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" + mCellular.getProductName() + ": $ " + (new
		// Double(double1)).longValue(),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + context.getString(R.string.fecha_printer) +
		// ":", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\t" + valArray[0],
		// ESCPOSConst.CMP_ALIGNMENT_RIGHT, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n\t\t" + valArray[1],
		// ESCPOSConst.CMP_ALIGNMENT_RIGHT, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(" " + context.getString(R.string.numero) + ":",
		// ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\t" + number, ESCPOSConst.CMP_ALIGNMENT_RIGHT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(context.getString(R.string.num_auth_printer) + " "
		// + idTransaccion,
		// ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(CommonMethods.utf8string(pieProducto).replace("￳",
		// "o"), ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_FONTB, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" + context.getString(R.string.tear),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
		// printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
		//
		// printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
		// result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
		// printer.disconnect();
		//
		// if (service_loan) {
		// if (ESCPOSConst.CMP_SUCCESS == result) {
		// if (NetworkConnectivity.netWorkAvailability(context)) {
		// new VentaMicroseguros(dto.getUserName(),
		// dto.getPassword()).execute();
		// } else {
		// CommonMethods.showCustomToast(context,
		// context.getResources().getString(R.string.no_wifi_adhoc));
		// }
		// } else {
		// CommonMethods.showCustomToast(context, new
		// PrinterErrorMessages().printerErrorMsg(context));
		// }
		// } else {
		// appContext.clearActivityList();
		// CommonMethods.openNewActivity(context, RechargeActivity.class);
		// }
		//
		// } else {
		ServiApplication.print_flage_eps = encabezadoProducto + "\n       " + mCellular.getProductName() + ":  $  "
				+ (new Double(double1)).longValue() + "\n" + context.getString(R.string.fecha_printer) + ":      "
				+ valArray[0] + "  " + valArray[1] + "\n" + context.getString(R.string.numero) + ":              "
				+ number + "\n" + context.getString(R.string.num_auth_printer) + "  " + idTransaccion + "\n"
				+ pieProducto;
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			if (service_loan) {
				if (NetworkConnectivity.netWorkAvailability(context)) {
					new VentaMicroseguros(dto.getUserName(), dto.getPassword()).execute();
				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				appContext.clearActivityList();
				CommonMethods.openNewActivity(context, RechargeActivity.class);
			}
		} else {
			ServiApplication.print_flage = encabezadoProducto + "\n\n" + mCellular.getProductName() + ":  $  "
					+ (new Double(double1)).longValue() + "\n" + context.getString(R.string.fecha_printer) + ":   "
					+ valArray[0] + "  " + valArray[1] + "\n" + context.getString(R.string.numero) + ":    " + number
					+ "\n" + context.getString(R.string.num_auth_printer) + "  " + idTransaccion + "\n" + pieProducto;

			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			appContext.pushActivity(serverIntent);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);

			// if (service_loan) {
			// if (NetworkConnectivity.netWorkAvailability(context)) {
			// new VentaMicroseguros(dto.getUserName(),
			// dto.getPassword()).execute();
			// } else {
			// CommonMethods.showCustomToast(context,
			// context.getResources().getString(R.string.no_wifi_adhoc));
			// }
			// } else {
			// Intent serverIntent = new Intent(context, PRTSDKApp.class);
			// appContext.pushActivity(serverIntent);
			// serverIntent.putExtra("babu", "print");
			// ((Activity) context).startActivityForResult(serverIntent, 10);
			//
			// }
			// }
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
				jsonobj.put("msisdn", msisdn.trim());
				jsonobj.put("valor", "" + ((new Double(double1)).longValue() * 100));
				jsonobj.put("productNumber", productNumber.trim());
				return jsonobj.toString();
			} catch (Exception e) {
				return null;
			}

		} catch (Exception e) {
		}
		return null;
	}

	public class VentaMicroseguros extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getVentaMicroseguros();

		private String encrypt_key, responds;

		public VentaMicroseguros(String email, String password) {
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
				appContext.pushActivity(intent);
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
				printfinalOperation2(responds);
			}
		}

	}

	public String getVentaMicroseguros() {

		try {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId().trim());
				jsonobj.put("terminalId", dto.getTerminalId().trim());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId().trim());
				jsonobj.put("idTransaccion", midTransaccion.trim());
				jsonobj.put("msisdn", mmsisdn.trim());
				jsonobj.put("valor", "" + ((new Double(microwInsurenceAmount)).longValue() * 100));
				jsonobj.put("productNumber", microwInsurence_productId.trim());
				return jsonobj.toString();
			} catch (Exception e) {
				return null;
			}

		} catch (Exception e) {
		}
		return null;

	}

	private void printfinalOperation2(String responds2) {

		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, fecha = null;
		// result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		try {
			JSONObject json = new JSONObject(responds2);
			encabezadoProducto = CommonMethods.removeAsrics(String.format(json.getString("encabezadoProducto"), "UTF-8"));
			pieProducto =CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));// String.format(json.getString("pieProducto"), "UTF-8");
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
		// printer.printText("\n" +
		// CommonMethods.utf8string(encabezadoProducto),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// " " + context.getResources().getString(R.string.r_efectivo) + "\t\t"
		// + new Double(Long.parseLong(microwInsurenceAmount) /
		// 1.16).longValue(),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_FONTB,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// " " + context.getResources().getString(R.string.iva) + "\t\t"
		// + (Long.parseLong(microwInsurenceAmount)
		// - new Double(Long.parseLong(microwInsurenceAmount) /
		// 1.16).longValue()),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_FONTB,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// " " + context.getResources().getString(R.string.r_total) + "\t\t\t"
		// + Long.parseLong(microwInsurenceAmount),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_FONTB,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText(CommonMethods.utf8string(pieProducto).replace("￳",
		// "o"), ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_FONTB, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n" + context.getString(R.string.tear),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH |
		// ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
		// printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
		//
		// printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
		// result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
		// printer.disconnect();
		//
		// appContext.clearActivityList();
		// CommonMethods.openNewActivity(context, RechargeActivity.class);
		//
		// } else {
		ServiApplication.print_flage_eps = encabezadoProducto + "\n"
				+ context.getResources().getString(R.string.r_efectivo) + "\t"
				+ new Double(Long.parseLong(microwInsurenceAmount) / 1.16).longValue() + "\n"
				+ context.getResources().getString(R.string.iva) + "\t "
				+ (Long.parseLong(microwInsurenceAmount)
						- new Double(Long.parseLong(microwInsurenceAmount) / 1.16).longValue())
				+ "\n" + context.getResources().getString(R.string.r_total) + "\t   "
				+ Long.parseLong(microwInsurenceAmount) + "\n" + pieProducto;
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, RechargeActivity.class);
		} else {
			ServiApplication.print_flage = encabezadoProducto + "\n\n"
					+ context.getResources().getString(R.string.r_efectivo) + "      \t"
					+ new Double(Long.parseLong(microwInsurenceAmount) / 1.16).longValue() + "\n"
					+ context.getResources().getString(R.string.iva) + "        \t"
					+ (Long.parseLong(microwInsurenceAmount)
							- new Double(Long.parseLong(microwInsurenceAmount) / 1.16).longValue())
					+ "\n" + context.getResources().getString(R.string.r_total) + "           \t"
					+ Long.parseLong(microwInsurenceAmount) + "\n\n" + pieProducto;

			print_result++;
			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			appContext.pushActivity(serverIntent);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);

		}
		// }
	}

	private void insertpayamount(String payAmount, String aut_id) {

		List<DTO> clientList = new ArrayList<DTO>();
		ClientPaymentsDTO dto = new ClientPaymentsDTO();
		dto.setAmountPaid(payAmount);
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setIncomeType(ConstantsEnum.INCOME_TYPE_SALE.code());
		dto.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
		dto.setSaleId(aut_id);
		dto.setSyncStatus(1);
		clientList.add(dto);
		ClientPaymentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), clientList);
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				if (print_result == 1) {
					if (service_loan) {
						if (NetworkConnectivity.netWorkAvailability(context)) {
							new VentaMicroseguros(dto.getUserName(), dto.getPassword()).execute();
						} else {
							CommonMethods.showCustomToast(context,
									context.getResources().getString(R.string.no_wifi_adhoc));
						}
					} else {
						appContext.clearActivityList();
						CommonMethods.openNewActivity(context, RechargeActivity.class);
					}

				} else {
					appContext.clearActivityList();
					CommonMethods.openNewActivity(context, RechargeActivity.class);
				}
			} else {

			}
		}
	};
}
