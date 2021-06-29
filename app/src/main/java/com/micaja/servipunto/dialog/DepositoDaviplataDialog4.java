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
import com.micaja.servipunto.activities.DepositoDaviplataActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.ComisionDTO;
import com.micaja.servipunto.database.dto.ConsultarCiudadesDTO;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
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

public class DepositoDaviplataDialog4 extends Dialog implements android.view.View.OnClickListener {

	private Button btnPrint, btnLeave;
	private ImageView imgClose;
	private Context context;
	private String cnfrmDaviplatatxt, Number_daviplata, json_responds;
	public DaviplataNumberDeDocumento documetn;
	public ConsultarCiudadesDTO ccDTO;
	public AcceptEfectivoDTO aedto;

	private SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	public ComisionDTO Comision_DTO;
	ServiApplication appContext;

	private TextView cnfrm_daviplata_text3, cnfrm_daviplata_text4, ccnfrm_daviplata_text5, cnfrm_daviplata_text6,
			cnfrm_daviplata_text7, cnfrm_daviplata_text8, cnfrm_daviplata_text9, cnfrm_daviplata_text10,
			cnfrm_daviplata_text11, cnfrm_daviplata_text12, cnfrm_daviplata_text13, cnfrm_daviplata_text14,
			cnfrm_daviplata_textView1, cnfrm_daviplata_textView2;
	private String idTransaccion, mmsisdn;

