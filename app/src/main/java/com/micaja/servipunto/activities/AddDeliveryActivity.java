/*******************************************************************************
c *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;

import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddDeliveryActivity extends BaseActivity implements OnClickListener,
		DialogInterface.OnClickListener {

	private EditText etxtName, etxtCedula, etxtPhoneNum, etxtEmail,
			etxtInitialDebt, etxtAddress;
	private TextView txtbalAmt, txtScreenName;

	private RadioGroup radiobtnGender;

	private Button btnSubmit, btnCancel, btnHistoric;
	private AlertDialog saveDialog;
	private boolean isValid;
	private String name, nit, phoneNum, email, initialdebt, gender, address,
			balance_amount;
	String screen = "";
	public SharedPreferences sharedpreferences;
	ServiApplication appContext;
	private String deliveryID;
	private UserDetailsDTO dto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		inItUI();

	}

	private void inItUI() {
		setContentView(R.layout.add_delivery_activity);

		etxtName = (EditText) findViewById(R.id.etxt_clientName);
		etxtCedula = (EditText) findViewById(R.id.etxt_clientNit);
		etxtPhoneNum = (EditText) findViewById(R.id.etxt_clientPhoneNum);
		etxtEmail = (EditText) findViewById(R.id.etxt_clientEmail);
		etxtInitialDebt = (EditText) findViewById(R.id.etxt_initialDebt);
		etxtAddress = (EditText) findViewById(R.id.etxt_clientAddress);
		txtbalAmt = (TextView) findViewById(R.id.txt_balAmount);

		txtScreenName = (TextView) findViewById(R.id.txt_ScreenName);

		radiobtnGender = (RadioGroup) findViewById(R.id.rg_gender);

		btnSubmit = (Button) findViewById(R.id.btn_Submit);
		btnCancel = (Button) findViewById(R.id.btn_Cancel);
		btnHistoric = (Button) findViewById(R.id.btn_History);

		btnSubmit.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnHistoric.setOnClickListener(this);

		etxtInitialDebt.setFilters(new InputFilter[] { new DecimalInputFilter(
				15, 2) });
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(50);

		etxtAddress.setFilters(FilterArray);
		etxtName.setFilters(FilterArray);
		loadUI();

	}
	
	@Override
	public void onBackPressed() {

		CommonMethods.startIntent(AddDeliveryActivity.this, appContext.mAddDeliveryScreenData.mBackStackClass);
		finish();
	}

	private boolean mIsNewItem = false;

	private void loadUI() {
		if (appContext.mAddDeliveryScreenData == null) {
			return;
		}
		if (appContext.mAddDeliveryScreenData.mScreenData != null) {
			txtScreenName.setText(getResources()
					.getString(R.string.edit_client));
			setDataToViews(appContext.mAddDeliveryScreenData.mScreenData);
		} else {
			mIsNewItem = true;
			btnHistoric.setVisibility(View.GONE);
			txtScreenName
					.setText(getResources().getString(R.string.add_client));
		}
	}

	private void setDataToViews(DeliveryDTO dto) {

		etxtCedula.setEnabled(false);
		etxtCedula.setBackgroundResource(R.drawable.etxt_disable);
		etxtInitialDebt.setEnabled(false);
		etxtCedula.setText(dto.getCedula());

		etxtName.setText(dto.getName());
		etxtEmail.setText(dto.getEmail());
		etxtPhoneNum.setText(dto.getTelephone());
		etxtInitialDebt.setText(dto.getInitialDebt());
		etxtInitialDebt.setBackgroundResource(R.drawable.etxt_disable);

		etxtAddress.setText(dto.getAddress());
		txtbalAmt.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(dto.getBalanceAmount())));

		if (dto.getGender().equals("0")) {
			radiobtnGender.check(R.id.radio_male);
		} else if (dto.getGender().equals("1")) {
			radiobtnGender.check(R.id.radio_female);
		} else
			radiobtnGender.check(R.id.radio_Others);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Submit:
			validateAndSave();
			break;

		case R.id.btn_History:
			Intent intent = new Intent(this, DeliveryHistoryActivity.class);
			intent.putExtra(ConstantsEnum.CLIENT_ID.code(), etxtCedula
					.getText().toString().trim());
			intent.putExtra(ConstantsEnum.CLIENT_NAME.code(), etxtName
					.getText().toString().trim());
			intent.putExtra(ConstantsEnum.CLIENT_TELEPHONE.code(), etxtPhoneNum
					.getText().toString().trim());
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;

		case R.id.btn_Cancel:
			try {
				CommonMethods.startIntent(AddDeliveryActivity.this,
						appContext.mAddDeliveryScreenData.mBackStackClass);
				finish();
			} catch (Exception e) {
				CommonMethods.openNewActivity(this, DeliveryActivity.class);
				finish();
			}
			

			break;

		default:
			break;
		}

	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validationFields(stringBuffer))
			CommonMethods.displayAlert(true,
					getResources().getString(R.string.fail),
					stringBuffer.toString(),
					getResources().getString(R.string.ok), "",
					AddDeliveryActivity.this, null, false);
		else
			saveDialog = CommonMethods.displayAlert(true, getResources()
					.getString(R.string.confirm),
					getResources().getString(R.string.save_messgae),
					getResources().getString(R.string.yes), getResources()
							.getString(R.string.no), AddDeliveryActivity.this,
					AddDeliveryActivity.this, false);
	}

	private boolean validationFields(StringBuffer stringBuffer) {

		name = etxtName.getText().toString().trim();
		nit = etxtCedula.getText().toString().trim();
		email = etxtEmail.getText().toString().trim();
		phoneNum = etxtPhoneNum.getText().toString().trim();
		initialdebt = etxtInitialDebt.getText().toString().trim();
		address = etxtAddress.getText().toString().trim();
		balance_amount = txtbalAmt.getText().toString();
		int selectedId = radiobtnGender.getCheckedRadioButtonId();

		if (selectedId != -1) {

			RadioButton radiobtn = (RadioButton) findViewById(selectedId);
			gender = radiobtn.getTag().toString();
		}

		isValid = true;

		if (name.length() == 0) {
			isValid = false;
			stringBuffer.append(getResources().getString(R.string.enter_name));
		}else if ("0".equals(name) || ".".equals(name)) {
			isValid = false;
			stringBuffer
					.append(getResources().getString(R.string.invalid_name));
		}
		if (nit.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_nit));
		} else if ("0".equals(nit) || ".".equals(nit) || nit.contains(" ")) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_nit));
		}

		if (email.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_email));
		} else {
			if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
				isValid = false;
				stringBuffer.append("\n"
						+ getResources().getString(R.string.invalid_email));
			}
		}
		if (phoneNum.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_phonenum));
		} else if (phoneNum.length() < 7 || ".".equals(phoneNum)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_phonenum));
		}
		if (address.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_address));
		} else if ("0".equals(address) || ".".equals(address)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_address));
		}

		if (selectedId == -1) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_gender));
		}
		if (".".equals(initialdebt)) {
			isValid = false;
			stringBuffer.append(getResources().getString(
					R.string.enter_valid_value));
		}
		return isValid;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		dialog.dismiss();
		if (dialog == saveDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				try {
					if (mIsNewItem) {
						List<String> deliveryIDList = DeliveryDAO.getInstance()
								.getDeliveryIDs(
										DBHandler.getDBObj(Constants.READABLE));

						if (!deliveryIDList.contains(nit)) {
							if (insertRecordInDB()) {
								if (appContext.mAddDeliveryScreenData.mBackStackClass == SalesActivity.class) {
									appContext
											.setDeliveryDTO((DeliveryDTO) getDataFromViews()
													.get(0));
								}
								if (NetworkConnectivity
										.netWorkAvailability(AddDeliveryActivity.this)) {
									new AddOrUpdateDelivery().execute("insert");
								} else {
									CommonMethods.showCustomToast(
											this,
											getResources().getString(
													R.string.no_wifi_adhoc));
									CommonMethods
											.startIntent(
													AddDeliveryActivity.this,
													appContext.mAddDeliveryScreenData.mBackStackClass);
									finish();
								}
							}
						} else
							CommonMethods.showCustomToast(
									AddDeliveryActivity.this,
									getResources().getString(
											R.string.client_id_exist));
					} else {
						if (updateRecordinDB()) {
							if (NetworkConnectivity
									.netWorkAvailability(AddDeliveryActivity.this)) {
								new AddOrUpdateDelivery().execute("Update");
							} else {
								CommonMethods.showCustomToast(
										this,
										getResources().getString(
												R.string.no_wifi_adhoc));
								CommonMethods
										.startIntent(
												AddDeliveryActivity.this,
												appContext.mAddDeliveryScreenData.mBackStackClass);
								finish();
							}
						} else {

						}

					}

				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}

	}

	private boolean updateRecordinDB() {
		if (DeliveryDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE),
				getUpdatedDataFromViews())) {
			DeliveryDTO cdto = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(
					DBHandler.getDBObj(Constants.READABLE), nit);
			DeliveryDAO.getInstance().updateSynckstatus(
					DBHandler.getDBObj(Constants.WRITABLE), cdto);


			return true;
		} else {
			CommonMethods.showCustomToast(AddDeliveryActivity.this,
					getResources().getString(R.string.not_added));
		}
		return false;
	}



	public class AddOrUpdateDelivery extends AsyncTask<String, Void, Void> {
		String responds, params_string;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					getResources().getString(R.string.please_wait),
					AddDeliveryActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			params_string = params[0];
			ServiceHandler servicehandler = new ServiceHandler(
					AddDeliveryActivity.this);
			if (params[0].equals("Update")) {
				responds = servicehandler.makeServiceCall(ServiApplication.URL
								+ "/delivery/update", ServiceHandler.POST,
						makejsonobj(true));
			} else {
				responds = servicehandler.makeServiceCall(ServiApplication.URL
								+ "/delivery/create", ServiceHandler.POST,
						makejsonobj(false));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			DeliveryDTO cdto = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(
					DBHandler.getDBObj(Constants.READABLE), nit);
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(responds) == 0) {
					cdto.setSyncStatus(1);
					DeliveryDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE), cdto);
					if (params_string.equals("Update")) {
						CommonMethods.showCustomToast(
								AddDeliveryActivity.this,
								getResources().getString(
										R.string.sucessfully_change));
					} else {
						CommonMethods.showCustomToast(AddDeliveryActivity.this,
								getResources()
										.getString(R.string.added_success));
					}
				} else {
					try {
						JSONObject json = new JSONObject(responds);
						if (json.getString("message").contains("Duplicate")) {
							DeliveryDAO.getInstance().delete(
									DBHandler.getDBObj(Constants.WRITABLE),
									cdto);
							CommonMethods.showCustomToast(
									AddDeliveryActivity.this,
									getResources().getString(
											R.string.customer_duplicate_record_start)+" "+cdto.getCedula()+" "+

											getResources().getString(
													R.string.customer_duplicate_record_end)+" "+dto.getNitShopId());
						}
					} catch (Exception e) {
						DeliveryDAO.getInstance().updateSynckstatus(
								DBHandler.getDBObj(Constants.WRITABLE), cdto);
						CommonMethods.showCustomToast(
								AddDeliveryActivity.this,
								getResources().getString(
										R.string.sync_error_msg));
					}
				}
			} else {
				CommonMethods.showCustomToast(AddDeliveryActivity.this,
						getResources().getString(R.string.sync_error_msg));
			}
			CommonMethods.dismissProgressDialog();
			CommonMethods.startIntent(AddDeliveryActivity.this,
					appContext.mAddDeliveryScreenData.mBackStackClass);
			finish();
		}

	}

	private DeliveryDTO getUpdatedDataFromViews() {
		DeliveryDTO deliveryDTO = appContext.mAddDeliveryScreenData.mScreenData;

		deliveryDTO.setName(name);
		deliveryDTO.setCedula(nit);
		deliveryDTO.setEmail(email);
		deliveryDTO.setTelephone(phoneNum);
		deliveryDTO.setGender(gender);
		deliveryDTO.setAddress(address);
		deliveryDTO.setModifiedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));

		return deliveryDTO;
	}

	private boolean insertRecordInDB() {
		DeliveryDTO cdto = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(
				DBHandler.getDBObj(Constants.READABLE), nit);

		if (DeliveryDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getDataFromViews())) {
			return true;
		} else
			CommonMethods.showCustomToast(AddDeliveryActivity.this,
					getResources().getString(R.string.not_added));
		return false;

	}

	private List<DTO> getDataFromViews() {

		List<DTO> deliveryList = new ArrayList<DTO>();

		DeliveryDTO deliveryDTO = new DeliveryDTO();
		deliveryDTO.setName(name);
		deliveryDTO.setCedula(nit);
		deliveryDTO.setEmail(email);
		deliveryDTO.setTelephone(phoneNum);
		deliveryDTO.setBalanceAmount(initialdebt);
		deliveryDTO.setGender(gender);
		deliveryDTO.setAddress(address);
		deliveryDTO.setActiveStatus(0);
		deliveryDTO.setInitialDebt(initialdebt);
		deliveryDTO.setCreatedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		deliveryDTO.setSyncStatus(0);
		deliveryDTO.setDeliveryID(deliveryID);

		deliveryList.add(deliveryDTO);

		return deliveryList;
	}

	private String makejsonobj(boolean b) {
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");
		DeliveryDTO user = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(
				DBHandler.getDBObj(Constants.READABLE), nit);

		try {
			JSONObject jsonobj = new JSONObject();
			if ("" != ServiApplication.store_id) {
				jsonobj.put("delivery_code", user.getCedula());
				jsonobj.put("name", user.getName());
				jsonobj.put("telephone", user.getTelephone());
				jsonobj.put("address", user.getAddress());
				jsonobj.put("email", user.getEmail());
				if (gender.equals("0")) {
					jsonobj.put("gender", user.getGender());
				} else {
					jsonobj.put("gender", user.getGender());
				}
				if (b) {
					jsonobj.put("modified_by", ServiApplication.userName);
				} else {
					jsonobj.put("created_by", ServiApplication.userName);
				}

				try {
					jsonobj.put("created_date",
							Dates.serverdateformate(user.getCreatedDate()));
				} catch (Exception e) {
				}
				try {
					jsonobj.put("modified_date",
							Dates.serverdateformate(user.getModifiedDate()));
				} catch (Exception e) {
				}
				try {
					jsonobj.put("last_payment_date",
							Dates.serverdateformate(user.getLastPaymentDate()));
				} catch (Exception e) {
				}
				jsonobj.put("active_status", "" + user.getActiveStatus());
				jsonobj.put("balance_amount",
						CommonMethods.getDoubleFormate(user.getBalanceAmount()));
				jsonobj.put("store_code", ServiApplication.store_id);
				jsonobj.put("initial_debts",
						CommonMethods.getDoubleFormate(initialdebt));

			}
			return jsonobj.toString();
		} catch (Exception e) {
			return null;
		}
	}

}
