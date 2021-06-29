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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.ControlDispersionActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ControlDispersionDTO;
import com.micaja.servipunto.database.dto.DTO;
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
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesPrint;

public class ControlDispersionDialog2 extends Dialog implements OnClickListener {

	private Context context;
	ServiApplication appContext;
	private Button btn_dispersion2leave;
	private Button btn_dispersion2print;
	private TextView txtv_dispersion;
	ControlDispersionDTO dto;
	private String server_res, fecha, hora, transactionId, value;
	private boolean click_eventtype;
	private List<DTO> dato = new ArrayList<DTO>();
	public SharedPreferences sharedpreferences;
	String key, print_message;

	public ControlDispersionDialog2(Context context, String server_res, ControlDispersionDTO dto,
			boolean click_eventtype, String key) {
		super(context);
		this.context = context;
		this.dto = dto;
		this.server_res = server_res;
		this.click_eventtype = click_eventtype;
		this.key = key;

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ServiApplication) context.getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.control_de_dispersiondialog2);
		txtv_dispersion = (TextView) findViewById(R.id.txtv_dispersiondialog2_msg);
		btn_dispersion2leave = (Button) findViewById(R.id.btn_dispersion2_leave);
		btn_dispersion2print = (Button) findViewById(R.id.btn_dispersion2_print);

		btn_dispersion2leave.setOnClickListener(this);
		btn_dispersion2print.setOnClickListener(this);
		// new ConsultarCentrosAcopio("","").execute();
		loadUi();
	}

	private void loadUi() {
		try {
			JSONObject json = new JSONObject(server_res);
			txtv_dispersion.setText(json.getString("message"));
			transactionId = json.getString("message");
			fecha = Dates.get_2bpunthodate(json.getString("fecharegistro"));
			hora = Dates.get_2bpunthohovers(json.getString("fecharegistro"));
			value = dto.getValor();
		} catch (Exception e) {
		}
		bindMessage();
	}

	private void bindMessage() {
		if ("DEVOLUCION".equalsIgnoreCase(dto.getTipo())) {

			if (click_eventtype) {
				// txtv_dispersinmssg.setText(context.getResources().getString(
				// R.string.dispers_accept_the_devolution)
				// + "$ "
				// + dto.getValor()
				// + " : "
				// + context.getResources().getString(
				// R.string.dispers_btnaccept_sureExit));
				print_message = "Devolucion de Efectivo para pagos, este comprobante indica la devolucion del efectivo en el comercio autorizado para pagos. ";
			} else {
				// txtv_dispersinmssg.setText(context.getResources().getString(
				// R.string.dispers_reject_the_devolution)
				// + "$ "
				// + dto.getValor()
				// + " : "
				// + context.getResources().getString(
				// R.string.dispers_btnaccept_sureExit));

				print_message = "Devolucion de Efectivo para pagos, este comprobante indica la devolucion del efectivo en el comercio autorizado para pagos. ";
			}

		} else {
			if (click_eventtype) {
				// txtv_dispersinmssg.setText(context.getResources().getString(
				// R.string.dispers_accept_the_acceptance)
				// + " $ "
				// + dto.getValor()
				// + " : "
				// + context.getResources().getString(
				// R.string.dispers_btnaccept_sureExit));
				print_message = "Entrega de Efectivo para pagos, este comprobante indica la aceptacion de efectivo en el comercio autorizado para pagos.";
			} else {
				// txtv_dispersinmssg.setText(context.getResources().getString(
				// R.string.dispers_reject_the_acceptance)
				// + " $ "
				// + dto.getValor()
				// + " : "
				// + context.getResources().getString(
				// R.string.dispers_btnaccept_sureExit));
				print_message = "Entrega de Efectivo para pagos, este comprobante indica la aceptacion de efectivo en el comercio autorizado para pagos. ";
			}

		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dispersion2_leave:
			this.dismiss();
			movetoControlDispersionactivity();
			break;
		case R.id.btn_dispersion2_print:
			printOperation();
			break;

		default:
			break;
		}

	}

	private void movetoControlDispersionactivity() {
		this.dismiss();
		appContext.clearActivityList();
		CommonMethods.openNewActivity(context, ControlDispersionActivity.class);
	}

	private void printOperation() {

		ServiApplication.himp_print_flage = false;
		ServiApplication.flage_for_log_print = false;
		ServiApplication.setUiHandler(uuiHandler);
		SharedPreferences sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES,
				Context.MODE_PRIVATE);
		// ServiApplication.print_flage_eps =
		// context.getResources().getString(R.string.twob_line1) + "\n"
		// + context.getResources().getString(R.string.twob_line2) + "\n\n"
		// + context.getResources().getString(R.string.twob_line3) + "\n"
		// + context.getResources().getString(R.string.twob_line4) + " "
		// + sharedpreferences.getString("name_of_store", "") + "\n"
		// + context.getResources().getString(R.string.valor) + " $ "
		// + CommonMethods.getNumSeparator(Double.parseDouble(value)) + "\n"
		// + context.getResources().getString(R.string.accp_fechatrans) + " " +
		// fecha + "\n"
		// + context.getResources().getString(R.string.accp_horatrans) + " " +
		// hora + "\n"
		// + context.getResources().getString(R.string.twob_line8) + "\n" +
		// transactionId + "\n"
		// +
		// context.getResources().getString(R.string.twob_line10)+""+sharedpreferences.getString("distribuidor",
		// "")+"\n\n\n";//print_message

		ServiApplication.print_flage_eps = context.getResources().getString(R.string.twob_line1) + "\n"
				+ context.getResources().getString(R.string.twob_line2) + "\n\n"
				+ context.getResources().getString(R.string.twob_line3) + "\n"
				+ context.getResources().getString(R.string.twob_line4) + "   "
				+ sharedpreferences.getString("name_of_store", "") + "\n"
				+ context.getResources().getString(R.string.valor) + "      $ "
				+ CommonMethods.getNumSeparator(Double.parseDouble(value)) + "\n"
				+ context.getResources().getString(R.string.accp_fechatrans) + "      " + fecha + "\n"
				+ context.getResources().getString(R.string.accp_horatrans) + "       " + hora + "\n" + print_message
				+ "\n" + transactionId + "\n" + context.getResources().getString(R.string.twob_line10) + " "
				+ dto.getCentroacopioId() + "\n\n\n";
		Log.v("varahalababu", ServiApplication.print_flage_eps);

		ServiApplication.print_flage = ServiApplication.print_flage_eps;

		if (new SalesPrint(context, ServiApplication.print_flage_eps).print()) {
			movetoControlDispersionactivity();
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
				movetoControlDispersionactivity();
			} else {
			}
		}
	};

	public class ConsultarCentrosAcopio extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, json_responds;

		public ConsultarCentrosAcopio(String email, String password) {
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
						ServiApplication.process_id_ConsultarCentrosAcopio, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					dato = new ParsingHandler().getConsultarCentrosAcopioList(new ParsingHandler().getString(
							new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"), context);
				} else {
					json_responds = new ParsingHandler()
							.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data");
					exception1 = true;
				}
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
					JSONObject json = new JSONObject(json_responds);
					CommonMethods.showCustomToast(context, json.getString("message"));
				} catch (Exception e) {
				}
			} else {
				try {

				} catch (Exception e) {

				}
			}

		}

	}

	private String getJsonObj() {
		JSONObject jsonobj = new JSONObject();
		UserDetailsDTO dto1 = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		try {
			jsonobj.put("idCentroAcopio", dto.getCentroacopioId());
			jsonobj.put("clave", key);
			jsonobj.put("puntoDeVentaId", dto1.getPuntoredId());
			jsonobj.put("comercioId", dto1.getComercioId());
			jsonobj.put("terminalId", dto1.getTerminalId());
			jsonobj.put("valor", "" + (Math.round((Double.parseDouble(dto.getValor()))) * 100));
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
