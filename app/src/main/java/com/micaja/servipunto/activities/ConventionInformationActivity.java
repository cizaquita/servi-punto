package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

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
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ConsultaConvenioDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.ConventionConfirmDetailsDialog;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class ConventionInformationActivity extends BaseActivity implements OnClickListener {

	ServiApplication appContext;
	public SharedPreferences sharedpreferences;
	private Intent intent;
	private Button btnEndSaleSave, btnEndSaleCancel, btn_confirm_print;
	private TextView txtConvenio, txtRefer1, txtRefer2, txtValue, txtDate;

	private TextView etxtPortugos, etxtRefer1, etxtRefer2, etxtValue, etxtDate,txt_refer1,txt_refer2;
	private boolean isValid;
	private static boolean devided2flage = false, thread_flage = false;;
	private String portugos, refer1, refer2, value, date, receiced_barcode,responds;
	private LinearLayout lt_conve_barcode_layout, lt_conve_details_layout;
	private EditText ext_capture_bar;
	UserDetailsDTO dto;
	final Handler handler = new Handler();
	static String companyname = "", ref1 = "", ref2 = "", value1 = "", duedate = "", temp, temp_for_fetcha,
			space = "	",nombreReferencia1,nombreReferencia2;
	static boolean flage = false;
	static int temp_bar = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, ConventionInformationActivity.class);
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		inItUI();
	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.conventioninfo_activity);
		txtConvenio = (TextView) findViewById(R.id.txt_convenio);
		txtRefer1 = (TextView) findViewById(R.id.txt_refer1);
		txtRefer2 = (TextView) findViewById(R.id.txt_refer2);
		txtValue = (TextView) findViewById(R.id.txt_value);
		txtDate = (TextView) findViewById(R.id.txt_date);

		etxtPortugos = (TextView) findViewById(R.id.etxt_portugos);
		etxtRefer1 = (TextView) findViewById(R.id.etxt_refer1);
		etxtRefer2 = (TextView) findViewById(R.id.etxt_refer2);
		etxtValue = (TextView) findViewById(R.id.etxt_value);
		etxtDate = (TextView) findViewById(R.id.etxt_date);

		btnEndSaleSave = (Button) findViewById(R.id.btn_EndSaleSave);
		btnEndSaleCancel = (Button) findViewById(R.id.btn_EndSaleCancel);

		btn_confirm_print = (Button) findViewById(R.id.btn_confirm_print);
		btn_confirm_print.setOnClickListener(this);
		btn_confirm_print.setVisibility(View.GONE);
		btnEndSaleCancel.setOnClickListener(this);
		btnEndSaleSave.setOnClickListener(this);
		lt_conve_barcode_layout = (LinearLayout) findViewById(R.id.lt_conve_barcode_layout);
		lt_conve_details_layout = (LinearLayout) findViewById(R.id.lt_conve_details_layout);
		ext_capture_bar = (EditText) findViewById(R.id.ext_capture_bar);
		ext_capture_bar.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					consultaConvenio();
				}
				return false;
			}
		});

		// 4157709998002319802003988581312000076688220802003988581312000076688220
		// 4157707209914253802003988581312000076688220802076567890987654234543234
		// 41577072099142538020011416225441461525380200114162254414615253390000000000033160
		// 41577099980023198020600000000026529472802000000000019620160323
		// 415770720991425380200114162254414615253802012345678904146152533900000000000331609620160124
		// 4157709998002319802003988581312000076688220802076567890987654234543234390206780155
		// 415770999800231980200398858131200045726843380206628984882994843902017801339620160114
		// 415770720991425380208666544664466466785
		// 415770999800231980209854533366443900000000154
		// 4157709998002319802003988581312000457268433390206780155
		// 4157707209914253802086665446644664667859620160213
		// 41577099980023198020985453336644390000000340549620160101
		// 41577099980023198020985453336644390200021340549620160201
		
