package com.micaja.servipunto.dialog;

import java.net.Proxy;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.DaviPlataPaymentActivity;
import com.micaja.servipunto.activities.DepositoDaviplataActivity;
import com.micaja.servipunto.adapters.MenuGriedAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ConsultarFacturasDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;

public class PointsDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context context;
	private Button btnRecharge, btnPaquetig;
	private Intent intent;
	private ImageView imgClose;
	private GridView gridView_points;
	private String[] module_names;
	public Integer[] module_drawable;
	private boolean flage;
	private ServiApplication appContext;
	public SharedPreferences sharedpreferences;
	private UserDetailsDTO dto;

	public PointsDialog(Context context, boolean b) {
		super(context);
		this.context = context;
		this.flage = b;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}
	private void initUI() {
		setContentView(R.layout.points_dialog);
		gridView_points = (GridView) findViewById(R.id.gridView_points);
		imgClose = (ImageView) findViewById(R.id.img_close);
		if (flage) {
			module_names = new String[] { context.getResources().getString(
					R.string.DaviPlata_Payment),context.getResources().getString(
							R.string.supp_payment)  };
			module_drawable = new Integer[] { R.drawable.new_recharge_bg,R.drawable.new_pagos_bg };
			gridView_points.setNumColumns(2);
		} else {
			module_names = new String[] {
//					 context.getResources().getString(R.string.menus),
					context.getResources().getString(R.string.giros),
					context.getResources().getString(R.string.daviplata_header)};

			module_drawable = new Integer[] {
//					 R.drawable.new_sales_bg,
					 R.drawable.new_twits_bg, R.drawable.new_trainig_bg};
		}

		imgClose.setOnClickListener(this);

		gridView_points.setAdapter(new MenuGriedAdapter(context, module_names,
				module_drawable, false));

		gridView_points.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (flage) {
					switch (position) {
					case 0:
						appContext.pushActivity(intent);
						intent = new Intent(context,
								DaviPlataPaymentActivity.class);
						context.startActivity(intent);
						break;
					case 1:
//						appContext.pushActivity(intent);
//						intent = new Intent(context,
//								PagoPaymentActivity.class);
//						context.startActivity(intent);
//						dismiss();
						
						CommonMethods.showCustomToast(context, context.getResources().getString(R.string.user_module_msg));
						
						break;
					default:
						break;
					}
				} else {
					switch (position) {
//					 case 0:
//					 appContext.pushActivity(intent);
//					 intent = new Intent(context, MenusActivity.class);
//					 context.startActivity(intent);
//					 break;
					case 0:
//						appContext.pushActivity(intent);
//						intent = new Intent(context, LotterySalesActivity.class);
//						context.startActivity(intent);
						CommonMethods.showCustomToast(context, context.getResources().getString(R.string.user_module_msg));
						
						break;
					case 1:
						appContext.pushActivity(intent);
						intent = new Intent(context,
								DepositoDaviplataActivity.class);
						context.startActivity(intent);
						break;
//					case 2:
//						Efectivo_MenuDialog dialog = new Efectivo_MenuDialog(
//								context);
//						dialog.show();
//						dialog.setCancelable(false);
//						dissmissDialog();
//						break;
//					case 3:
//						if (NetworkConnectivity.netWorkAvailability(context)) {
//							new CalRegPagoWebservice1().execute();
//						} else {
//							CommonMethods.showCustomToast(context, context.getResources()
//									.getString(R.string.no_wifi_adhoc));
//						}
//						break;
					default:
						break;
					}

				}
			}

		});
	}
	
	
	
	/* call microseguros */
	private class CalRegPagoWebservice1 extends AsyncTask<Void, Void, Void> {

		private static final String SOAP_ACTION = "";
		private boolean flage = false, exception = false;
		private String microwInsurenceAmount_value;
		private String json, jsondata;
		private String encrypt_key;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait),
					context);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjforRgb1(), key);
			// json=getJsonObjforRgb1();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(context, encrypt_key,
								ServiApplication.process_id_Consulta_factura,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					jsondata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
				} else {
					jsondata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
				}
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return null;
			}
			return null;
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
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();

			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(context);
				dialog.show();
				dialog.setCancelable(false);
			} else {
				Log.v("varahalababu", "varahlababu" + jsondata);
				JSONObject json;
				try {
					json = new JSONObject(AESTEST.AESDecrypt(jsondata, AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", ""))));
					if (json.getBoolean("estado")) {
						ConsultarFacturasDTO dto = new ConsultarFacturasDTO();
						dto.setAbonos(json.getLong("abonos"));
						dto.setAceptaciones(json.getLong("aceptaciones"));
						dto.setComercioId(dto.getComercioId());
						dto.setComisiones(json.getLong("comisiones"));
						dto.setCreditos(json.getLong("creditos"));
						dto.setDebitos(json.getLong("debitos"));
						dto.setDevoluciones(json.getLong("devoluciones"));
						dto.setIdFactura(json.getString("idFactura"));
						dto.setOtrosCargos(json.getLong("otrosCargos"));
						dto.setPagos(json.getLong("pagos"));
						dto.setSaldoInicial(json.getLong("saldoInicial"));
						dto.setSaldoTotal(json.getLong("saldoTotal"));
						dto.setTotalCorte(json.getLong("totalCorte"));
						dto.setValorCausado(json.getLong("valorCausado"));
						dto.setVentas(json.getDouble("ventas"));
						ConsultarfacturasDilog dialog = new ConsultarfacturasDilog(context, dto);
						dialog.show();
						dialog.setCancelable(false);
						dismiss();
					} else {
						OopsErrorDialogWithParam dialog = new OopsErrorDialogWithParam(
								context,
								"No tiene Facturas pendientes de pago");
						dialog.show();
						dialog.setCancelable(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void dissmissDialog() {
		this.dismiss();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.btn_recharge:
		// intent = new Intent(context, RechargeActivity.class);
		// context.startActivity(intent);
		//
		// break;
		//
		// case R.id.btn_paquetigos:
		// intent = new Intent(context, PaquetigosActivity.class);
		// context.startActivity(intent);
		// break;
		case R.id.img_close:
			this.dismiss();

			break;

		default:
			break;
		}
	}
	public String getJsonObjforRgb1() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}
}
