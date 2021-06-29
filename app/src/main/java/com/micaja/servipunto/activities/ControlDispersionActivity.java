package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.ControlDispersionAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.ControlDispersionDialog4;
import com.micaja.servipunto.dialog.ControlDispersionDialog5;
import com.micaja.servipunto.dialog.Efectivo_MenuDialog;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class ControlDispersionActivity extends BaseActivity implements
		OnClickListener {
	ServiApplication appContext;
	private TextView txtacceptance_value, txtacceptancecollection,
			txtacceptance_medio, txtacceptance_date, txtreturn_value,
			txtreturn_collection, txtreturn_medio, txtreturn_date;
	private Context context;
	private Button btn_acceptancecnfrm, btn_acceptancecncl, btn_returncnfrm,
			btn_returncncl, btn_leave;
	private TextView txtProductName;
	private Intent intent;
	private SharedPreferences sharedpreferences;
	UserDetailsDTO dto;
	private List<DTO> datoCD = new ArrayList<DTO>();
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	private LinearLayout layout1;

	/* Header Text */

	private TextView products, product_profit, product_quntity,
			product_totallosers, txtAccept, txtRreject;
	private View report_view, reportview2, reportview3, reportview4, disView1,
			disView2;
	private ListView lv_ReportDetail;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, DevolucionActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.control_de_dispersion);
		txtacceptance_value = (TextView) findViewById(R.id.txt_acceptance_value);
		txtacceptancecollection = (TextView) findViewById(R.id.txt_acceptance_collection);
		txtacceptance_medio = (TextView) findViewById(R.id.txt_acceptance_medio);
		txtacceptance_date = (TextView) findViewById(R.id.txt_acceptance_date);
		txtreturn_value = (TextView) findViewById(R.id.txt_return_value);
		txtreturn_collection = (TextView) findViewById(R.id.txt_return_collection);
		txtreturn_medio = (TextView) findViewById(R.id.txt_return_medio);
		txtreturn_date = (TextView) findViewById(R.id.txt_return_date);

		btn_acceptancecnfrm = (Button) findViewById(R.id.btn_acceptance_cnfrm);
		btn_acceptancecncl = (Button) findViewById(R.id.btn_acceptance_cncl);
		btn_returncnfrm = (Button) findViewById(R.id.btn_return_cnfrm);
		btn_returncncl = (Button) findViewById(R.id.btn_return_cncl);
		btn_leave = (Button) findViewById(R.id.btn_leave);

		btn_acceptancecnfrm.setOnClickListener(this);
		btn_acceptancecncl.setOnClickListener(this);
		btn_returncnfrm.setOnClickListener(this);
		btn_returncncl.setOnClickListener(this);
		btn_leave.setOnClickListener(this);
		txtProductName = (TextView) findViewById(R.id.txt_ProductName);
		txtProductName.setText(getResources().getString(
				R.string.control_de_dispersion));

		report_view = (View) findViewById(R.id.report_view);
		reportview2 = (View) findViewById(R.id.reportview2);
		reportview3 = (View) findViewById(R.id.reportview3);
		reportview4 = (View) findViewById(R.id.reportview4);
		disView1 = (View) findViewById(R.id.dispersio_view1);
		disView2 = (View) findViewById(R.id.dispersio_view2);

		products = (TextView) findViewById(R.id.products);
		product_profit = (TextView) findViewById(R.id.product_profit);
		product_quntity = (TextView) findViewById(R.id.product_quntity);
		product_totallosers = (TextView) findViewById(R.id.product_totallosers);
		txtAccept = (TextView) findViewById(R.id.dispersio_accept);
		txtRreject = (TextView) findViewById(R.id.dispersio_reject);

		lv_ReportDetail = (ListView) findViewById(R.id.lv_ReportDetail);
		load_Data();
	}

	private void load_Data() {
		products.setText(R.string.tipo);
		product_profit.setText(R.string.valor);
		product_quntity.setText(R.string.fecha);
		product_totallosers.setText(R.string.centro_de_acopio);
		product_totallosers.setVisibility(View.VISIBLE);
		report_view.setVisibility(View.VISIBLE);
		reportview2.setVisibility(View.VISIBLE);
		reportview3.setVisibility(View.VISIBLE);
		reportview4.setVisibility(View.VISIBLE);
		disView1.setVisibility(View.VISIBLE);
		disView2.setVisibility(View.VISIBLE);

		txtAccept.setVisibility(View.VISIBLE);
		txtRreject.setVisibility(View.VISIBLE);

		controlDispersion();
	}

	private void controlDispersion() {
		if (NetworkConnectivity
				.netWorkAvailability(ControlDispersionActivity.this)) {
			new ControlDispersion(dto.getUserName(), dto.getPassword())
					.execute();
		} else {
			CommonMethods.showCustomToast(ControlDispersionActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	public void onBackPressed() {

		this.finish();
		backButtonHandler();
		return;
	}

	private void backButtonHandler() {
		Efectivo_MenuDialog dialog = new Efectivo_MenuDialog(this);
		dialog.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_acceptance_cnfrm:
			// ControlDispersionDialog1 dialog = new ControlDispersionDialog1(
			// this);
			// dialog.show();
			// dialog.setCancelable(false);

			break;
		case R.id.btn_acceptance_cncl:
			ControlDispersionDialog5 dialog7 = new ControlDispersionDialog5(
					this);
			dialog7.show();

			break;
		case R.id.btn_return_cnfrm:
			// ControlDispersionDialog3 dialog3 = new ControlDispersionDialog3(
			// this);
			// dialog3.show();
			// break;

		case R.id.btn_return_cncl:
			ControlDispersionDialog4 dialog5 = new ControlDispersionDialog4(
					this);
			dialog5.show();
			break;
		case R.id.btn_leave:
			backButtonHandler();
			finish();
		default:
			break;
		}
	}

	// ControlDispersion Service
	public class ControlDispersion extends AsyncTask<Void, Void, Boolean> {
		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;;
		String json = getJsonObj();
		private String encrypt_key, responds;

		public ControlDispersion(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					ControlDispersionActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(
								ControlDispersionActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsultarAceptaciones,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.v("varun", ht.responseDump);
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					datoCD = new ParsingHandler().getControlDispersion(
							new ParsingHandler().getString(
									new GetDocumentObject()
											.getDocumentObj(ht.responseDump),
									"response", "data"),
							ControlDispersionActivity.this);
				} else {
					responds = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
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
		protected void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(
						ControlDispersionActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(
							ControlDispersionActivity.this,
							json.getString("message"));
					moveMenuScreen();
				} catch (Exception e) {

				}
			} else {
				bindthedatatoView();
			}
		}
	}

	public String getJsonObj() {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public void bindthedatatoView() {

		lv_ReportDetail.setAdapter(new ControlDispersionAdapter(
				ControlDispersionActivity.this, datoCD));

		// for (DTO dto : datoCD) {
		// ControlDispersionDTO cdto=(ControlDispersionDTO) dto;
		//
		// }
		//
		// for (int i = 0; i < datoCD.size(); i++) {
		// ControlDispersionDTO cdto=(ControlDispersionDTO) datoCD.get(i);
		//
		// if (cdto.getTipo().equalsIgnoreCase("ACEPTACION")) {
		// txtacceptance_value .setText(cdto.getValor());
		// txtacceptancecollection .setText(cdto.getCentroacopioId());
		// txtacceptance_medio.setText(cdto.getTipo());
		// txtacceptance_date.setText(cdto.getDate());
		// }else {
		// txtreturn_value .setText(cdto.getValor());
		// txtreturn_collection .setText(cdto.getCentroacopioId());
		// txtreturn_medio .setText(cdto.getTipo());
		// txtreturn_date .setText(cdto.getDate());
		// }
		// }

	}

	public void moveMenuScreen() {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
	}

}
