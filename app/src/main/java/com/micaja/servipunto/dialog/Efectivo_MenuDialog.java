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
import android.widget.Button;
import android.widget.ImageView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.AceptacionActivity;
import com.micaja.servipunto.activities.CentrosdeAcopioActivity;
import com.micaja.servipunto.activities.ControlDispersionActivity;
import com.micaja.servipunto.activities.DevolucionActivity;
import com.micaja.servipunto.activities.RegistrodeEfectivoActivity;
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
import com.micaja.servipunto.utils.NetworkConnectivity;

public class Efectivo_MenuDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context context;
	private Context context1;
	private Button btn_Centros_de_Acopio, Btn_Aceptacion,
			Control_de_dispersion, btn_registro, btn_Devolucion, btn_abono;
	private Intent intent;
	private ImageView imgClose;
	public SharedPreferences sharedpreferences;
	private UserDetailsDTO dto;
	private ServiApplication appContext;

	public Efectivo_MenuDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.modulo_efectivo);
		btn_Centros_de_Acopio = (Button) findViewById(R.id.btn_Centros_de_Acopio);
		Btn_Aceptacion = (Button) findViewById(R.id.Btn_Aceptacion);
		btn_registro = (Button) findViewById(R.id.btn_registro);
		btn_Devolucion = (Button) findViewById(R.id.btn_Devolucion);
		Control_de_dispersion = (Button) findViewById(R.id.Control_de_dispersion);
		btn_abono = (Button) findViewById(R.id.btn_abono);
		btn_abono.setOnClickListener(this);
		btn_Centros_de_Acopio.setOnClickListener(this);
		Btn_Aceptacion.setOnClickListener(this);
		btn_registro.setOnClickListener(this);
		btn_Devolucion.setOnClickListener(this);
		Control_de_dispersion.setOnClickListener(this);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgClose.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Centros_de_Acopio:
			appContext.pushActivity(intent);
			intent = new Intent(context, CentrosdeAcopioActivity.class);
			context.startActivity(intent);
//			 CommonMethods.showCustomToast(context,
//			 context.getResources().getString(R.string.user_module_msg));
			break;
		case R.id.Btn_Aceptacion:
			appContext.pushActivity(intent);
			intent = new Intent(context, AceptacionActivity.class);
			context.startActivity(intent);
//			 CommonMethods.showCustomToast(context,
//			 context.getResources().getString(R.string.user_module_msg));
			break;
		case R.id.btn_Devolucion:
			appContext.pushActivity(intent);
			intent = new Intent(context, DevolucionActivity.class);
			context.startActivity(intent);
//			 CommonMethods.showCustomToast(context,
//			 context.getResources().getString(R.string.user_module_msg));
			break;
		case R.id.btn_registro:
			appContext.pushActivity(intent);
			intent = new Intent(context, RegistrodeEfectivoActivity.class);
			context.startActivity(intent);
//			 CommonMethods.showCustomToast(context,
//			 context.getResources().getString(R.string.user_module_msg));
			break;
		case R.id.Control_de_dispersion:
			appContext.pushActivity(intent);
			intent = new Intent(context, ControlDispersionActivity.class);
			context.startActivity(intent);
//			 CommonMethods.showCustomToast(context,
//			 context.getResources().getString(R.string.user_module_msg));
			break;
		case R.id.img_close:
			this.dismiss();
			break;
		case R.id.btn_abono:
			if (NetworkConnectivity.netWorkAvailability(context)) {
				new CalRegPagoWebservice1().execute();
			} else {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.no_wifi_adhoc));
			}
			break;
		default:
			break;
		}

	}

	private class CalRegPagoWebservice1 extends AsyncTask<Void, Void, Void> {

		private static final String SOAP_ACTION = "";
		private boolean flage = false, exception = false, exception1 = false;
		private String microwInsurenceAmount_value;
		private String json, jsondata;
		private String encrypt_key;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
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
					exception1 = true;

				}
				Log.d("DUMP RESPONSE", AESTEST.AESDecrypt(
						jsondata, AESTEST
						.AESkeyFromString(sharedpreferences
								.getString("AutoGenKey", ""))));
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
					ServiApplication.SOAP_URL, 60000);
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
			} else if (exception1) {
				try {
					JSONObject json = new JSONObject(AESTEST.AESDecrypt(
							jsondata, AESTEST
									.AESkeyFromString(sharedpreferences
											.getString("AutoGenKey", ""))));
					// CommonMethods.showCustomToast(context,
					// json.getString("message"));
					ConsultarFacturasDTO dto = new ConsultarFacturasDTO();
					PaymentInAdvance dialog = new PaymentInAdvance(context,
							dto, json.getString("message"));
					dialog.show();
					dialog.setCancelable(false);
					dissmissDialog();

				} catch (Exception e) {
					appContext.pushActivity(intent);
					OopsErrorDialog dialog = new OopsErrorDialog(context);
					dialog.show();
					dialog.setCancelable(false);
				}
			} else {
				Log.v("varahalababu", "varahlababu" + jsondata);
				JSONObject json;
				try {
					json = new JSONObject(AESTEST.AESDecrypt(jsondata, AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", ""))));
					if (json.getBoolean("estado")) {
						System.out.println(json);
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
						ConsultarfacturasDilog dialog = new ConsultarfacturasDilog(
								context, dto);
						dialog.show();
						dialog.setCancelable(false);
						dismiss();
						dissmissDialog();
					} else {
						try {
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

							PaymentInAdvance dialog = new PaymentInAdvance(
									context, dto, json.getString("message"));
							dialog.show();
							dialog.setCancelable(false);
							dissmissDialog();
							// try {
							// CommonMethods.showCustomToast(context,
							// json.getString("message"));
							// } catch (Exception e) {
							// // TODO: handle exception
							// }
						} catch (Exception e) {
							CommonMethods.showCustomToast(context,
									json.getString("message"));
						}
					}
				} catch (JSONException e) {
					OopsErrorDialogWithParam dialog = new OopsErrorDialogWithParam(
							context, "No tiene Facturas pendientes de pago");
					dialog.show();
					dialog.setCancelable(false);
					dissmissDialog();
				}
			}

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

	private void dissmissDialog() {
		this.dismiss();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
	}

}
