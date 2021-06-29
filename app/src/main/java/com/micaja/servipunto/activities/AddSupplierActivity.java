/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddSupplierActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener {

	private Button btnMon, btnTues, btnWed, btnThu, btnFri, btnSat, btnSun;

	private EditText etxtSupName, etxtSupNit, etxtSupPhoneNum, etxtSupAdd, etxtContactname, etxtcontactNum;
	private TextView txtScreenName;

	private Button btnSubmit, btnCancel;
	private Spinner spnFreqVisit;
	private ArrayAdapter<String> freqVisitAdapter;
	private AlertDialog saveDialog;
	private String name, nit, address, phoneNum, freqVisit, contactname, contactNumber, visitDays = "",
			BalanceAmount = "0";
	private boolean isValid;
	private String screen;
	ServiApplication appContext;
	String supplierID = "";
	SupplierDTO supplierDto = new SupplierDTO();
	public SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		inItUI();

	}

	private void inItUI() {

		setContentView(R.layout.add_supplier_activity);

		txtScreenName = (TextView) findViewById(R.id.txt_ScreenName);

		etxtSupName = (EditText) findViewById(R.id.etxt_suppName);
		etxtSupNit = (EditText) findViewById(R.id.etxt_suppNit);
		etxtContactname = (EditText) findViewById(R.id.etxt_contactname);

		etxtcontactNum = (EditText) findViewById(R.id.etxt_contactNum);

		etxtSupPhoneNum = (EditText) findViewById(R.id.etxt_suppPhone);
		etxtSupAdd = (EditText) findViewById(R.id.etxt_supp_address);
		etxtSupName = (EditText) findViewById(R.id.etxt_suppName);
		spnFreqVisit = (Spinner) findViewById(R.id.spn_freqvisit);
		btnSubmit = (Button) findViewById(R.id.btn_Submit);
		btnCancel = (Button) findViewById(R.id.btn_Cancel);

		btnMon = (Button) findViewById(R.id.btn_WeekMon);
		btnTues = (Button) findViewById(R.id.btn_WeekTue);
		btnWed = (Button) findViewById(R.id.btn_WeekWed);
		btnThu = (Button) findViewById(R.id.btn_WeekThu);
		btnFri = (Button) findViewById(R.id.btn_WeekFri);
		btnSat = (Button) findViewById(R.id.btn_WeekSat);
		btnSun = (Button) findViewById(R.id.btn_WeekSun);

		btnMon.setOnClickListener(this);
		btnTues.setOnClickListener(this);
		btnWed.setOnClickListener(this);
		btnThu.setOnClickListener(this);
		btnFri.setOnClickListener(this);
		btnSat.setOnClickListener(this);
		btnSun.setOnClickListener(this);

		btnSubmit.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		loadUI();

	}

	private void loadUI() {
		screen = getIntent().getExtras().get(ConstantsEnum.SUPPLIER_MODE.code()).toString();
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(50);
		etxtContactname.setFilters(FilterArray);

		if (screen.equals(ConstantsEnum.EDIT_SUPPLIER.code())) {
			txtScreenName.setText(getResources().getString(R.string.edit_supplier));
			populateFreqVisitData();
			supplierID = getIntent().getExtras().getString(ConstantsEnum.SUPPLIER_ID.code());
			getDataFromDB(supplierID);
		} else {
			txtScreenName.setText(getResources().getString(R.string.add_supplier));
			populateFreqVisitData();
		}
	}

	private void populateFreqVisitData() {
		freqVisitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getFreqVisits());
		freqVisitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnFreqVisit.setAdapter(freqVisitAdapter);
	}

	private void getDataFromDB(String supplierID) {
		supplierDto = SupplierDAO.getInstance().getRecordsByID(DBHandler.getDBObj(Constants.READABLE), supplierID);

		setDataToViews(supplierDto);

	}

	private void setDataToViews(SupplierDTO dto) {
		etxtSupNit.setEnabled(false);
		etxtSupNit.setBackgroundResource(R.drawable.etxt_disable);

		/*
		 * etxtSupAdd.setEnabled(false);
		 * etxtSupAdd.setBackgroundResource(R.drawable.etxt_disable);
		 * 
		 * etxtSupPhoneNum.setEnabled(false);
		 * etxtSupPhoneNum.setBackgroundResource(R.drawable.etxt_disable);
		 * 
		 * etxtContactname.setEnabled(false);
		 * etxtContactname.setBackgroundResource(R.drawable.etxt_disable);
		 */

		etxtSupName.setText(dto.getName());
		etxtSupAdd.setText(dto.getAddress());
		etxtSupNit.setText(dto.getCedula());
		etxtSupPhoneNum.setText(dto.getTelephone());
		etxtContactname.setText(dto.getContactName());
		etxtcontactNum.setText(dto.getContactTelephone());
		try {
			spnFreqVisit.setSelection(Integer.parseInt(dto.getVisitFrequency()));
		} catch (Exception e) {
			try {
				String visitFrecuency = dto.getVisitFrequency();
				if (visitFrecuency.contains("Semanal"))
					visitFrecuency = "1";
				else if (visitFrecuency.contains("Quincenal"))
					visitFrecuency = "2";
				else if (visitFrecuency.contains("Mensual"))
					visitFrecuency = "3";
				else if (visitFrecuency.isEmpty())
					visitFrecuency = "1";
				spnFreqVisit.setSelection(Integer.parseInt(visitFrecuency));
			} catch (Exception e2) {
				spnFreqVisit.setSelection(Integer.parseInt("1"));
			}
		}
		BalanceAmount = dto.getBalanceAmount();
		try {
			String[] days = (dto.getVisitDays()).split(",");
			for (int i = 0; i < days.length; i++)
				setDataToBtn(Integer.parseInt(days[i]));
		} catch (Exception e) {
			try {
				String temp = "";
				String visitDays = dto.getVisitDays();
				if (visitDays.contains("Lunes")) {
					temp = temp + "0,";
				}
				if (visitDays.contains("Martes")) {
					temp = temp + "1,";
				}
				if (visitDays.contains("Miercoles")) {
					temp = temp + "2,";
				}
				if (visitDays.contains("Jueves")) {
					temp = temp + "3,";
				}
				if (visitDays.contains("Viernes")) {
					temp = temp + "4,";
				}
				if (visitDays.contains("Sabado")) {
					temp = temp + "5,";
				}
				if (visitDays.contains("Domingo")) {
					temp = temp + "6,";
				}
				visitDays = temp;
				if (visitDays.isEmpty())
					visitDays = "0,";
				String[] days = (visitDays.split(","));
				for (int i = 0; i < days.length; i++)
					setDataToBtn(Integer.parseInt(days[i]));
			} catch (Exception e2) {

			}
		}

	}

	private void setDataToBtn(int week) {
		switch (week) {
		case 0:
			visitDays += "0,";
			btnMon.setBackgroundResource(R.drawable.addsup_weekhover);
			btnMon.setTextColor(getResources().getColor(R.color.white_color));

			break;

		case 1:
			visitDays += "1,";
			btnTues.setBackgroundResource(R.drawable.addsup_weekhover);
			btnTues.setTextColor(getResources().getColor(R.color.white_color));

			break;
		case 2:
			visitDays += "2,";
			btnWed.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWed.setTextColor(getResources().getColor(R.color.white_color));
			break;
		case 3:
			visitDays += "3,";
			btnThu.setBackgroundResource(R.drawable.addsup_weekhover);
			btnThu.setTextColor(getResources().getColor(R.color.white_color));
			break;
		case 4:
			visitDays += "4,";
			btnFri.setBackgroundResource(R.drawable.addsup_weekhover);
			btnFri.setTextColor(getResources().getColor(R.color.white_color));
			break;
		case 5:
			visitDays += "5,";
			btnSat.setBackgroundResource(R.drawable.addsup_weekhover);
			btnSat.setTextColor(getResources().getColor(R.color.white_color));
			break;

		case 6:
			visitDays += "6,";
			btnSun.setBackgroundResource(R.drawable.addsup_weekhover);
			btnSun.setTextColor(getResources().getColor(R.color.white_color));
			break;

		default:
			break;
		}
	}

	/*
	 * private void setDataToBtn(String week) { if
	 * (week.equals(getResources().getString(R.string.monday))) {
	 * btnMon.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnMon.setTextColor(getResources().getColor(R.color.white_color));
	 * 
	 * visitDays += getResources().getString(R.string.monday) + ","; } else if
	 * (week.equals(getResources().getString(R.string.tuesday))) {
	 * btnTues.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnTues.setTextColor(getResources().getColor(R.color.white_color));
	 * 
	 * visitDays += getResources().getString(R.string.tuesday) + ","; } else if
	 * (week.equals(getResources().getString(R.string.wedneday))) {
	 * btnWed.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnWed.setTextColor(getResources().getColor(R.color.white_color));
	 * 
	 * visitDays += getResources().getString(R.string.wedneday) + ","; } else if
	 * (week.equals(getResources().getString(R.string.thursday))) {
	 * btnThu.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnThu.setTextColor(getResources().getColor(R.color.white_color));
	 * 
	 * visitDays += getResources().getString(R.string.thursday) + ","; } else if
	 * (week.equals(getResources().getString(R.string.friday))) {
	 * btnFri.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnFri.setTextColor(getResources().getColor(R.color.white_color));
	 * 
	 * visitDays += getResources().getString(R.string.friday) + ","; } else if
	 * (week.equals(getResources().getString(R.string.saturday))) {
	 * btnSat.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnSat.setTextColor(getResources().getColor(R.color.white_color));
	 * 
	 * visitDays += getResources().getString(R.string.saturday) + ","; } else if
	 * (week.equals(getResources().getString(R.string.sunday))) {
	 * btnSun.setBackgroundResource(R.drawable.addsup_weekhover);
	 * btnSun.setTextColor(getResources().getColor(R.color.white_color));
	 * visitDays += getResources().getString(R.string.sunday)+ ","; } }
	 */

	private List<String> getFreqVisits() {

		List<String> visitlist = new ArrayList<String>();

		visitlist.add(getResources().getString(R.string.select));

		visitlist.add(getResources().getString(R.string.weekly));
		visitlist.add(getResources().getString(R.string.fortnightly));

		visitlist.add(getResources().getString(R.string.monthly));

		return visitlist;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_Submit:

			validateAndSave();

			break;

		case R.id.btn_Cancel:

			if (screen.equals(ConstantsEnum.INVENTORY.code())) {
				CommonMethods.startIntent(this, InventoryActivity.class);

			} else if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
				Intent intent = new Intent(this, AddProductActivity.class);
				intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
				startActivity(intent);
				finish();

			} else {
				CommonMethods.startIntent(this, SuppliersActivity.class);
			}
			finish();
			appContext.popActivity();
			break;

		case R.id.btn_WeekMon:

			if (!visitDays.contains("0"))
				setDataToBtn(0);
			else {
				visitDays = visitDays.replace("0,", "");
				btnMon.setBackgroundResource(R.drawable.addsup_week);
				btnMon.setTextColor(getResources().getColor(R.color.black_color));
			}
			break;
		case R.id.btn_WeekTue:

			if (!visitDays.contains("1"))
				setDataToBtn(1);
			else {
				visitDays = visitDays.replace("1,", "");
				btnTues.setBackgroundResource(R.drawable.addsup_week);
				btnTues.setTextColor(getResources().getColor(R.color.black_color));
			}
			break;

		case R.id.btn_WeekWed:

			if (!visitDays.contains("2"))
				setDataToBtn(2);
			else {
				visitDays = visitDays.replace("2,", "");
				btnWed.setBackgroundResource(R.drawable.addsup_week);
				btnWed.setTextColor(getResources().getColor(R.color.black_color));
			}
			break;

		case R.id.btn_WeekThu:

			if (!visitDays.contains("3"))
				setDataToBtn(3);
			else {
				visitDays = visitDays.replace("3,", "");
				btnThu.setBackgroundResource(R.drawable.addsup_week);
				btnThu.setTextColor(getResources().getColor(R.color.black_color));
			}
			break;

		case R.id.btn_WeekFri:

			if (!visitDays.contains("4"))
				setDataToBtn(4);
			else {
				visitDays = visitDays.replace("4,", "");
				btnFri.setBackgroundResource(R.drawable.addsup_week);
				btnFri.setTextColor(getResources().getColor(R.color.black_color));
			}
			break;

		case R.id.btn_WeekSat:

			if (!visitDays.contains("5"))
				setDataToBtn(5);
			else {
				visitDays = visitDays.replace("5,", "");
				btnSat.setBackgroundResource(R.drawable.addsup_week);
				btnSat.setTextColor(getResources().getColor(R.color.black_color));
			}

			break;

		case R.id.btn_WeekSun:

			if (!visitDays.contains("6"))
				setDataToBtn(6);
			else {
				visitDays = visitDays.replace("6,", "");
				btnSun.setBackgroundResource(R.drawable.addsup_week);
				btnSun.setTextColor(getResources().getColor(R.color.black_color));
			}
			break;

		default:
			break;
		}
		System.out.println("Visit Days :" + visitDays);
	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validationFields(stringBuffer))
			CommonMethods.displayAlert(true, getResources().getString(R.string.alert), stringBuffer.toString(),
					getResources().getString(R.string.ok), "", AddSupplierActivity.this, null, false);
		else
			saveDialog = CommonMethods.displayAlert(true, getResources().getString(R.string.confirm),
					getResources().getString(R.string.save_messgae), getResources().getString(R.string.yes),
					getResources().getString(R.string.no), AddSupplierActivity.this, AddSupplierActivity.this, false);
	}

	private boolean validationFields(StringBuffer stringBuffer) {

		name = etxtSupName.getText().toString().trim();
		nit = etxtSupNit.getText().toString().trim();
		contactname = etxtContactname.getText().toString().trim();
		contactNumber = etxtcontactNum.getText().toString().trim();
		address = etxtSupAdd.getText().toString().trim();

		phoneNum = etxtSupPhoneNum.getText().toString().trim();
		freqVisit = spnFreqVisit.getSelectedItem().toString().trim();

		isValid = true;

		if (name.length() == 0) {
			isValid = false;
			stringBuffer.append(getResources().getString(R.string.enter_name));
		}
		if (nit.length() == 0) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.enter_supplier_nit));
		}

		if (address.length() == 0) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.enter_address));
		}
		if (phoneNum.length() == 0) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.enter_phonenum));
		} else if (phoneNum.length() < 7) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.enter_validphonenum));
		}
		if (contactNumber.length() != 0 && contactNumber.length() < 7) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.enter_validcontactnum));
		}
		if (freqVisit.equals(getResources().getString(R.string.select))) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.select_freqvisit));
		}
		if ("".equals(visitDays)) {
			isValid = false;
			stringBuffer.append("\n" + getResources().getString(R.string.select_days));
		}
		return isValid;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		dialog.dismiss();
		if (dialog == saveDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				if (screen.equals(ConstantsEnum.EDIT_SUPPLIER.code())) {
					if (updateRecordinDB()) {
						

						System.out.println("Update==========>>>>>>>");
						if (NetworkConnectivity.netWorkAvailability(AddSupplierActivity.this)) {
							new AddOrUpdateCustomer().execute("Update");
						} else {
							CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));

							CommonMethods.startIntent(this, SuppliersActivity.class);
							finish();
							appContext.popActivity();
						
						}
					
						
						
//						if (screen.equals(ConstantsEnum.INVENTORY.code())) {
//							appContext.popActivity();
//							CommonMethods.startIntent(this, InventoryActivity.class);
//							finish();
//						} else if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
//							Intent intent = new Intent(this, AddProductActivity.class);
//							intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
//							startActivity(intent);
//							finish();
//							appContext.popActivity();
//						} else {
//							CommonMethods.startIntent(this, SuppliersActivity.class);
//							finish();
//							appContext.popActivity();
//						}

					}

				} else {
					List<String> supplierIDsList = SupplierDAO.getInstance()
							.getSupplierIDs(DBHandler.getDBObj(Constants.READABLE));

					if (!supplierIDsList.contains(nit)) {
						if (insertRecordInDB()) {
							if (NetworkConnectivity.netWorkAvailability(AddSupplierActivity.this)) {
								new AddOrUpdateCustomer().execute("insert");
							} else {
								CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));

								if (screen.equals(ConstantsEnum.INVENTORY.code())) {
									appContext.popActivity();
									CommonMethods.startIntent(AddSupplierActivity.this, InventoryActivity.class);
									finish();

								} else if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
									Intent intent = new Intent(AddSupplierActivity.this, AddProductActivity.class);
									intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
									startActivity(intent);
									finish();
									appContext.popActivity();
								} else {
									CommonMethods.startIntent(AddSupplierActivity.this, SuppliersActivity.class);
									finish();
									appContext.popActivity();
								}

							
							}
						}
					} else
						CommonMethods.showCustomToast(AddSupplierActivity.this,
								getResources().getString(R.string.supplier_id_exist));
				}
			}
		}

	}

	private boolean updateRecordinDB() {
		SupplierDTO supdto = SupplierDAO.getInstance().getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE),
				nit);
		if (SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), getUpdatedDataFromViews())) {
//			if (new AsynkTaskClass().callcentralserver("/supplier/update", "" + ServiceHandler.POST,
//					makejsonobj(false).toString().trim(), AddSupplierActivity.this) != null) {
//				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
//					if (ServiApplication.connectionTimeOutState) {
//						CommonMethods.showCustomToast(AddSupplierActivity.this,
//								getResources().getString(R.string.supplier_update));
//						SupplierDAO.getInstance().changesynckstatus(DBHandler.getDBObj(Constants.WRITABLE), supdto);
//					}
//					return true;
//				} else {
//					return true;
//				}
//			} else {
//
//				CommonMethods.showCustomToast(AddSupplierActivity.this,
//						getResources().getString(R.string.sucessfully_change));
//				return true;
//			}
			
			return true;

		} else
			CommonMethods.showCustomToast(AddSupplierActivity.this, getResources().getString(R.string.not_added));

		return false;
	}

	private SupplierDTO getUpdatedDataFromViews() {

		SupplierDTO supDTO = new SupplierDTO();
		supDTO.setSupplierId(supplierDto.getSupplierId());
		supDTO.setName(name);
		supDTO.setAddress(address);
		supDTO.setTelephone(phoneNum);
		supDTO.setVisitFrequency(getFreqVisitNum(freqVisit));
		supDTO.setVisitDays(visitDays);
		supDTO.setContactName(contactname);
		supDTO.setContactTelephone(contactNumber);
		supDTO.setModifiedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		supDTO.setSyncStatus(0);
		return supDTO;
	}

	private String getFreqVisitNum(String freqVisit) {
		String visitNum = "";
		if (freqVisit.equalsIgnoreCase(getResources().getString(R.string.weekly)))
			visitNum = "1";
		else if (freqVisit.equalsIgnoreCase(getResources().getString(R.string.fortnightly)))
			visitNum = "2";
		else
			visitNum = "3";

		System.out.println("Freq visits :" + freqVisit);
		System.out.println("Visit Number :" + visitNum);

		return visitNum;
	}

	private boolean insertRecordInDB() {

		if (SupplierDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), getDataFromViews())) {
			bindDatatoproduct();
//			if (new AsynkTaskClass().callcentralserver("/supplier/create", "" + ServiceHandler.POST, makejsonobj(true),
//					AddSupplierActivity.this) != null) {
//				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
//					if (ServiApplication.connectionTimeOutState) {
//						SupplierDTO dto = SupplierDAO.getInstance()
//								.getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE), nit);
//						dto.setSyncStatus(1);
//						SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dto);
//
//						CommonMethods.showCustomToast(AddSupplierActivity.this,
//								getResources().getString(R.string.added_success));
//					}
//					return true;
//				} else {
//					CommonMethods.showCustomToast(AddSupplierActivity.this,
//							getResources().getString(R.string.added_success));
//					return true;
//				}
//			} else {
//				CommonMethods.showCustomToast(AddSupplierActivity.this,
//						getResources().getString(R.string.added_success));
//				return true;
//			}
			return true;
		} else

			CommonMethods.showCustomToast(AddSupplierActivity.this, getResources().getString(R.string.not_added));

		return false;
	}

	private List<DTO> getDataFromViews() {

		List<DTO> supplierList = new ArrayList<DTO>();
		SupplierDTO supDTO = new SupplierDTO();
		supDTO.setName(name);
		supDTO.setCedula(nit);
		supDTO.setAddress(address);
		supDTO.setTelephone(phoneNum);
		supDTO.setVisitFrequency(getFreqVisitNum(freqVisit));
		supDTO.setVisitDays(visitDays);
		supDTO.setContactName(contactname);
		supDTO.setBalanceAmount(BalanceAmount);
		supDTO.setContactTelephone(contactNumber);
		supDTO.setCreatedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		supDTO.setSyncStatus(0);
		supplierList.add(supDTO);
		return supplierList;
	}

	@Override
	public void onBackPressed() {
		if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
			Intent intent = new Intent(this, AddProductActivity.class);
			intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
			startActivity(intent);
			finish();
		} else {
			finish();
			appContext.popActivity();
		}
	}

	private String makejsonobj(boolean b) {
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		SupplierDTO supdto = SupplierDAO.getInstance().getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE),
				nit);
		JSONObject jsonobj = new JSONObject();
		try {

			if ("" != ServiApplication.store_id) {
				jsonobj.put("supplier_code", nit);
				jsonobj.put("name", name);
				jsonobj.put("logo", "");
				jsonobj.put("address", address);
				jsonobj.put("telephone", phoneNum);
				jsonobj.put("contact_name", contactname);
				jsonobj.put("contact_telephone", contactNumber);
				jsonobj.put("visit_days", visitDays);
				jsonobj.put("visit_frequency", getFreqVisitNum(freqVisit));
				jsonobj.put("active_status", "true");
				if (b) {
					jsonobj.put("modified_by", ServiApplication.userName);
				} else {
					jsonobj.put("created_by", ServiApplication.userName);
				}

				try {
					jsonobj.put("created_date", Dates.serverdateformate(supdto.getCreatedDate()));
				} catch (Exception e) {

				}

				try {
					jsonobj.put("modified_date", Dates.serverdateformate(supdto.getModifiedDate()));
				} catch (Exception e) {

				}

				try {
					jsonobj.put("last_payment_date", Dates.serverdateformate(supdto.getLastPaymentDate()));
				} catch (Exception e) {

				}
				jsonobj.put("balance_amount", BalanceAmount);
				jsonobj.put("store_code", ServiApplication.store_id);
			}
			return jsonobj.toString();
		} catch (JSONException e) {
			return null;
		}
	}

	private void bindDatatoproduct() {
		if (ServiApplication.add_sup_product) {
			ProductDTO dto = ServiApplication.prodto;
			dto.setSupplierId(nit);
			dto.setSupplierName(name);
			ServiApplication.prodto = dto;
		}
	}
	
	
	public class AddOrUpdateCustomer extends AsyncTask<String, Void, Void> {
		String responds, params_string;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CommonMethods.showProgressDialog(getResources().getString(R.string.please_wait), AddSupplierActivity.this);

		}

		@Override
		protected Void doInBackground(String... params) {
			params_string = params[0];
			ServiceHandler servicehandler = new ServiceHandler(AddSupplierActivity.this);
			if (params[0].equals("Update")) {
				responds = servicehandler.makeServiceCall(ServiApplication.URL + "/supplier/update",
						ServiceHandler.POST, makejsonobj(true));
			} else {
				responds = servicehandler.makeServiceCall(ServiApplication.URL + "/supplier/create",
						ServiceHandler.POST, makejsonobj(false));

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			SupplierDTO cdto = SupplierDAO.getInstance()
					.getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE), nit);
			SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), cdto);
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(responds) == 0) {
					cdto.setSyncStatus(1);

					SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), cdto);
					if (params_string.equals("Update")) {
						CommonMethods.showCustomToast(AddSupplierActivity.this,
								getResources().getString(R.string.sucessfully_change));
						if (screen.equals(ConstantsEnum.INVENTORY.code())) {
							appContext.popActivity();
							CommonMethods.startIntent(AddSupplierActivity.this, InventoryActivity.class);
							finish();

						} else if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
							Intent intent = new Intent(AddSupplierActivity.this, AddProductActivity.class);
							intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
							startActivity(intent);
							finish();
							appContext.popActivity();
						} else {
							CommonMethods.startIntent(AddSupplierActivity.this, SuppliersActivity.class);
							finish();
							appContext.popActivity();
						}

					} else {
						if (screen.equals(ConstantsEnum.INVENTORY.code())) {
							appContext.popActivity();
							CommonMethods.startIntent(AddSupplierActivity.this, InventoryActivity.class);
							finish();

						} else if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
							Intent intent = new Intent(AddSupplierActivity.this, AddProductActivity.class);
							intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
							startActivity(intent);
							finish();
							appContext.popActivity();
						} else {
							CommonMethods.startIntent(AddSupplierActivity.this, SuppliersActivity.class);
							finish();
							appContext.popActivity();
						}
						CommonMethods.showCustomToast(AddSupplierActivity.this,getResources().getString(R.string.added_success));
					}
				} else {
					cdto.setSyncStatus(0);
					SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), cdto);
					CommonMethods.showCustomToast(AddSupplierActivity.this,
							getResources().getString(R.string.sync_error_msg));
				}
			} else {
				CommonMethods.showCustomToast(AddSupplierActivity.this,getResources().getString(R.string.sync_error_msg));
				if (screen.equals(ConstantsEnum.INVENTORY.code())) {
					appContext.popActivity();
					CommonMethods.startIntent(AddSupplierActivity.this, InventoryActivity.class);
					finish();

				} else if (screen.equals(ConstantsEnum.PRODUCT_SUPPLIER.code())) {
					Intent intent = new Intent(AddSupplierActivity.this, AddProductActivity.class);
					intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(), ConstantsEnum.ADD_PRODUCT.code());
					startActivity(intent);
					finish();
					appContext.popActivity();
				} else {
					CommonMethods.startIntent(AddSupplierActivity.this, SuppliersActivity.class);
					finish();
					appContext.popActivity();
				}
			}
			CommonMethods.dismissProgressDialog();
			
		}

	}

}
