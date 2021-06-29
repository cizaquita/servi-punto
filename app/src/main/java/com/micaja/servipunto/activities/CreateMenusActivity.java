package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.CreateMenusAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.MenuDAO;
import com.micaja.servipunto.database.dao.MenuDishesDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.MenuDTO;
import com.micaja.servipunto.database.dto.MenuDishesDTO;
import com.micaja.servipunto.database.dto.MenusEditDTO;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.SalesCodes;

public class CreateMenusActivity extends BaseActivity implements
		OnClickListener, OnItemSelectedListener {

	private ListView lvMenusDish;
	private Button btnCreateMenus, btnAddDish, btnMenusSave, btnWeekMon,
			btnWeekTue, btnWeekWed, btnWeekThu, btnWeekFri, btnWeekSat,
			btnWeekSun, btnInventory;
	private Spinner spnFiletr, spnMenusType;
	private TextView txtTitel;
	private static EditText etxtStartDate, etxtEndDate;

	private EditText etxtMenusName;

	private ArrayAdapter<String> menusTypeAdapter;
	private List<DTO> dishesList, menusList;

	private String menusName, menusType, weekDays = "", menuType;

	private RelativeLayout relWeeks, relOccasional;
	private List<String> weeksList;
	private static View dateView;
	private String editMenuId = "", startDate, endDate, menuID;
	private boolean isDishAdded;
	private static JSONObject MenuJsonObj;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, CreateMenusActivity.class);

		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.create_menus);

		lvMenusDish = (ListView) findViewById(R.id.lv_MenusDish);
		btnAddDish = (Button) findViewById(R.id.btn_CreateDish);
		txtTitel = (TextView) findViewById(R.id.txt_HeaderTitle);
		btnMenusSave = (Button) findViewById(R.id.btn_MenusSave);
		spnMenusType = (Spinner) findViewById(R.id.spn_MenusType);
		etxtMenusName = (EditText) findViewById(R.id.etxt_MenusName);
		relWeeks = (RelativeLayout) findViewById(R.id.rel_MenusWeek);
		relOccasional = (RelativeLayout) findViewById(R.id.rel_MenusOccasional);
		btnWeekMon = (Button) findViewById(R.id.btn_WeekMon);
		btnWeekTue = (Button) findViewById(R.id.btn_WeekTue);
		btnWeekWed = (Button) findViewById(R.id.btn_WeekWed);
		btnWeekThu = (Button) findViewById(R.id.btn_WeekThu);
		btnWeekFri = (Button) findViewById(R.id.btn_WeekFri);
		btnWeekSat = (Button) findViewById(R.id.btn_WeekSat);
		btnWeekSun = (Button) findViewById(R.id.btn_WeekSun);
		etxtStartDate = (EditText) findViewById(R.id.etxt_Startdate);
		etxtEndDate = (EditText) findViewById(R.id.etxt_Enddate);
		btnInventory = (Button) findViewById(R.id.btn_MenuInvantory);
		btnInventory.setVisibility(View.INVISIBLE);

		btnAddDish.setText(getResources().getString(R.string.add_menus_item));
		txtTitel.setText(getResources().getString(R.string.create_menu_titel));

		btnAddDish.setOnClickListener(this);
		btnMenusSave.setOnClickListener(this);
		btnWeekMon.setOnClickListener(this);
		btnWeekTue.setOnClickListener(this);
		btnWeekWed.setOnClickListener(this);
		btnWeekThu.setOnClickListener(this);
		btnWeekFri.setOnClickListener(this);
		btnWeekSat.setOnClickListener(this);
		btnWeekSun.setOnClickListener(this);

		etxtStartDate.setOnClickListener(this);
		etxtEndDate.setOnClickListener(this);

		spnMenusType.setOnItemSelectedListener(this);

		setViewsInvis();
		loadUI();
		getDataFromDB();

	}

	private void getDataFromDB() {
		if (getIntent().getExtras() != null) {
			menusList = MenuDAO.getInstance().getRecordsWithValues(
					DBHandler.getDBObj(Constants.READABLE),
					"menu_id",
					getIntent().getExtras().getString(
							ConstantsEnum.EDIT_DISH.code()));

			dishesList = MenuDishesDAO.getInstance().getEditMenusRecords(
					DBHandler.getDBObj(Constants.READABLE),
					getIntent().getExtras().getString(
							ConstantsEnum.EDIT_DISH.code()));

			if (menusList.size() != 0 && dishesList.size() != 0)
				setDataToViews();
		} else
			appContext.setMenusDishesList(new ArrayList<DTO>());
	}

	private void setDataToViews() {
		MenuDTO menuDTO = (MenuDTO) menusList.get(0);

		etxtMenusName.setText(menuDTO.getName());
		spnMenusType
				.setSelection((Integer.valueOf(menuDTO.getMenuTypeId())) + 1);
		editMenuId = menuDTO.getMenuId();

		relOccasional.setVisibility(View.GONE);
		relWeeks.setVisibility(View.GONE);

		appContext.setMenusDishesList(prepareDishDTO(dishesList));

		if (!"".equals(menuDTO.getWeekDays()))
			enableWeekBtns(menuDTO);

		if (!"".equals(menuDTO.getStartDate())
				&& !"".equals(menuDTO.getEndDate()))
			enableDateBtns(menuDTO);

		etxtMenusName.setEnabled(false);

		updateListView();
	}

	private List<DTO> prepareDishDTO(List<DTO> dishesList) {
		List<DTO> list = new ArrayList<DTO>();

		for (int i = 0; i < dishesList.size(); i++) {
			DishesDTO dishesDTO = new DishesDTO();
			MenusEditDTO editDTO = (MenusEditDTO) dishesList.get(i);
			dishesDTO.setDishId(editDTO.getDishId());
			dishesDTO.setDishName(editDTO.getDishName());
			dishesDTO.setDishPrice(editDTO.getDishPrice());
			dishesDTO.setExpiryDays(editDTO.getExpiryDays());
			dishesDTO.setNoOfItems(editDTO.getNoOfItems());
			dishesDTO.setSellingCostperItem(editDTO.getSellingPricePerItem());
			dishesDTO.setVat(editDTO.getVat());
			dishesDTO.setSyncStatus(0);

			list.add(dishesDTO);
		}

		return list;
	}

	private void enableDateBtns(MenuDTO menuDTO) {
		relOccasional.setVisibility(View.VISIBLE);
		etxtStartDate.setText(menuDTO.getStartDate());
		etxtEndDate.setText(menuDTO.getEndDate());
	}

	// Result of this method,get the length of week days
	private void enableWeekBtns(MenuDTO menuDTO) {
		relWeeks.setVisibility(View.VISIBLE);
		String[] weeks = menuDTO.getWeekDays().split(",");
		for (int i = 0; i < weeks.length; i++) {

			try {
				setDataToBtn(Integer.parseInt(weeks[i]));
			} catch (Exception e) {

				setDataToBtn(Integer.parseInt("0"));

			}
		}
	}

	// Result of this method,week_days click events
	private void setDataToBtn(int week) {
		switch (week) {
		case 0:
			weeksList.add("0");
			btnWeekMon.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekMon.setTextColor(getResources()
					.getColor(R.color.white_color));
			break;

		case 1:
			weeksList.add("1");
			btnWeekTue.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekTue.setTextColor(getResources()
					.getColor(R.color.white_color));

			break;
		case 2:
			weeksList.add("2");
			btnWeekWed.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekWed.setTextColor(getResources()
					.getColor(R.color.white_color));
			break;
		case 3:
			weeksList.add("3");
			btnWeekThu.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekThu.setTextColor(getResources()
					.getColor(R.color.white_color));
			break;
		case 4:
			weeksList.add("4");
			btnWeekFri.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekFri.setTextColor(getResources()
					.getColor(R.color.white_color));
			break;
		case 5:
			weeksList.add("5");
			btnWeekSat.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekSat.setTextColor(getResources()
					.getColor(R.color.white_color));
			break;

		case 6:
			weeksList.add("6");
			btnWeekSun.setBackgroundResource(R.drawable.addsup_weekhover);
			btnWeekSun.setTextColor(getResources()
					.getColor(R.color.white_color));
			break;

		default:
			break;
		}
	}

	private void setViewsInvis() {
		spnFiletr = (Spinner) findViewById(R.id.spn_MenusFilter);
		btnCreateMenus = (Button) findViewById(R.id.btn_CreateMenus);

		btnCreateMenus.setVisibility(View.INVISIBLE);
		spnFiletr.setVisibility(View.INVISIBLE);
	}

	// Result of this method,showing menu types dropdown
	private void loadUI() {
		weeksList = new ArrayList<String>();

		menusTypeAdapter = new ArrayAdapter<String>(CreateMenusActivity.this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.menu_type));
		menusTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnMenusType.setAdapter(menusTypeAdapter);

	}

	@Override
	public void onClick(View view) {
		DatePicker datePicker = new DatePicker();
		switch (view.getId()) {
		case R.id.btn_CreateDish:
			Intent intent = new Intent(CreateMenusActivity.this,
					DishesSeleActivity.class);
			appContext
					.pushActivity(new Intent(this, CreateMenusActivity.class));
			startActivityForResult(intent, SalesCodes.SELECT_DISH.code());
			break;

		case R.id.btn_MenusSave:
			if (appContext.getMenusDishesList().size() != 0)
				validateAndInsert();
			else
				CommonMethods.showCustomToast(CreateMenusActivity.this,
						getResources().getString(R.string.select_dishes));
			break;

		case R.id.btn_WeekMon:
			if (!weeksList.contains("0"))
				setDataToBtn(0);
			else {
				weeksList.remove("0");
				btnWeekMon.setBackgroundResource(R.drawable.addsup_week);
				btnWeekMon.setTextColor(getResources().getColor(
						R.color.black_color));
			}

			break;
		case R.id.btn_WeekTue:
			if (!weeksList.contains("1"))
				setDataToBtn(1);
			else {
				weeksList.remove("1");
				btnWeekTue.setBackgroundResource(R.drawable.addsup_week);
				btnWeekTue.setTextColor(getResources().getColor(
						R.color.black_color));
			}
			break;

		case R.id.btn_WeekWed:
			if (!weeksList.contains("2"))
				setDataToBtn(2);
			else {
				weeksList.remove("2");
				btnWeekWed.setBackgroundResource(R.drawable.addsup_week);
				btnWeekWed.setTextColor(getResources().getColor(
						R.color.black_color));
			}
			break;

		case R.id.btn_WeekThu:
			if (!weeksList.contains("3"))
				setDataToBtn(3);
			else {
				weeksList.remove("3");
				btnWeekThu.setBackgroundResource(R.drawable.addsup_week);
				btnWeekThu.setTextColor(getResources().getColor(
						R.color.black_color));
			}

			break;

		case R.id.btn_WeekFri:

			if (!weeksList.contains("4"))
				setDataToBtn(4);
			else {
				weeksList.remove("4");
				btnWeekFri.setBackgroundResource(R.drawable.addsup_week);
				btnWeekFri.setTextColor(getResources().getColor(
						R.color.black_color));
			}

			break;

		case R.id.btn_WeekSat:

			if (!weeksList.contains("5"))
				setDataToBtn(5);
			else {
				weeksList.remove("5");
				btnWeekSat.setBackgroundResource(R.drawable.addsup_week);
				btnWeekSat.setTextColor(getResources().getColor(
						R.color.black_color));
			}

			break;

		case R.id.btn_WeekSun:

			if (!weeksList.contains("6"))
				setDataToBtn(6);
			else {
				weeksList.remove("6");
				btnWeekSun.setBackgroundResource(R.drawable.addsup_week);
				btnWeekSun.setTextColor(getResources().getColor(
						R.color.black_color));
			}

			break;

		case R.id.etxt_Startdate:
			dateView = view;
			datePicker.show(getFragmentManager(), "datePicker");
			break;

		case R.id.etxt_Enddate:
			dateView = view;
			datePicker.show(getFragmentManager(), "datePickerEnd");
			break;

		default:
			break;
		}
	}

	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndInsert() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validate(stringBuffer))
			CommonMethods.showcustomdialogbox(CreateMenusActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		else {
			if (getIntent().getExtras() != null)
				updateRecordsMenu();
			else
				insertRecords();
		}
	}

	private void insertMenuDish() {
		// List<DTO> list = getMenusDishList(isDishAdded);
		// if(list.size() != 0)
		// {
		if (MenuDishesDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE),
				getMenusDishList(isDishAdded))) {
			navigateBackScreen("insert");
		}
		/*
		 * } else navigateBackScreen();
		 */

	}

	private void insertRecords() {
		if (MenuDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getMenusList()))
			insertMenuDish();
	}

	private void updateRecordsMenu() {
		if (MenuDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE), getMenusList().get(0))) {
			if (isDishAdded)
				insertMenuDish();
			else
				navigateBackScreen("update");
		}
	}

	// Result of this method, navigateBackScreen
	private void navigateBackScreen(String type) {
		if ("insert".equals(type)) {
			if (synkCallInsert()) {
				CommonMethods.showCustomToast(CreateMenusActivity.this,
						getResources().getString(R.string.menu_insert_msg));
				appContext.setMenusDishesList(new ArrayList<DTO>());
				appContext.setTotalAmount(0);
				// CommonMethods.openNewActivity(CreateMenusActivity.this,MenusActivity.class);
				goBackScreen();
				this.finish();
			} else {
				CommonMethods.showCustomToast(CreateMenusActivity.this,
						getResources().getString(R.string.menu_insert_msg));
				appContext.setMenusDishesList(new ArrayList<DTO>());
				appContext.setTotalAmount(0);
				CommonMethods.openNewActivity(CreateMenusActivity.this,
						MenusActivity.class);
				goBackScreen();
				this.finish();
			}
		} else if ("update".equals(type)) {
			if (synkCallUpdate()) {
				CommonMethods.showCustomToast(CreateMenusActivity.this,
						getResources().getString(R.string.menu_edit_msg));
				appContext.setMenusDishesList(new ArrayList<DTO>());
				appContext.setTotalAmount(0);
				// CommonMethods.openNewActivity(CreateMenusActivity.this,MenusActivity.class);
				goBackScreen();
				this.finish();
			} else {
				CommonMethods.showCustomToast(CreateMenusActivity.this,
						getResources().getString(R.string.menu_edit_msg));
				appContext.setMenusDishesList(new ArrayList<DTO>());
				appContext.setTotalAmount(0);
				// CommonMethods.openNewActivity(CreateMenusActivity.this,MenusActivity.class);
				goBackScreen();
				this.finish();
			}
		}

	}

	private void goBackScreen() {
		if (appContext.getActivityListSize() > 0) {
			appContext.getActivityList()
					.get(appContext.getActivityListSize() - 1)
					.putExtra(getResources().getString(R.string.back), "true");
			startActivity(appContext.getActivityList().get(
					appContext.getActivityListSize() - 1));
			finish();
			appContext.popActivity();
		}
	}

	// This method using for Update Menu
	private boolean synkCallUpdate() {
		List<DTO> list = MenuDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		MenuDTO menudto = (MenuDTO) list.get(list.size() - 1);
		if (new AsynkTaskClass().callcentralserver("/menu/update", ""
				+ ServiceHandler.PUT, makeMenuJSONObj(menudto),
				CreateMenusActivity.this) != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				MenuDTO mdto = menudto;
				mdto.setSyncStatus(1);
				MenuDAO.getInstance().update(
						DBHandler.getDBObj(Constants.WRITABLE), mdto);
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	// This method using for Create Menu
	private boolean synkCallInsert() {
		List<DTO> list = MenuDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		MenuDTO menudto = (MenuDTO) list.get(list.size() - 1);

		if (new AsynkTaskClass().callcentralserver("/menu/create", ""
				+ ServiceHandler.POST, makeMenuJSONObj(menudto),
				CreateMenusActivity.this) != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				List<DTO> list1 = MenuDAO.getInstance().getRecords(
						DBHandler.getDBObj(Constants.WRITABLE));
				MenuDTO mdto = (MenuDTO) list1.get(list1.size() - 1);
				mdto.setSyncStatus(1);
				MenuDAO.getInstance().update(
						DBHandler.getDBObj(Constants.WRITABLE), mdto);
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	// This method using for Create and Update Menu that time data passing
	// to the json objects
	private String makeMenuJSONObj(MenuDTO menudto) {
		JSONObject jsonobj = new JSONObject();
		JSONArray jsonarrayobj = new JSONArray();
		try {
			jsonobj.put("menu_id", Long.parseLong(menudto.getMenuId()));
			jsonobj.put("menu_type_id", Long.parseLong(menudto.getMenuTypeId()));
			jsonobj.put("name", menudto.getName());
			if (menudto.getStartDate().length() > 0) {
				jsonobj.put("start_date", menudto.getStartDate());
			}
			if (menudto.getEndDate().length() > 0) {
				jsonobj.put("end_date", menudto.getEndDate());
			}
			jsonobj.put("week_days", menudto.getWeekDays());
			jsonobj.put("store_code", ServiApplication.store_id);

			List<DTO> list = MenuDishesDAO.getInstance().getRecordsWithValues(
					DBHandler.getDBObj(Constants.READABLE), "menu_id",
					menudto.getMenuId());
			for (DTO dto : list) {
				MenuDishesDTO menudishdto = (MenuDishesDTO) dto;
				JSONObject jsonobj1 = new JSONObject();
				jsonobj1.put("dish_id", Long.parseLong(menudishdto.getDishId()));
				jsonobj1.put("menu_dishes_id",
						Long.parseLong(menudishdto.getMenuDishesId()));
				jsonobj1.put("menu_id", Long.parseLong(menudishdto.getMenuId()));
				jsonobj1.put("store_code", ServiApplication.store_id);
				jsonarrayobj.put(jsonobj1);
			}
			jsonobj.put("dishes", jsonarrayobj);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	// Result of this method, getting menu dish list
	private List<DTO> getMenusDishList(boolean isDishAdded) {
		List<DTO> list = new ArrayList<DTO>();
		List<DTO> selectList = appContext.getMenusDishesList();
		List<String> dishesList = MenuDishesDAO.getInstance()
				.getMenuDishRecords(DBHandler.getDBObj(Constants.READABLE),
						menuID);

		for (int i = 0; i < selectList.size(); i++) {
			DishesDTO dishesDTO = (DishesDTO) selectList.get(i);
			if (!dishesList.contains(dishesDTO.getDishId())) {
				MenuDishesDTO menuDishesDTO = new MenuDishesDTO();
				menuDishesDTO.setDishId(dishesDTO.getDishId());
				menuDishesDTO.setMenuId(menuID);
				menuDishesDTO.setSyncStatus(0);
				list.add(menuDishesDTO);
			}
		}
		return list;
	}

	// Bind new menu data to BindDataTo MenuDTO
	private List<DTO> getMenusList() {
		List<DTO> list = new ArrayList<DTO>();
		validateType();
		MenuDTO dto = new MenuDTO();

		if (!"".equals(editMenuId))
			menuID = editMenuId;
		else
			menuID = String.valueOf(Dates.getSysDateinMilliSeconds());

		dto.setMenuId(menuID);
		dto.setEndDate(etxtEndDate.getText().toString().trim());
		dto.setMenuTypeId(menuType);
		dto.setName(menusName);
		dto.setStartDate(etxtStartDate.getText().toString().trim());
		dto.setSyncStatus(0);
		dto.setWeekDays(weekDays);
		list.add(dto);
		return list;
	}

	// Result of this method,get the position of menu_types
	private void validateType() {
		if (weeksList.size() != 0) {
			for (int i = 0; i < weeksList.size(); i++) {
				if ("".equals(weekDays))
					weekDays = weeksList.get(i);
				else
					weekDays = weekDays + "," + weeksList.get(i);

			}

		} else
			weekDays = "";

		if (spnMenusType.getSelectedItem().toString().trim()
				.equals(getResources().getString(R.string.menus_types_daily)))
			menuType = "0";
		else if (spnMenusType.getSelectedItem().toString().trim()
				.equals(getResources().getString(R.string.menus_types_weekly)))
			menuType = "1";
		else
			menuType = "2";
	}

	private List<DTO> getUpdateDishList() {
		List<DTO> list = new ArrayList<DTO>();

		List<DTO> selectList = appContext.getMenusDishesList();

		for (int i = 0; i < selectList.size(); i++) {
			DishesDTO dishesDTO = (DishesDTO) selectList.get(i);

			MenusEditDTO dto = (MenusEditDTO) dishesList.get(i);

			MenuDishesDTO menuDishesDTO = new MenuDishesDTO();

			menuDishesDTO.setMenuDishesId(dto.getMenuDishId());
			menuDishesDTO.setDishId(dishesDTO.getDishId());
			menuDishesDTO.setMenuId(editMenuId);
			menuDishesDTO.setSyncStatus(0);

			list.add(menuDishesDTO);
		}

		return list;
	}

	// This method using for Validation purpose
	private boolean validate(StringBuffer stringBuffer) {
		menusName = etxtMenusName.getText().toString().trim();
		menusType = spnMenusType.getSelectedItem().toString().trim();
		startDate = etxtStartDate.getText().toString().trim();
		endDate = etxtEndDate.getText().toString().trim();

		boolean isValid = true;

		if (menusName.length() == 0 || "0".equals(menusName)) {
			isValid = false;
			stringBuffer.append(getResources().getString(
					R.string.enter_menus_name));
		} else if (getIntent().getExtras() == null) {
			String name = MenuDAO.getInstance().isMenuNameExist(
					DBHandler.getDBObj(Constants.READABLE), menusName);

			if (!"".equalsIgnoreCase(name) && menusName.equalsIgnoreCase(name)) {
				isValid = false;
				stringBuffer.append(getResources().getString(
						R.string.menu_name_exist));
			}
		}

		if (menusType.equals(getResources().getString(
				R.string.select_menus_type))) {
			isValid = false;
			stringBuffer.append(getResources().getString(
					R.string.select_menus_type));
		} else if (menusType.equals(getResources().getString(
				R.string.menus_types_Occasionally))) {
			/*
			 * if (Dates.featureDateValidation(startDate)) { isValid = false;
			 * stringBuffer
			 * .append(getResources().getString(R.string.invalid_start_date)); }
			 */

			if (Dates.getDifferenceInDays(startDate, endDate) < 0) {
				isValid = false;
				stringBuffer.append(getResources().getString(
						R.string.invalid_occasionally_date));
			}
		} else if (menusType.equals(getResources().getString(
				R.string.menus_types_weekly))) {
			if (weeksList.size() == 0) {
				isValid = false;
				stringBuffer.append(getResources().getString(
						R.string.select_weekdays));
			}
		}

		return isValid;
	}

	// Result of this method, getting  From_date and To_date in DatePicker
	public static class DatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;

			if (dateView.getId() == R.id.etxt_Startdate) {
				etxtStartDate.setText(date);
			} else {
				etxtEndDate.setText(date);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		appContext.popActivity();

		if (requestCode == SalesCodes.SELECT_DISH.code()) {
			isDishAdded = true;
			updateListView();
		}

	}

	private void updateListView() {
		lvMenusDish.setAdapter(new CreateMenusAdapter(CreateMenusActivity.this,
				appContext.getMenusDishesList()));
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long id) {
		if (adapter.getAdapter() == menusTypeAdapter) {
			if (spnMenusType
					.getSelectedItem()
					.toString()
					.trim()
					.equals(getResources()
							.getString(R.string.select_menus_type))) {
				relWeeks.setVisibility(View.GONE);
				relOccasional.setVisibility(View.GONE);
			} else if (spnMenusType
					.getSelectedItem()
					.toString()
					.trim()
					.equals(getResources().getString(
							R.string.menus_types_weekly))) {
				relWeeks.setVisibility(View.VISIBLE);
				relOccasional.setVisibility(View.GONE);
			} else if (spnMenusType
					.getSelectedItem()
					.toString()
					.trim()
					.equals(getResources()
							.getString(R.string.menus_types_daily))) {
				relWeeks.setVisibility(View.GONE);
				relOccasional.setVisibility(View.GONE);
			} else {
				relWeeks.setVisibility(View.GONE);
				relOccasional.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}