	public DepositoDaviplataDialog4(Context context, DaviplataNumberDeDocumento documetn, ConsultarCiudadesDTO ccDTO,
			String Number_daviplata, ComisionDTO comision_DTO, AcceptEfectivoDTO aedto, String json_responds) {
		super(context);
		this.context = context;
		this.Number_daviplata = Number_daviplata;
		this.Comision_DTO = comision_DTO;
		this.documetn = documetn;
		this.ccDTO = ccDTO;
		this.aedto = aedto;
		this.json_responds = json_responds;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	private void initUI() {
		setContentView(R.layout.deposito_daviplata_confirm2);
		btnPrint = (Button) findViewById(R.id.btn_print);
		btnLeave = (Button) findViewById(R.id.btn_leave);
		imgClose = (ImageView) findViewById(R.id.img_close);

		cnfrm_daviplata_textView1 = (TextView) findViewById(R.id.cnfrm_daviplata_textView1);
		cnfrm_daviplata_textView2 = (TextView) findViewById(R.id.cnfrm_daviplata_textView2);
		cnfrm_daviplata_text3 = (TextView) findViewById(R.id.cnfrm_daviplata_textView3);
		cnfrm_daviplata_text4 = (TextView) findViewById(R.id.cnfrm_daviplata_textView4);

		ccnfrm_daviplata_text5 = (TextView) findViewById(R.id.cnfrm_daviplata_textView5);
		cnfrm_daviplata_text6 = (TextView) findViewById(R.id.cnfrm_daviplata_textView6);

		cnfrm_daviplata_text7 = (TextView) findViewById(R.id.cnfrm_daviplata_textView7);

		cnfrm_daviplata_text8 = (TextView) findViewById(R.id.cnfrm_daviplata_textView8);
		cnfrm_daviplata_text9 = (TextView) findViewById(R.id.cnfrm_daviplata_textView9);

		cnfrm_daviplata_text10 = (TextView) findViewById(R.id.cnfrm_daviplata_textView10);
		cnfrm_daviplata_text11 = (TextView) findViewById(R.id.cnfrm_daviplata_textView11);

		cnfrm_daviplata_text12 = (TextView) findViewById(R.id.cnfrm_daviplata_textView12);
		cnfrm_daviplata_text13 = (TextView) findViewById(R.id.cnfrm_daviplata_textView13);
		cnfrm_daviplata_text14 = (TextView) findViewById(R.id.cnfrm_daviplata_textView14);

		btnPrint.setOnClickListener(this);
		btnLeave.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		loadUI();

	}

	private void loadUI() {

		cnfrm_daviplata_text3.setText(documetn.getNombres());
		cnfrm_daviplata_text4.setText(documetn.getDocumento());
		ccnfrm_daviplata_text5.setText(documetn.getCelular());
		cnfrm_daviplata_text6.setText(documetn.getDireccion());
		cnfrm_daviplata_text7.setText(documetn.getCiudad());

		cnfrm_daviplata_text8.setText(Comision_DTO.getNombre());
		cnfrm_daviplata_text10.setText(Number_daviplata);
		cnfrm_daviplata_text11.setText("");// puntos_sugeridos

		cnfrm_daviplata_text12.setText(Comision_DTO.getValue());
		cnfrm_daviplata_text13.setText(Comision_DTO.getComision());
		cnfrm_daviplata_text14.setText(Comision_DTO.getTotal());

		cnfrm_daviplata_text9.setText(ccDTO.getDescripcion());

		try {
			JSONObject json = new JSONObject(json_responds);
			cnfrm_daviplata_textView1.setText("" + json.getLong("idTransaccion"));
			cnfrm_daviplata_textView2.setText("" + json.getLong("idDavivienda"));
			idTransaccion = "" + json.getLong("idTransaccion");
			mmsisdn = "" + json.getLong("idDavivienda");
		} catch (Exception e) {

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_print:
			printMethod();
			break;
		case R.id.btn_leave:
			this.dismiss();
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, DepositoDaviplataActivity.class);
			break;
		case R.id.img_close:
			this.dismiss();
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, DepositoDaviplataActivity.class);
			break;

		default:
			break;
		}

	}

	private void printMethod() {
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new DepositoDaviplataMicroseguros(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	public class DepositoDaviplataMicroseguros extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getVentaMicroseguros();

		private String encrypt_key, responds;

		public DepositoDaviplataMicroseguros(String email, String password) {
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
				printfinalOperation2(responds);
			}
		}
	}

	public void printfinalOperation2(String responds) {
		ServiApplication.himp_print_flage = false;
		ServiApplication.flage_for_log_print = false;
		ServiApplication.setUiHandler(uuiHandler);
		SharedPreferences sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES,
				Context.MODE_PRIVATE);
		String trans = null, fecha = null, hora = null, pieProducto;
		String[] words = ccDTO.getDescripcion().split(",");

		try {
			JSONObject json = new JSONObject(responds);
			trans = CommonMethods.removeAsrics(json.getString("encabezadoProducto"));
			pieProducto = CommonMethods.removeAsrics(String.format(json.getString("pieProducto"), "UTF-8"));//json.getString("pieProducto");
			fecha = Dates.get_2bpunthodate(json.getString("fecha"));
			hora = Dates.get_2bpunthohovers(json.getString("fecha"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		ServiApplication.print_flage_eps = "\nDAVIVIENDA" + "\n" + "PUNTORED" + "\n\n" + trans + "\n\n"
				+ context.getResources().getString(R.string.accp_fechatrans) + fecha + "\n"
				+ context.getResources().getString(R.string.accp_horatrans) + hora + "\n"
				+ context.getResources().getString(R.string.reference_de_pago_terminal) + "    " + dto.getTerminalId()
				+ "\n\n" + "DEPOSITANTE" + "\n\n" + context.getResources().getString(R.string.nombre) + "    "
				+ documetn.getNombres() + "\n" + context.getResources().getString(R.string.nombre) + "    "
				+ documetn.getDocumento() + "\n" + "DESTINO" + "\n\n" + "Departamento" + "    " + words[0] + "\n"
				+ "Ciudad/Municipio" + "    " + words[1] + "\n" + "Punto sugerido" + "\n" + "Daviplata" + "    "
				+ Number_daviplata + "\n" + "Valor giro" + "    " + Comision_DTO.getValue() + "\n" + "Costo giro"
				+ "    " + Comision_DTO.getComision() + "\n" + "Total" + "    " + Comision_DTO.getTotal() + "\n"
				+ "Código de autorización" + idTransaccion + "\n" + "Usuario de venta" + "    "
				+ sharedpreferences.getString("name_of_store", "");

		ServiApplication.print_flage = ServiApplication.print_flage_eps;
		
		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			dismiss();
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, DepositoDaviplataActivity.class);
		} else {
			Intent serverIntent = new Intent(context, PRTSDKApp.class);
			serverIntent.putExtra("babu", "print");
			((Activity) context).startActivityForResult(serverIntent, 10);
		}
		
		
	}

	public String getVentaMicroseguros() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId().trim());
			jsonobj.put("terminalId", dto.getTerminalId().trim());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId().trim());
			jsonobj.put("idTransaccion", idTransaccion);
			jsonobj.put("msisdn", Number_daviplata);
			jsonobj.put("valor", "" + ((new Double(Comision_DTO.getValue())).longValue() * 100));
			jsonobj.put("productNumber", "341");
		} catch (Exception e) {
			return null;
		}
		return jsonobj.toString();
	}

	private final Handler uuiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.SUCESS_PRINT.code()) {
				dismiss();
				appContext.clearActivityList();
				CommonMethods.openNewActivity(context, DepositoDaviplataActivity.class);
			} else {
			}
		}
	};
}
