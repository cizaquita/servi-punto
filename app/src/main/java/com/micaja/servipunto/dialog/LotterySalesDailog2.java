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
import android.widget.TextView;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.LotterySalesActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.ConsultarLoteriasDTO;
import com.micaja.servipunto.database.dto.ConsultarSeriesLoteriasDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.service.SincronizarTransacciones;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
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

public class LotterySalesDailog2 extends Dialog
		implements android.view.View.OnClickListener, android.content.DialogInterface.OnClickListener {

	private Context context;

	private ImageView img_close;
	private Button btn_reg_cancel, btn_reg_cnfm;

	private TextView tv_lottery, tv_number, tv_series, tv_fraction, tv_amount, tv_number_authorization;

	private String fjson, nUMBER, authorizationNumber;
	ConsultarLoteriasDTO clDTO;
	ConsultarSeriesLoteriasDTO cslDTO;
	public SharedPreferences sharedpreferences;
	ServiApplication appContext;
	UserDetailsDTO dto;
	private Intent intent;

	ESCPOSPrinter printer = new ESCPOSPrinter();
	UsbDevice usbdevice = null;
	int result;
	private String idTransaccion, msisdn, productNumber, fecha, distribuidor, numbers4;

	public LotterySalesDailog2(Context context, String fjson, String nUMBER, ConsultarLoteriasDTO clDTO,
			ConsultarSeriesLoteriasDTO cslDTO, String numbers4) {
		super(context);
		this.context = context;
		this.fjson = fjson;
		this.nUMBER = nUMBER;
		this.clDTO = clDTO;
		this.cslDTO = cslDTO;
		this.numbers4 = nUMBER;
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		distribuidor = sharedpreferences.getString("distribuidor", "");
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		printer.setContext(context);
		result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.lottery_sales_dailog2);
		img_close = (ImageView) findViewById(R.id.img_close);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btn_reg_cnfm = (Button) findViewById(R.id.btn_reg_cnfm);

		tv_number_authorization = (TextView) findViewById(R.id.tv_number_authorization);
		tv_lottery = (TextView) findViewById(R.id.tv_lottery);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_series = (TextView) findViewById(R.id.tv_series);

		tv_fraction = (TextView) findViewById(R.id.tv_fraction);
		tv_amount = (TextView) findViewById(R.id.tv_amount);

		loadUI();

	}

	@SuppressLint("UseValueOf")
	private void loadUI() {

		tv_number_authorization = (TextView) findViewById(R.id.tv_number_authorization);
		try {
			JSONObject json = new JSONObject(fjson);
			tv_number_authorization.setText(json.getString("authorizationNumber"));
			authorizationNumber = json.getString("authorizationNumber");
			SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
			dto.setTipo_transaction(json.getString("transactionID"));
			dto.setCreation_date(Dates.currentdate());
			dto.setId_pdb_servitienda("");
			dto.setModule("loteries");
			dto.setAuthorization_number(json.getString("authorizationNumber"));
			dto.setTransaction_value(clDTO.getValor());
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
			intent.putExtra("ModuleName", "loteries");
			intent.putExtra("Module_Tipo_id", "8");
			context.startService(intent);
			try {
				Log.v("varahalababu", "varahalababu" + json.toString());
				idTransaccion = json.getString("transactionID");// 967124706
				// idTransaccion = "967124706";
				msisdn = "1";
				productNumber = "900";

			} catch (Exception e) {
			}

		} catch (Exception e) {
		}
		tv_lottery.setText(clDTO.getLoteName());
		tv_number.setText(nUMBER);
		tv_series.setText(cslDTO.getSerie());
		tv_fraction.setText(cslDTO.getFractions());
		tv_amount.setText("$ " + (new Double(clDTO.getValor()).longValue()));

		img_close.setOnClickListener(this);
		btn_reg_cancel.setOnClickListener(this);
		btn_reg_cnfm.setOnClickListener(this);
		
		insertpayamount("" + new Double(clDTO.getValor()).longValue(), authorizationNumber);


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

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, LotterySalesActivity.class);
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
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new ImpresionTirilla(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
		}
		// if (ESCPOSConst.CMP_SUCCESS == result) {
		// if (NetworkConnectivity.netWorkAvailability(context)) {
		// new ImpresionTirilla(dto.getUserName(), dto.getPassword())
		// .execute();
		//
		// } else {
		// CommonMethods.showCustomToast(context, context.getResources()
		// .getString(R.string.no_wifi_adhoc));
		// }
		//
		// } else {
		// CommonMethods.showCustomToast(context,
		// new PrinterErrorMessages().printerErrorMsg(context));
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
		protected void onPostExecute(Boolean result) {
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

	private void printfinalOperation(String responds) {

		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, LoteNumberLong = null,
				tSerie = null;
		String[] valArray = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto = String.format(json.getString("encabezadoProducto"), "UTF-8");
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//String.format(json.getString("pieProducto"), "UTF-8");
			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
			LoteNumberLong = CommonMethods.getLettersOfNumaric(numbers4, context);
			tSerie = CommonMethods.getLettersOfNumaric(cslDTO.getSerie(), context);

			valArray = Dates.printdate(fecha).split(" ");
			Log.v("varahlababu",
					"varahalababu" + valArray[0] + "\n" + valArray[1] + "\n" + clDTO.getLoteName() + "\n"
							+ clDTO.getLoteNumberLong() + "\n" + LoteNumberLong + "\n" + cslDTO.getSerie() + "\n"
							+ tSerie + "\n" + clDTO.getDateClose() + "\n" + clDTO.getValor() + "\n"
							+ clDTO.getLoteFraction() + "\n" + authorizationNumber + "\n" + distribuidor);

		} catch (Exception e) {
		}
//		if (ESCPOSConst.CMP_SUCCESS == result) {
//			Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.print_logo);
//			printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText(context.getString(R.string.l_title), ESCPOSConst.CMP_ALIGNMENT_CENTER,
//					ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText(context.getString(R.string.reference_de_pago_fecha_trans) + "\t" + valArray[0],
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText(
//					context.getString(R.string.reference_de_pago_hora_trans) + "\t" + Dates.getTimeFormate(valArray[1]),
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.l_loteria) + "\t\t" + clDTO.getLoteName(),
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText(context.getString(R.string.l_numero) + "\t\t" + numbers4, ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_BOLD, ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(LoteNumberLong, ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText(context.getString(R.string.l_serie) + "\t\t" + cslDTO.getSerie(),
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(tSerie, ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.l_fecha_del_sorteo) + "\t\t" + clDTO.getDateClose(),
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.l_valor) + "\t\t" + "$" + clDTO.getValor(),
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.l_fraccion) + "\t\t" + clDTO.getLoteFraction(),
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(
//					context.getString(R.string.reference_de_pago_codigo_autorizacion) + "\t" + authorizationNumber,
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_user_de_venta) + "\t" + distribuidor,
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1HEIGHT | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
//			printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
//			printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
//			result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
//			printer.disconnect();
//
//		} else {
//			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.printer_errmsg));
//		}
		
		ServiApplication.allprinters_flage = true;
		ServiApplication.flage_for_log_print = true;
		ServiApplication.setUiHandler(uuiHandler);
		

		ServiApplication.print_flage_eps = "\n 	   " + context.getString(R.string.l_title) + "\n\n" 
		+context.getString(R.string.reference_de_pago_fecha_trans) + " 			" + valArray[0]+"\n"
		+context.getString(R.string.reference_de_pago_hora_trans) + " 			" + Dates.getTimeFormate(valArray[1])+"\n"
		+context.getString(R.string.l_loteria) + " 			" + clDTO.getLoteName()+"\n"
		+context.getString(R.string.l_numero) + " 			" + numbers4+"\n"
		+" 	   "+LoteNumberLong+"\n"
		+context.getString(R.string.l_serie) + " 			" + cslDTO.getSerie()+"\n"
		+" 	   "+tSerie+"\n"
		+context.getString(R.string.l_fecha_del_sorteo) + " 			" + clDTO.getDateClose()+"\n"
		+context.getString(R.string.l_valor) + " 			" + "$" + clDTO.getValor()+"\n"
		+context.getString(R.string.l_fraccion) + " 			" + clDTO.getLoteFraction()+"\n"
		+context.getString(R.string.reference_de_pago_codigo_autorizacion) + " 			" + authorizationNumber+"\n"
		+context.getString(R.string.reference_de_pago_user_de_venta) + " 			" +  distribuidor+"\n\n\n";

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
				jsonobj.put("productNumber", productNumber);
				jsonobj.put("msisdn", msisdn);
				jsonobj.put("valor", "" + new Double(clDTO.getValor()).longValue());
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
		CommonMethods.openNewActivity(context, LotterySalesActivity.class);

	}
}
