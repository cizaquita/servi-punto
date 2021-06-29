/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dao.UserModuleIDDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.SyncDataFromServer;

public class LoginActivity extends BaseActivity implements OnClickListener,
		OnItemSelectedListener {

	private EditText etxtUserName, etxtPassword;
	private Spinner spnLanguage;
	private Button btnLogin;
	private String userName, password;
	private ArrayAdapter<String> languageAdapter;
	private Hashtable<String, String> langTable;
	ServiApplication appContext;
	Intent intent;
	SharedPreferences sharedpreferences;
	Editor editor;
	private TextView help_desk;
	private boolean loginflage = true;

	// private ImageView help_desk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, MenuActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
		ServiApplication.login_session_count = 0;
		inItUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
		List<DTO> dto = UserDetailsDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		if (dto.size() > 0) {
			UserDetailsDTO userdao = (UserDetailsDTO) dto.get(0);
			ServiApplication.userName = userdao.getUserName();
			ServiApplication.store_id = userdao.getNitShopId();
			if (userdao.getIsClosed() == 0) {
				if (ServiApplication.backButton) {
					ServiApplication.backButton = false;
					this.finish();
				} else {
					changeLanguage(sharedpreferences.getString(
							"Servitienda_Language", ""));
					CommonMethods.openNewActivity(LoginActivity.this,
							MenuActivity.class);
					this.finish();
				}

			} else {

			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_Login:
			userName = etxtUserName.getText().toString().trim();
			password = etxtPassword.getText().toString().trim();
			validateCredentials();
			break;

		// case R.id.help_desk:
		// getHelpdeskInfo();
		// break;

		default:
			break;
		}
	}

	private void getHelpdeskInfo() {

		if (sharedpreferences.getString("STORE_CONTACT_NUMBER", "").length() > 0) {
			String[] days = sharedpreferences.getString("STORE_CONTACT_NUMBER",
					"").split(",");
			String numbesrs = "";
			for (int i = 0; i < days.length; i++) {
				numbesrs = numbesrs + days[i] + "\n";
			}
			help_desk.setText(numbesrs);
		} else {
			help_desk.setVisibility(View.GONE);
		}

	}

	private void validateCredentials() {
		if (!"".equals(userName) && !"".equals(password)) {
			/*
			 * List<DTO> userDetails = UserDetailsDAO.getInstance().getRecords(
			 * DBHandler.getDBObj(0));
			 */
			if (NetworkConnectivity.netWorkAvailability(LoginActivity.this)) {
				new PoastPayment().execute(makejsonobject(userName, password));
			} else {
				List<DTO> userDetails = UserDetailsDAO.getInstance()
						.getRecordsWithValues(DBHandler.getDBObj(0),
								"user_name", userName);
				if (userDetails.size() > 0) {
					UserDetailsDTO userinfo = (UserDetailsDTO) userDetails
							.get(0);
					if(userinfo.getPassword().equals(password)){
						ServiApplication.userName = userName;
						ServiApplication.store_id = userinfo.getNitShopId();
						CommonMethods.startIntent(LoginActivity.this,
								MenuActivity.class);
						finish();
					}else {
						CommonMethods.showCustomToast(LoginActivity.this,
								getResources().getString(R.string.invalid_pwd));
					}
				} else {
					CommonMethods.showCustomToast(LoginActivity.this,
							getResources().getString(R.string.no_wifi_adhoc));
				}
			}
		} else
			CommonMethods.showToast(LoginActivity.this, getResources()
					.getString(R.string.fileds_not_blank));
	}

	private void moveToMunuActivity() {
		new UiHandler().execute();
	}

	private void inItUI() {
		setContentView(R.layout.login);
		etxtUserName = (EditText) findViewById(R.id.etxt_UserName);
		etxtPassword = (EditText) findViewById(R.id.etxt_Password);
		spnLanguage = (Spinner) findViewById(R.id.spn_Language);
		btnLogin = (Button) findViewById(R.id.btn_Login);
		help_desk = (TextView) findViewById(R.id.help_desk);

		// help_desk = (ImageView) findViewById(R.id.help_desk);
		// help_desk.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		spnLanguage.setOnItemSelectedListener(this);

		loadUI();

	}

	private void loadUI() {
		etxtPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (etxtPassword.getText().toString().contains(" ")) {
					CommonMethods.showCustomToast(LoginActivity.this,
							getResources()
									.getString(R.string.no_spaces_allowed));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (etxtPassword.getText().toString().contains(" ")) {
					int lenght = etxtPassword.getText().toString().length();
					s.delete(lenght - 1, lenght);
				}
			}
		});
		languageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getLanguages());
		languageAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnLanguage.setAdapter(languageAdapter);

		spnLanguage.setSelection(1);
		if (sharedpreferences.getString("Servitienda_Language", "") != null) {

		} else {
			editor.putString("Servitienda_Language", "Espanol");
			editor.commit();
		}
		getHelpdeskInfo();
	}

	private List<String> getLanguages() {
		List<String> list = new ArrayList<String>();
		langTable = new Hashtable<String, String>();
		list.add(getResources().getString(R.string.english));
		langTable.put(getResources().getString(R.string.english),
				Constants.ENGLISH);

		list.add(getResources().getString(R.string.spanish));
		langTable.put(getResources().getString(R.string.spanish),
				Constants.SPANISH);

		/*
		 * list.add(getResources().getString(R.string.portuguese));
		 * langTable.put
		 * (getResources().getString(R.string.portuguese),Constants.PORTUGESUE);
		 */

		return list;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int id,
			long arg3) {
		editor.putString("Servitienda_Language", parent.getItemAtPosition(id)
				.toString());
		editor.commit();
		changeLanguage(parent.getItemAtPosition(id).toString());

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void changeLanguage(String language) {
		try {
			Locale locale = new Locale(langTable.get(language));
			Locale.setDefault(locale);
			Configuration config1 = new Configuration();
			config1.locale = locale;
			getBaseContext().getResources().updateConfiguration(config1,
					getBaseContext().getResources().getDisplayMetrics());
		} catch (Exception e) {
		}
		updateUI();
	}

	private void updateUI() {
		etxtUserName.setHint(R.string.hint_user_name);
		etxtPassword.setHint(R.string.hint_password);
		btnLogin.setText(R.string.login);

	}

	@Override
	public void onBackPressed() {
		appContext.clearActivityList();
		this.finish();
	}

	private String makejsonobject(String username, String password) {
		try {
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("username", username);
			jsonobject.put("password", password);
			return jsonobject.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private List<DTO> setDataUserInfo(String responds_feed) {
		List<DTO> userInfo = new ArrayList<DTO>();
		try {

			JSONObject jsonObject = new JSONObject(responds_feed);
			JSONObject jsonuserJsonObject = jsonObject.getJSONObject("user");
			UserDetailsDTO userDetails = new UserDetailsDTO();
			List<DTO> userDetails12 = UserDetailsDAO.getInstance().getRecords(
					DBHandler.getDBObj(Constants.READABLE));
			UserDetailsDTO udto = null;
			for (DTO dto : userDetails12) {
				udto = (UserDetailsDTO) dto;
			}
			if (userDetails12.isEmpty()) {
				userDetails.setCedulaDocument(jsonuserJsonObject
						.getString("cedula_document"));
				userDetails.setCloseDateTime(jsonuserJsonObject
						.getString("closing_time"));
				userDetails.setCompanyName(jsonuserJsonObject
						.getString("company_name"));
				userDetails.setImei(jsonuserJsonObject.getString("imei"));
				userDetails.setIsClosed(Constants.TRUE);
				userDetails.setLastLogin(Dates.currentdate());
				userDetails.setName(jsonuserJsonObject.getString("name"));
				userDetails.setNitShopId(jsonuserJsonObject
						.getString("store_code"));
				userDetails.setOpeningBalance(Dates.currentdate());
				userDetails.setOpeningDateTime(jsonuserJsonObject
						.getString("opening_date_time"));
				userDetails.setPassword(jsonuserJsonObject
						.getString("password"));
				userDetails.setRegistrationDate(jsonuserJsonObject
						.getString("registration_date"));
				userDetails.setIs_admin(jsonuserJsonObject
						.getString("is_admin"));
				userDetails.setIs_authorized(jsonuserJsonObject
						.getString("is_authorized"));
				userDetails.setAuthtoken(jsonuserJsonObject
						.getString("authtoken"));
				
				if (jsonuserJsonObject
						.getString("is_admin").equals("null")) {
					if (jsonuserJsonObject
							.getString("is_authorized").equals("null")) {
						userDetails.setIs_admin("Y");
					}
				}
				
				userDetails.setEmail(jsonuserJsonObject.getString("email"));
				try {
					userDetails.setTerminalId(jsonuserJsonObject
							.getString("terminalId"));
					userDetails.setPuntoredId(jsonuserJsonObject
							.getString("puntoDeVentaId"));
					userDetails.setComercioId(jsonuserJsonObject
							.getString("comercioId"));
					userDetails.setSystemId("1");
				} catch (Exception e) {
					userDetails.setTerminalId("");
					userDetails.setPuntoredId("");
					userDetails.setComercioId("");
					userDetails.setSystemId("");
				}
				try {
					userDetails.setActualBalance(""
							+ jsonuserJsonObject.getDouble("actual_balance"));
				} catch (Exception e) {
					userDetails.setActualBalance("" + 00.0);

				}
				userDetails.setSyncStatus(Constants.FALSE);
				userDetails.setUserId(jsonuserJsonObject.getString("user_id"));
				userDetails.setUserName(jsonuserJsonObject
						.getString("username"));
				userInfo.add(userDetails);

				editor.putString("user_name",
						jsonuserJsonObject.getString("username"));
				editor.putString("store_code",
						jsonuserJsonObject.getString("store_code"));
				editor.commit();
				loginflage = true;
				return userInfo;
			} else if (udto.getNitShopId().contains(
					jsonuserJsonObject.getString("store_code"))) {
				userDetails.setCedulaDocument(jsonuserJsonObject
						.getString("cedula_document"));
				userDetails.setCloseDateTime(jsonuserJsonObject
						.getString("closing_time"));
				userDetails.setCompanyName(jsonuserJsonObject
						.getString("company_name"));
				userDetails.setImei(jsonuserJsonObject.getString("imei"));
				userDetails.setIsClosed(Constants.TRUE);
				userDetails.setLastLogin(Dates.currentdate());
				userDetails.setName(jsonuserJsonObject.getString("name"));
				userDetails.setNitShopId(jsonuserJsonObject
						.getString("store_code"));
				userDetails.setOpeningBalance(Dates.currentdate());
				userDetails.setOpeningDateTime(jsonuserJsonObject
						.getString("opening_date_time"));
				userDetails.setPassword(jsonuserJsonObject
						.getString("password"));
				userDetails.setRegistrationDate(jsonuserJsonObject
						.getString("registration_date"));
				userDetails.setIs_admin(jsonuserJsonObject
						.getString("is_admin"));
				userDetails.setIs_authorized(jsonuserJsonObject
						.getString("is_authorized"));
				userDetails.setEmail(jsonuserJsonObject.getString("email"));
				userDetails.setAuthtoken(jsonuserJsonObject
						.getString("authtoken"));
				
				if (jsonuserJsonObject
						.getString("is_admin").equals("null")) {
					if (jsonuserJsonObject
							.getString("is_authorized").equals("null")) {
						userDetails.setIs_admin("Y");
					}
				}
				try {
					userDetails.setTerminalId(jsonuserJsonObject
							.getString("terminalId"));
					userDetails.setPuntoredId(jsonuserJsonObject
							.getString("puntoDeVentaId"));
					userDetails.setComercioId(jsonuserJsonObject
							.getString("comercioId"));
					userDetails.setSystemId("1");
				} catch (Exception e) {
					userDetails.setTerminalId("");
					userDetails.setPuntoredId("");
					userDetails.setComercioId("");
					userDetails.setSystemId("");
				}

				try {

					userDetails.setActualBalance(""
							+ jsonuserJsonObject.getDouble("actual_balance"));

				} catch (Exception e) {
					userDetails.setActualBalance("" + 00.0);

				}
				userDetails.setSyncStatus(Constants.FALSE);
				userDetails.setUserId(jsonuserJsonObject.getString("user_id"));
				userDetails.setUserName(jsonuserJsonObject
						.getString("username"));
				userInfo.add(userDetails);
				editor.putString("user_name",
						jsonuserJsonObject.getString("username"));
				editor.putString("store_code",
						jsonuserJsonObject.getString("store_code"));
				editor.commit();
				loginflage = true;
				return userInfo;

			} else {
				loginflage = false;
				return userInfo;
			}

		} catch (Exception e) {
			return null;
		}
	}

	private class PoastPayment extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(LoginActivity.this);
		String responds = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					LoginActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			responds = servicehandler.makeServiceCall(ServiApplication.URL
					+ "/login", ServiceHandler.POST, params[0]);

			// "/puntored/user/login"

			if (ServiApplication.connectionTimeOutState) {
				ServiApplication.responds_feed = responds;
			} else {
				ServiApplication.responds_feed = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			ServiApplication.userName = userName;

			if (ServiApplication.connectionTimeOutState) {
				if (responds != null) {
					try {
						if (new JSONStatus()
								.status(ServiApplication.responds_feed) == 0) {
							UserModuleIDDAO.getInstance().deleteAllRecords(
									DBHandler.getDBObj(Constants.WRITABLE));
							if (new JSONStatus()
									.getStoreCode(ServiApplication.responds_feed)) {
								if (UserDetailsDAO
										.getInstance()
										.insert(DBHandler
												.getDBObj(Constants.WRITABLE),
												setDataUserInfo(ServiApplication.responds_feed))) {
									if (loginflage) {
										moveToMunuActivity();
									} else {
										CommonMethods
												.showCustomToast(
														LoginActivity.this,
														getResources()
																.getString(
																		R.string.invalid_usernamepass));
									}

								} else {
									if (loginflage) {
										updateUserTable(setDataUserInfo(ServiApplication.responds_feed));
										moveToMunuActivity();
									} else {
										CommonMethods
												.showCustomToast(
														LoginActivity.this,
														getResources()
																.getString(
																		R.string.invalid_usernamepass));
									}
								}
							} else {
								CommonMethods.showCustomToast(
										LoginActivity.this,
										getResources().getString(
												R.string.store_code_missing));
							}
						} else {
							if (new JSONStatus().notactivemsg(
									ServiApplication.responds_feed).equals(
									"User not Active")) {
								CommonMethods.showCustomToast(
										LoginActivity.this,
										ServiApplication.userName
												+ "  "
												+ getResources().getString(
														R.string.user_blocked));
							} else {

								if (new JSONStatus()
										.user_id_comparision(ServiApplication.responds_feed)) {
									ServiApplication.login_session_count++;
									if (ServiApplication.login_session_count < 3) {
										if (new JSONStatus().message(
												ServiApplication.responds_feed)
												.contains("Invalid")) {
											CommonMethods
													.showCustomToast(
															LoginActivity.this,
															getResources()
																	.getString(
																			R.string.invalid_usernamepass));
										} else {
											CommonMethods
													.showCustomToast(
															LoginActivity.this,
															getResources()
																	.getString(
																			R.string.user_logged_in));
										}
									} else {
										if (NetworkConnectivity
												.netWorkAvailability(LoginActivity.this)) {
											new BlockUser()
													.execute(ServiApplication.res_user_id);
										} else {

											CommonMethods
													.showCustomToast(
															LoginActivity.this,
															getResources()
																	.getString(
																			R.string.no_wifi_adhoc));

										}

									}

								} else {
									CommonMethods
											.showCustomToast(
													LoginActivity.this,
													getResources()
															.getString(
																	R.string.invalid_usernamepass));
								}

							}
						}

						try {
							String number = null;
							JSONObject rootjsonobj = new JSONObject(
									ServiApplication.responds_feed);
							JSONArray contact_info_jsonarray = rootjsonobj
									.getJSONArray("contact information");
							JSONObject contact_info_jsonobj = contact_info_jsonarray
									.getJSONObject(0);
							try {
								
								
								if (contact_info_jsonobj.getString(
										"help_desk_contact_number1").length() > 2) {
									number = contact_info_jsonobj
											.getString("help_desk_contact_number1");
								}
								if (contact_info_jsonobj.getString(
										"help_desk_contact_number2").length() > 2) {
									number = number
											+ ","
											+ contact_info_jsonobj
													.getString("help_desk_contact_number2");
								}
								if (contact_info_jsonobj.getString(
										"help_desk_contact_number3").length() > 2) {
									number = number
											+ ","
											+ contact_info_jsonobj
													.getString("help_desk_contact_number3");
								}
								if (contact_info_jsonobj.getString(
										"help_desk_contact_number4").length() > 2) {
									number = number
											+ ","
											+ contact_info_jsonobj
													.getString("help_desk_contact_number4");
								}
								
								editor.putString("store_name", contact_info_jsonobj
										.getString("store_name"));
								editor.putString("telephone", contact_info_jsonobj
										.getString("telephone"));

							} catch (Exception e) {

							}
							
							editor.putString("STORE_CONTACT_NUMBER", number);
							editor.commit();
						} catch (Exception e) {

						}

					} catch (Exception e) {
						CommonMethods.showCustomToast(
								LoginActivity.this,
								getResources().getString(
										R.string.sync_error_msg));
					}
				} else {
					CommonMethods.showCustomToast(LoginActivity.this,
							getResources().getString(R.string.sync_error_msg));
				}
			} else {
				CommonMethods.showCustomToast(LoginActivity.this,
						getResources().getString(R.string.sync_error_msg));
			}
		}
	}

	public class UiHandler extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.db_cleanup),LoginActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			UserModuleIDDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE),
					new ParsingHandler()
							.getModulesData(ServiApplication.responds_feed));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			new SyncDataFromServer(LoginActivity.this, 0);
		}

	}

	public void synkMethod() {
		List<DTO> list = setDataUserInfo(ServiApplication.responds_feed);
		if (new AsynkTaskClass().callcentralserver("/sync", ""
				+ ServiceHandler.POST,
				new RequestFiels(LoginActivity.this).getMoveToMenuData(list),
				LoginActivity.this) != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				CommonMethods.startIntent(LoginActivity.this,
						MenuActivity.class);
				this.finish();
			} else {
				CommonMethods.startIntent(LoginActivity.this,
						MenuActivity.class);
				this.finish();
				CommonMethods.showCustomToast(LoginActivity.this,
						getResources().getString(R.string.sync_error_msg));
			}
		} else {
			CommonMethods.startIntent(LoginActivity.this, MenuActivity.class);
			this.finish();
		}

	}

	private class BlockUser extends AsyncTask<String, Void, Void> {
		JSONObject obj;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					LoginActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				obj = new JSONObject();
				obj.put("userid", params[0]);
			} catch (Exception e) {

			}
			new ServiceHandler(LoginActivity.this).makeServiceCall(
					ServiApplication.URL + "/user/block", ServiceHandler.POST,
					obj.toString());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.showCustomToast(LoginActivity.this,
					ServiApplication.userName + "  "
							+ getResources().getString(R.string.user_blocked));
			CommonMethods.dismissProgressDialog();
		}

	}

	public void updateUserTable(List<DTO> setDataUserInfo) {

		for (DTO dto : setDataUserInfo) {
			UserDetailsDTO udto = (UserDetailsDTO) dto;
			UserDetailsDAO.getInstance().UpdateUserMycashBoxInfo(
					DBHandler.getDBObj(Constants.WRITABLE), udto);
		}
	}
}
