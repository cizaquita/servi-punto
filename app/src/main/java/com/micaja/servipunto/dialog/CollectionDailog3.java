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
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.ConsultaConveniosDTO;
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

public class CollectionDailog3 extends Dialog
		implements android.view.View.OnClickListener, android.content.DialogInterface.OnClickListener {

	private Context context;

	private ImageView img_close;
	private Button btnCollectionPrint, btnCollectionCancel;

	private String Key, nit, paymentCode, value, cellular, tvDate, resjson, idTransaccion, msisdn, numeroPin, serialPin;
	ConsultaConveniosDTO ccDTO;

	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	private TextView tv_number_authorization, tv_agrement, tv_code_agrement, tv_ref1, tv_ref2, tv_value;

	ServiApplication appContext;

	ESCPOSPrinter printer = new ESCPOSPrinter();
	UsbDevice usbdevice = null;
	int result;

	private String horaTransaccion, fecha, distribuidor, idDavivienda;

	public CollectionDailog3(Context context, Context context2, String nit, String paymentCode, String value,
			String cellular, String tvDate, ConsultaConveniosDTO ccDTO, String resjson) {
		super(context);
		this.context = context;
		this.context = context;
		this.nit = nit;
		this.paymentCode = paymentCode;
		this.value = value;
		this.cellular = cellular;
		this.tvDate = tvDate;
		this.ccDTO = ccDTO;
		this.resjson = resjson;

		appContext = (ServiApplication) context.getApplicationContext();

		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		distribuidor = sharedpreferences.getString("distribuidor", "");

		printer.setContext(context);
		result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.collection_dailog3);
		img_close = (ImageView) findViewById(R.id.img_close);
		btnCollectionCancel = (Button) findViewById(R.id.btn_collection_cancel);
		btnCollectionPrint = (Button) findViewById(R.id.btn_collection_print);

		tv_number_authorization = (TextView) findViewById(R.id.tv_number_authorization);
		tv_agrement = (TextView) findViewById(R.id.tv_agrement);
		tv_code_agrement = (TextView) findViewById(R.id.tv_code_agrement);
		tv_ref1 = (TextView) findViewById(R.id.tv_ref1);

		tv_ref2 = (TextView) findViewById(R.id.tv_ref2);
		tv_value = (TextView) findViewById(R.id.tv_value);

		loadUI();

	}

	@SuppressLint("UseValueOf")
	private void loadUI() {

		try {
			JSONObject jsonObj = new JSONObject(resjson);
			tv_number_authorization.setText(jsonObj.getString("idTransaccion"));
			tv_agrement.setText(ccDTO.getDescripcion());
			tv_code_agrement.setText(ccDTO.getCuentaDestino());
			tv_ref1.setText(nit);
			tv_ref2.setText(paymentCode);
			tv_value.setText("$" + (new Double(Double.parseDouble(value)).longValue()));
			
			insertpayamount("" + new Double(value).longValue(), jsonObj.getString("idTransaccion"));
			

			SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
			dto.setTipo_transaction(jsonObj.getString("idTransaccion"));
			idTransaccion = jsonObj.getString("idTransaccion");
			try {
				msisdn = jsonObj.getString("msisdn");
			} catch (Exception e) {
				// TODO: handle exception
			}
			dto.setCreation_date(Dates.currentdate());
			dto.setId_pdb_servitienda("");
			dto.setModule("pago");
			dto.setAuthorization_number(cellular);
			dto.setTransaction_value(value);
			dto.setCreated_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_date(Dates.currentdate());
			dto.setPunthored_synck_status("0");
			dto.setServitienda_synck_status("0");
			dto.setStatus("true");
			dto.setTransaction_datetime(Dates.currentdate());
			dto.setRowid("");
			dto.setModule_tipo_id(ServiApplication.tipo_recaudo_cnr_id);
			List<DTO> list = new ArrayList<DTO>();
			list.add(dto);
			SincronizarTransaccionesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
			Intent intent = new Intent(context, SincronizarTransacciones.class);
			// intent.putExtra("ModuleName", "pago");
			// intent.putExtra("Module_Tipo_id", "10");
			context.startService(intent);
			horaTransaccion = jsonObj.getString("hora");
			fecha = jsonObj.getString("fecha");
			idDavivienda = jsonObj.getString("idDavivienda");

		} catch (Exception e) {
			// TODO: handle exception
		}

		img_close.setOnClickListener(this);
		btnCollectionCancel.setOnClickListener(this);
		btnCollectionPrint.setOnClickListener(this);

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
		case R.id.btn_collection_cancel:
			homeactivityLoad();
			break;

		case R.id.btn_collection_print:
			printOperation();
			break;

		case R.id.img_close:
			homeactivityLoad();
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
		//
		// if (NetworkConnectivity.netWorkAvailability(context)) {
		// new ImpresionTirilla(dto.getUserName(), dto.getPassword())
		// .execute();
		// } else {
		// CommonMethods.showCustomToast(context, context.getResources()
		// .getString(R.string.no_wifi_adhoc));
		// }
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

	public String getJsonObj() {

		try {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId());
				jsonobj.put("terminalId", dto.getTerminalId());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
				jsonobj.put("idTransaccion", idTransaccion);
				jsonobj.put("msisdn", "");
				jsonobj.put("valor", "" + ((new Double(value)).longValue()));
				jsonobj.put("productNumber", ccDTO.getProdCodigo());
				// jsonobj.put("productNumber","338");
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
		String[] valArray = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto = String.format(json.getString("encabezadoProducto"), "UTF-8");
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//String.format(json.getString("pieProducto"), "UTF-8");
			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
			numeroPin = json.getString("numeroPin");
			serialPin = json.getString("serialPin");
			valArray = Dates.printdate(fecha).split(" ");

		} catch (Exception e) {
		}

		// if (ESCPOSConst.CMP_SUCCESS == result) {
		// printer.printText(
		// context.getString(R.string.reference_de_pago_title),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_2HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_sucess),
		// ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_fecha_trans)
		// + "\t" + valArray[0],
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_hora_trans)
		// + "\t" + horaTransaccion,
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_terminal)
		// + "\t" + dto.getTerminalId(),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_convenio)
		// + "\t" + ccDTO.getDescripcion(),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_codigo_convenio)
		// + "\t" + ccDTO.getCodigoConvenio(),
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(context.getString(R.string.refer1) + "\t" + nit,
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(context.getString(R.string.refer2) + "\t"
		// + paymentCode, ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(context.getString(R.string.valor_s) + "\t\t"
		// + "$" + value, ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_codigo_autorizacion)
		// + "\t" + idTransaccion,
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(
		// context.getString(R.string.reference_de_pago_user_de_venta)
		// + "\t\t" + distribuidor,
		// ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT
		// | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);// pieProducto
		// printer.printText(idDavivienda, ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);//
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		// printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH
		// | ESCPOSConst.CMP_TXT_1HEIGHT);
		//
		// printer.printText(pieProducto, ESCPOSConst.CMP_ALIGNMENT_CENTER,
		// ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
		// ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);//
		//
		// printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
		// printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
		//
		// printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
		// result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
		// printer.disconnect();
		// homeactivityLoad();
		// } else {
		// CommonMethods.showCustomToast(context, context.getResources()
		// .getString(R.string.printer_errmsg));
		// }

		ServiApplication.allprinters_flage = true;
		ServiApplication.flage_for_log_print = false;
		ServiApplication.setUiHandler(uuiHandler);

		ServiApplication.print_flage_eps = "\n 	   " + "DAVIVIENDA  PUNTORED" + "\n\n" + " 		"
				+ context.getString(R.string.reference_de_pago_sucess) + "\n"
				+ context.getString(R.string.reference_de_pago_fecha_trans) + "     " + "\n" + valArray[0] + "\n"
				+ context.getString(R.string.reference_de_pago_hora_trans) + "      " + valArray[1] + "\n"
				+ context.getString(R.string.reference_de_pago_terminal) + "      " + dto.getTerminalId() + "\n"
				+ context.getString(R.string.reference_de_pago_convenio) + "      " + ccDTO.getDescripcion() + "\n"
				+ context.getString(R.string.reference_de_pago_codigo_convenio) + "      " + ccDTO.getCodigoConvenio()
				+ "\n" + context.getString(R.string.refer1) + "      " + nit + "\n" + context.getString(R.string.refer2)
				+ "      " + paymentCode + "\n" + context.getString(R.string.valor_s) + "      " + "$ " + value + "\n"
				+ context.getString(R.string.reference_de_pago_codigo_autorizacion) + "      " + idTransaccion + "\n"
				+ context.getString(R.string.reference_de_pago_user_de_venta) + "      " + distribuidor + "\n"
				+ " 		" + idDavivienda + "\n\n\n" + pieProducto;

		ServiApplication.print_flage = ServiApplication.print_flage_eps;
		
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			homeactivityLoad();
		} else {
			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);
		}
		
		

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
		CommonMethods.openNewActivity(context, MenuActivity.class);

	}
}
