package com.micaja.servipunto.dialog;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.ConventionInformationActivity;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.ConsultaConvenioDTO;
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

public class ConventionTransactionResultsDialog extends Dialog
		implements android.view.View.OnClickListener, android.content.DialogInterface.OnClickListener {

	private Context context;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 listener;
	private Intent intent;
	private Button btnPrint, btnLeave;
	private TextView convino;
	private ImageView imgClose;
	private String jsondata, idTransaccion, msisdn, productNumber, value, numeroPin, serialPin;;
	ConsultaConvenioDTO consultaConvenioDTO;
	String cnfmValue, cnfmValue1;
	public SharedPreferences sharedpreferences;
	private TextView numero_autrz, convenio, codigo_convenio, referencia1, referencia2, valor, ref2, tx_val;// if
																											// bind
																											// data
																											// use
																											// this
																											// textviews
																											// only

	private TextView txtAuthnumber, txtRegvalue, txtRegassst, colomos, txtDialogTitle,value_date;
	ESCPOSPrinter printer = new ESCPOSPrinter();
	UserDetailsDTO dto;
	UsbDevice usbdevice = null;
	int result;
	private String horaTransaccion, fecha, distribuidor, idDavivienda, paymentCode;
	
	private LinearLayout last_linearlayout_barcode_fetcha;

	public ConventionTransactionResultsDialog(Context context, String jsonformate,
			ConsultaConvenioDTO consultaConvenioDTO, String cnfmValue) {
		super(context);
		this.context = context;
		this.jsondata = jsonformate;
		this.consultaConvenioDTO = consultaConvenioDTO;

		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		distribuidor = sharedpreferences.getString("distribuidor", "");
		printer.setContext(context);
		result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
		this.cnfmValue = cnfmValue;
		try {
			cnfmValue1 = cnfmValue;
		} catch (Exception e) {
			this.cnfmValue1 = cnfmValue;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		ininUI();

	}

	private void ininUI() {
		setContentView(R.layout.recharge_dialog2);

		txtAuthnumber = (TextView) findViewById(R.id.txt_authorization);
		txtAuthnumber.setText(getContext().getString(R.string.num_auth));
		convenio = (TextView) findViewById(R.id.txtproduct);
		convenio.setText(getContext().getString(R.string.converaion));
		txtRegvalue = (TextView) findViewById(R.id.txt_regvalue);
		txtRegvalue.setText(getContext().getString(R.string.code_pay));
		txtRegassst = (TextView) findViewById(R.id.txt_regassst);
		txtRegassst.setText(getContext().getString(R.string.refer1));
		txtRegassst.setTypeface(null, Typeface.NORMAL);
		referencia1 = (TextView) findViewById(R.id.referencia1);
		ref2 = (TextView) findViewById(R.id.sale_bill);
		referencia2 = (TextView) findViewById(R.id.txt_bills);
		referencia2.setText(getContext().getString(R.string.refer2));
		valor = (TextView) findViewById(R.id.txt_values);
		valor.setText(getContext().getString(R.string.report_value));

		btnPrint = (Button) findViewById(R.id.btn_print);
		btnLeave = (Button) findViewById(R.id.btn_leave);
		btnLeave.setText(getContext().getString(R.string.leave));

		imgClose = (ImageView) findViewById(R.id.img_close);

		numero_autrz = (TextView) findViewById(R.id.auth_number);
		convino = (TextView) findViewById(R.id.number);
		tx_val = (TextView) findViewById(R.id.value);
		codigo_convenio = (TextView) findViewById(R.id.auth_value);
		codigo_convenio.setText(getContext().getString(R.string.codigo_conven));

		colomos = (TextView) findViewById(R.id.txtcoloms);
		colomos.setVisibility(View.VISIBLE);

		// txtDialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		// txtDialogTitle.setText(context.getResources().getString(R.string.convenio_dialog1));
		
		last_linearlayout_barcode_fetcha=(LinearLayout)findViewById(R.id.last_linearlayout_barcode_fetcha);
		last_linearlayout_barcode_fetcha.setVisibility(View.VISIBLE);
		
		value_date=(TextView)findViewById(R.id.value_date);
	

		imgClose.setOnClickListener(this);
		btnPrint.setOnClickListener(this);
		btnLeave.setOnClickListener(this);
		loadUI();

	}

	private void loadUI() {
		try {
			JSONObject jsonObject = new JSONObject(jsondata);
			convino.setText(consultaConvenioDTO.getDescripcion());
			codigo_convenio.setText(consultaConvenioDTO.getCodigoConvenio());
			referencia1.setVisibility(View.VISIBLE);
			referencia1.setText(consultaConvenioDTO.getRef1());
			ref2.setText(consultaConvenioDTO.getRef2());
			ref2.setVisibility(View.VISIBLE);
			tx_val.setText("$ " + cnfmValue1);
			paymentCode = consultaConvenioDTO.getRef2();
			
			value_date.setText(Dates.getMyCashBoxFechaforbar(consultaConvenioDTO.getDuedate()));
			try {
				idTransaccion = jsonObject.getString("idTransaccion");
				numero_autrz.setText(jsonObject.getString("idTransaccion"));
			} catch (Exception e) {
				idTransaccion = "";
			}

			try {
				msisdn = jsonObject.getString("msisdn");
			} catch (Exception e) {
				msisdn = "";
			}

			try {
				value = jsonObject.getString("value");
			} catch (Exception e) {
				value = "";
			}
			
			insertpayamount("" + new Double(cnfmValue1).longValue(), jsonObject.getString("idTransaccion"));

			SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
			dto.setTipo_transaction("");
			dto.setCreation_date(Dates.currentdate());
			dto.setId_pdb_servitienda("");
			dto.setModule("racaudo RBC");
			dto.setAuthorization_number(jsonObject.getString("idTransaccion"));
			dto.setTransaction_value(jsonObject.getString("valor"));
			dto.setCreated_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_by(sharedpreferences.getString("user_name", ""));
			dto.setModified_date(Dates.currentdate());
			dto.setPunthored_synck_status("0");
			dto.setServitienda_synck_status("0");
			dto.setStatus("true");
			dto.setTransaction_datetime(Dates.currentdate());
			dto.setRowid("");
			dto.setModule_tipo_id(ServiApplication.tipo_recaudo_rbc_id);
			List<DTO> list = new ArrayList<DTO>();
			list.add(dto);
			SincronizarTransaccionesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
			Intent intent = new Intent(context, SincronizarTransacciones.class);
			// intent.putExtra("ModuleName", "racaudo RBC");
			// intent.putExtra("Module_Tipo_id", "6");
			context.startService(intent);
			horaTransaccion = jsonObject.getString("hora");
			fecha = jsonObject.getString("fecha");
			idDavivienda = jsonObject.getString("idDavivienda");
		} catch (Exception e) {
		}

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_print:
			printOperation();
			break;

		case R.id.btn_leave:
			// appContext.clearActivityList();
			CommonMethods.openNewActivity(context, MenuActivity.class);
			this.dismiss();
			break;

		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	private void printOperation() {

		// new ImpresionTirilla(dto.getUserName(), dto.getPassword()).execute();

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

	public String getJsonObj() {
		try {
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("comercioId", dto.getComercioId());
				jsonobj.put("terminalId", dto.getTerminalId());
				jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
				jsonobj.put("idTransaccion", idTransaccion);
				jsonobj.put("msisdn", msisdn);
				jsonobj.put("valor", cnfmValue);
				jsonobj.put("productNumber", consultaConvenioDTO.getProdCodigo());
				return jsonobj.toString();
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}

	public void printfinalOperation(String responds) {
		Log.v("idTransaccion", "idTransaccion" + responds);
		String encabezadoProducto = null, pieProducto = null, logo = null, urlLogo, fecha = null;
		String[] valArray = null;
		try {
			JSONObject json = new JSONObject(responds);
			encabezadoProducto = CommonMethods.removeAsrics( String.format(json.getString("encabezadoProducto"), "UTF-8"));
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//String.format(json.getString("pieProducto"), "UTF-8");
			logo = json.getString("logo");
			urlLogo = json.getString("urlLogo");
			fecha = json.getString("fecha");
			numeroPin = json.getString("numeroPin");
			serialPin = json.getString("serialPin");
			valArray = Dates.printdate(fecha).split(" ");
		} catch (Exception e) {
		}

//		if (ESCPOSConst.CMP_SUCCESS == result) {
//
//			Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.print_logo);
//			printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
//			printer.printText("\n\n" + CommonMethods.utf8string(encabezadoProducto), ESCPOSConst.CMP_ALIGNMENT_CENTER,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_sucess), ESCPOSConst.CMP_ALIGNMENT_CENTER,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_2WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_fecha_trans) + "\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(valArray[0], ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_hora_trans) + "\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(valArray[1], ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_terminal) + "\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(dto.getTerminalId(), ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_convenio) + "\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(consultaConvenioDTO.getDescripcion(), ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_codigo_convenio) + "\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(consultaConvenioDTO.getCodigoConvenio(), ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.refer1) + "\t", ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(consultaConvenioDTO.getRef1(), ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_DEFAULT, ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.refer2) + "\t", ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(paymentCode, ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.valor_s) + "\t\t", ESCPOSConst.CMP_ALIGNMENT_LEFT,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("$ " + cnfmValue1, ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_codigo_autorizacion) + "\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(idTransaccion, ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(context.getString(R.string.reference_de_pago_user_de_venta) + "\t\t",
//					ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(distribuidor, ESCPOSConst.CMP_ALIGNMENT_LEFT, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);// pieProducto
//			printer.printText(idDavivienda, ESCPOSConst.CMP_ALIGNMENT_CENTER,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);//
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//			printer.printText("\n", ESCPOSConst.CMP_ALIGNMENT_CENTER, ESCPOSConst.CMP_FNT_DEFAULT,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
//
//			printer.printText(pieProducto, ESCPOSConst.CMP_ALIGNMENT_CENTER,
//					ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
//					ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);//
//
//			printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
//			printer.pageModePrint(ESCPOSConst.CMP_PM_PRINT_SAVE);
//
//			printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
//			result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
//			printer.disconnect();
//			homeactivityLoad();
//
//		} else {

			ServiApplication.allprinters_flage = true;
			ServiApplication.flage_for_log_print = true;
			ServiApplication.setUiHandler(uuiHandler);

			ServiApplication.print_flage_eps = "\n" + CommonMethods.utf8string(encabezadoProducto) + "\n\n"
					+ " 		" + context.getString(R.string.reference_de_pago_sucess) + "\n"
					+ context.getString(R.string.reference_de_pago_fecha_trans) + "     " + " " + valArray[0] + "\n"
					+ context.getString(R.string.reference_de_pago_hora_trans) + "      " + valArray[1] + "\n"
					+ context.getString(R.string.reference_de_pago_terminal) + "      " + dto.getTerminalId() + "\n"
					+ context.getString(R.string.reference_de_pago_convenio) + "      "
					+ consultaConvenioDTO.getDescripcion() + "\n"
					+ context.getString(R.string.reference_de_pago_codigo_convenio) + "      "+ consultaConvenioDTO.getCodigoConvenio() + "\n" 
					+ context.getString(R.string.refer1) + "      "+ consultaConvenioDTO.getRef1() + "\n" 
					+ context.getString(R.string.refer2) + "      " + paymentCode
					+ "\n" + context.getString(R.string.valor_s) + "      " + "$ " + cnfmValue1 + "\n"
					+ context.getString(R.string.reference_de_pago_codigo_autorizacion) + "      " + idTransaccion
					+ "\n" + context.getString(R.string.reference_de_pago_user_de_venta) + "      " + distribuidor
					+ "\n" + " 		" + idDavivienda + "\n\n\n" + pieProducto;

			ServiApplication.print_flage = ServiApplication.print_flage_eps;
			
			if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
				homeactivityLoad();
			} else {
				Intent serverIntent = new Intent(context, PRTSDKApp.class);
				serverIntent.putExtra("babu", "print");
				((Activity) context).startActivityForResult(serverIntent, 10);
			}
			
			

//		}
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
		CommonMethods.openNewActivity(context, ConventionInformationActivity.class);

	}

}