//		ext_capture_bar.setText("415770720991425380200114162254414615253802012345678904146152533900000000000331609620160124");
//		ext_capture_bar.setText("415)7709998828568(8020)1130001895348105(3900)0000017300(96)20141027");
//		consultaConvenio();
		
	}

	private void consultaConvenio1() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2500);
				} catch (Exception e) {
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (thread_flage) {
								if (ext_capture_bar.getText().toString().length() > 5) {

									if (temp_bar == ext_capture_bar.getText().toString().length()) {
										thread_flage = false;
										consultaConvenio();
									} else {
										temp_bar = ext_capture_bar.getText().toString().length();
									}

								}
							}
						} catch (Exception e) {

						}
					}
				});
			}
		}).start();

	}

	protected void consultaConvenio() {
		// ext_capture_bar.setText("4157707209914253802001160396514002663133*390000000000024100");
		receiced_barcode = ext_capture_bar.getText().toString();
		if (NetworkConnectivity.netWorkAvailability(ConventionInformationActivity.this)) {
			new EjemploConsultaConvenio(dto.getUserName(), dto.getPassword()).execute();
		} else {
			CommonMethods.showCustomToast(ConventionInformationActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	private void loadUI() {

		ConsultaConvenioDTO dto = ServiApplication.getCcdto();
		// etxtPortugos.setText(dto.getDescripcion());
		// etxtRefer1.setText(dto.getReferencia1());
		// etxtRefer2.setText(dto.getReferencia2());
		// etxtValue.setText(dto.getValor());
		// etxtDate.setText(dto.getFecha());
		
		try {
			value1=value1.replaceFirst("^0+(?!$)", "");
		} catch (Exception e) {
			
		}
		
		if (duedate.length()==0) {
			duedate=Dates.currentdate2forbarcodecollection();
		}
		
		dto.setCompanyname(companyname);
		dto.setRef1(ref1);
		dto.setRef2(ref2);
		dto.setValue1(value1);
		dto.setDuedate(duedate);
		ServiApplication.setCcdto(dto);

		etxtPortugos.setText(companyname);
		etxtRefer1.setText(ref1);
		etxtRefer2.setText(ref2);
		etxtValue.setText(value1);
		etxtDate.setText(Dates.getMyCashBoxFechaforbar(duedate));

		System.out.println(companyname + "============" + ref1 + "============" + ref2 + "===============" + value1
				+ "=============" + duedate);
		
		txt_refer1=(TextView)findViewById(R.id.txt_refer1);
		txt_refer2=(TextView)findViewById(R.id.txt_refer2);
		
		try {
			JSONObject json=new JSONObject(responds);
			txt_refer1.setText(CommonMethods.firstLetterasUppercase(json.getString("nombreReferencia1").trim()));
			txt_refer2.setText(CommonMethods.firstLetterasUppercase(json.getString("nombreReferencia2").trim()));
			
			nombreReferencia1=CommonMethods.firstLetterasUppercase(json.getString("nombreReferencia1").trim());
			nombreReferencia2=CommonMethods.firstLetterasUppercase(json.getString("nombreReferencia2").trim());
		} catch (Exception e) {
			txt_refer1.setText("");
			txt_refer2.setText("");
		}
		
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_confirm_print:
			consultaConvenio();
			break;

		case R.id.btn_EndSaleSave:
			validateAndSave();
			break;
		case R.id.btn_EndSaleCancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);
			finish();

			break;
		default:
			break;
		}

	}

	// move to ConventionConfirmDetails Dialog screen
	private void validateAndSave() {
		ConventionConfirmDetailsDialog dialog = new ConventionConfirmDetailsDialog(this, ServiApplication.getCcdto(),nombreReferencia1,nombreReferencia2);
		dialog.show();
		dialog.setCancelable(false);
	}

	@SuppressWarnings("unused")
	private boolean validateFields(StringBuffer stringBuffer) {
		isValid = true;
		portugos = etxtPortugos.getText().toString().trim();
		refer1 = etxtRefer1.getText().toString().trim();
		refer2 = etxtRefer2.getText().toString().trim();
		value = etxtValue.getText().toString().trim();
		date = etxtDate.getText().toString().trim();

		if (portugos.length() == 0) {
			isValid = false;

		}
		return isValid;
	}

	// Ejemplo consultaConvenio
	public class EjemploConsultaConvenio extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		String json;
		private String encrypt_key;

		public EjemploConsultaConvenio(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), ConventionInformationActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""));
			Log.v("varahalababu", "varahalababu JSON" + getJsonObj());
			json = AESTEST.AESCrypt(getJsonObj(), key);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader
						.makepropertyInfo1(MakeHeader.makeHeader(ConventionInformationActivity.this, encrypt_key,
								ServiApplication.process_id_get_consultaConvenio, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					responds = AESTEST.AESDecrypt(
							new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));
					ServiApplication.setCcdto(new ParsingHandler().getConsultaConvenioDTOData(AESTEST.AESDecrypt(
							new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")))));
					Log.v("varahalababu", "varahalababu" + responds);
				} else {
					Log.v("varahalababu",
							"varahalababu" + AESTEST.AESDecrypt(new ParsingHandler().getString(
									new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));

					responds = AESTEST.AESDecrypt(
							new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
									"response", "data"),
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", "")));
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
			Log.v("varahalababu", "varun"+responds);
			if (exception) {
				appContext.pushActivity(intent);
				ext_capture_bar.setText(null);
				OopsErrorDialog dialog = new OopsErrorDialog(ConventionInformationActivity.this);
				dialog.show();
			} else if (exception1) {

				try {
					JSONObject json = new JSONObject(responds);
					CommonMethods.showCustomToast(ConventionInformationActivity.this, json.getString("message"));
					CommonMethods.openNewActivity(ConventionInformationActivity.this, MenuActivity.class);
				} catch (Exception e) {

				}

			} else {
				lt_conve_barcode_layout.setVisibility(View.GONE);
				lt_conve_details_layout.setVisibility(View.VISIBLE);
				ext_capture_bar.setText("");
				loadUI();
			}
		}

	}

	public String getJsonObj() {
		// getTheValuesFromBarcodeNew();
		
		companyname="";
		ref1="";
		ref2="";
		value1="";
		duedate="";
		flage = false;
		if (receiced_barcode.replace("*", "").subSequence(0, 3).equals("415")) {
			companyname = receiced_barcode.replace("*", "").substring(3, 16).trim();
			if (receiced_barcode.replace("*", "").length() > 16) {
				getTheValuesFromBarcode(receiced_barcode.replace("*", ""));
			} else {
				bindoutputvalues();
			}
		} else {
			companyname = receiced_barcode.replace("*", "").trim();
		}

		String values = companyname + "\t" + ref1 + "\t" + ref2 + "\t" + value1 + "\t" + duedate;
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("codigoConvenio", values.trim());
			jsonobj.put("barCode", values.trim());
			jsonobj.put("codigoConvenio", values);
			jsonobj.put("barCode", values);
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	private String getCompanyName(String trim) {
		try {
			trim = trim.substring(3, 16);
		} catch (Exception e) {
		}
		return trim;
	}

	private static void getTheValuesFromBarcode(String string) {
		final String BFBSC = string.replace("*", "");
		boolean ref2_flage=true,ref1_flage=true;
		if (BFBSC.substring(16, 20).equals("8020")) {
			temp = BFBSC.substring(20);
		} else {
			temp = BFBSC;
		}
		Log.v("varahalababu", temp);
		for (int i = 0; i < temp.length(); i++) {
			try {
				String sub = temp.substring(i, i + 4);
				if (sub.equals("8020") || sub.equals("3900") || sub.equals("3902")) {
					if (sub.equals("8020")) {
						ref1 = temp.substring(0, i);
						temp = temp.substring(i + 4, temp.length());
						if (sub.equals("3902")) {
							flage = true;
						}
						Log.v("varahalababu", temp);
						ref1_flage=false;
						break;
					}else{
						ref1 = temp.substring(0, i);
						temp = temp.substring(i , temp.length());//temp = temp.substring(i + 4, temp.length());
						if (sub.equals("3902")) {
							flage = true;
						}
						Log.v("varahalababu", temp);
						ref1_flage=false;
						break;
					}
				}
			} catch (Exception e) {
//				ref1 = temp;
//				temp = "";
				ref1_flage=true;
			}
		}
		
		Log.v("varahalababu", temp);
		
		if (ref1_flage) {
			if (temp.length() > 12) {
				checkthedatevalue_ref(temp);
			} else {
				ref1 = temp;
				temp="";
			}
		}else{
			Log.v("varahalababu", "===============");
		}

		for (int i = 0; i < temp.length(); i++) {
			try {
				String sub = temp.substring(i, i + 4);
				Log.v("varahalababu", temp);
				if (sub.equals("3900")) {
					ref2 = temp.substring(0, i);
					temp = temp.substring(i + 4, temp.length());
					ref2_flage=false;
					
					break;
				}
			} catch (Exception e) {
				try {
					for (int j = 0; j < temp.length(); j++) {
						String sub = temp.substring(j, j + 4);
						if (sub.equals("3902")) {
							ref2 = temp.substring(0, j);
							temp = temp.substring(j + 4, temp.length());
							flage = true;
							ref2_flage=false;
							break;
						}
					}
				} catch (Exception e2) {
					Log.v("vara", e2.getMessage());
					ref2_flage=true;
				}
			}
		}
		
		Log.v("varahalababu", "==============="+ref2_flage);
		if (ref2_flage) {
			if (temp.length() > 12) {
				checkthedatevalue(temp);
			} else {
				ref2 = temp;
				temp="";
			}
		}
		
		try {
			if (temp.length() > 12) {
				gettheref2(temp);
			} else {
				value1 = temp;
			}
		} catch (Exception e) {
			value1 = temp;
		}
		bindoutputvalues();
	}
	
	
	
	private static void checkthedatevalue_ref(String temp2) {
		try {
			temp_for_fetcha = temp2.substring(temp2.length() - 8, temp2.length());
			String temp1 = temp_for_fetcha.substring(0, 4);
			int value = Integer.parseInt(temp1) / 2012;
			if (value == 0) {
				ref1 = temp2;
				temp=null;
			} else {
				try {
					if (temp2.substring(temp2.length() - 10, temp2.length() - 8).equals("96")) {
						duedate = temp_for_fetcha;
						try {
							ref1=temp2.substring(0,temp2.length() - 10);
							temp="";
						} catch (Exception e) {
							
						}
					}else{
						ref1 = temp2;
						temp="";
					}
				} catch (Exception e) {
					ref1 = temp2;
					temp="";
				}
			}

		} catch (Exception e) {
			ref2 = temp2;
			temp="";
		}
		
	}
	

	private static void checkthedatevalue(String temp2) {
		try {
			temp_for_fetcha = temp2.substring(temp2.length() - 8, temp2.length());
			String temp1 = temp_for_fetcha.substring(0, 4);
			int value = Integer.parseInt(temp1) / 2012;
			if (value == 0) {
				ref2 = temp2;
				temp="";
			} else {
				try {
					if (temp2.substring(temp2.length() - 10, temp2.length() - 8).equals("96")) {
						duedate = temp_for_fetcha;
						try {
							ref2=temp2.substring(0,temp2.length() - 10);
							temp="";
						} catch (Exception e) {
							
						}
					}else{
						ref2 = temp2;
						temp="";
					}
				} catch (Exception e) {
					ref2 = temp2;
					temp="";
				}
			}

		} catch (Exception e) {
			ref2 = temp2;
			temp="";
		}
		
	}

	private static void gettheref2(String temp2) {
		try {
			temp_for_fetcha = temp2.substring(temp2.length() - 8, temp2.length());
			String temp1 = temp_for_fetcha.substring(0, 4);
			int value = Integer.parseInt(temp1) / 2012;
			if (value == 0) {
				value1 = temp2;
			} else {
				duedate = temp_for_fetcha;
				value1 = temp2.substring(0, temp2.length() - 10);
			}

		} catch (Exception e) {
			value1 = temp2;
		}

	}

	private static void bindoutputvalues() {

		if (ref1.length() > 1) {

		} else {
			ref1 = ref2;
			ref2 = "";
		}
		if (flage) {
			try {
				if (value1.length() > 2) {
					value1 = value1.substring(0, value1.length() - 2) + "."
							+ value1.substring(value1.length() - 2, value1.length());
				}
			} catch (Exception e) {
				value1 = "0." + value1;
			}
		}
		System.out.println("companyname==========>   " + companyname);
		System.out.println("ref1==========>   " + ref1);
		System.out.println("ref2==========>   " + ref2);
		System.out.println("value==========>   " + value1);
		System.out.println("duedate==========>   " + duedate);
		
		
	}

	/* Add the spaces to Barcode */
	// private String bindthetabspace(String value) {
	//
	// String[] stringArray = (value).split(" ");
	//
	// String babu_spaces = null;
	// for (int i = 0; i < stringArray.length; i++) {
	// if (i == 1) {
	// companyname = stringArray[++i];
	// babu_spaces = "415" + space + companyname;
	// } else {
	// if (i > 1) {
	//
	// if (getValue8020(stringArray[i])) {
	// if (ref1.equals("")) {
	// ref1 = stringArray[i].substring(4,
	// stringArray[i].length());
	// babu_spaces = babu_spaces + space + "8020" + space
	// + ref1;
	// } else {
	// ref2 = stringArray[i].substring(4,
	// stringArray[i].length());
	// babu_spaces = babu_spaces + space + "8020" + space
	// + ref2;
	// }
	// } else if (getValue3900(stringArray[i])) {
	// if (devided2flage) {
	// String bval = stringArray[i].substring(4,
	// stringArray[i].length());
	//
	// value1 = bval.substring(0, bval.length() - 2)
	// + "."
	// + bval.substring(bval.length() - 2,
	// bval.length());
	// babu_spaces = babu_spaces + space + "3902" + space
	// + bval;
	// } else {
	// value1 = stringArray[i].substring(4,
	// stringArray[i].length());
	// babu_spaces = babu_spaces + space + "3900" + space
	// + value1;
	// }
	// } else if (getdate(stringArray[i])) {
	// duedate = stringArray[i].substring(2,
	// stringArray[i].length());
	// babu_spaces = babu_spaces + space + "96" + space
	// + duedate;
	// }
	//
	// }
	// }
	//
	// }
	//
	// return babu_spaces;
	//
	// }

	/* Divided the Barcode */
	private void getTheValuesFromBarcodeNew() {

		String[] stringArray = (receiced_barcode).replace("*", "=").split("=");
		ref1 = "";
		ref2 = "";
		System.out.println(Arrays.toString(stringArray));
		for (int i = 0; i < stringArray.length; i++) {
			if (i == 2) {
				companyname = stringArray[i];
			} else {
				if (getValue8020(stringArray[i])) {
					if (ref1.length() > 0) {
						ref2 = stringArray[i].substring(4, stringArray[i].length());
						System.out.println("==========else===========");
					} else {
						ref1 = stringArray[i].substring(4, stringArray[i].length());
						System.out.println("==========IF===========");
					}
				} else if (getValue3900(stringArray[i])) {
					if (devided2flage) {
						String bval = stringArray[i].substring(4, stringArray[i].length());

						value1 = bval.substring(0, bval.length() - 2) + "."
								+ bval.substring(bval.length() - 2, bval.length());
					} else {
						value1 = stringArray[i].substring(4, stringArray[i].length());
					}
				} else if (getdate(stringArray[i])) {
					duedate = stringArray[i].substring(2, stringArray[i].length());
				}
			}

		}
	}

	private static boolean getdate(String string) {
		try {
			if (string.substring(0, 2).equals("96")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean getValue3900(String string) {
		try {
			if (string.substring(0, 4).equals("3900")) {
				return true;
			} else if (string.substring(0, 4).equals("3902")) {
				devided2flage = true;
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean getValue8020(String string) {
		try {
			if (string.substring(0, 4).equals("8020")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

}