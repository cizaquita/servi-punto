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
import android.widget.ListView;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.DishesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.MenuDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.MenusFilterDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class MenusActivity extends BaseActivity implements OnClickListener,
		OnItemSelectedListener {

	private ListView lvMenusDish;
	private Button btnCreateMenus, btnCreateDish, btnMenuInventory;
	private Spinner spnFilter;

	private ArrayAdapter<String> filterAdapter;
	private Hashtable<String, String> filterTable;
	private DishesAdapter dishesAdapter;

	private List<DTO> dishesList = new ArrayList<DTO>();
	private List<DTO> menusFilterList = new ArrayList<DTO>();

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, MenusActivity.class);
		ServiApplication.shale_menu = false;
		inItUI();

	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.menus);

		lvMenusDish = (ListView) findViewById(R.id.lv_MenusDish);
		btnCreateMenus = (Button) findViewById(R.id.btn_CreateMenus);
		btnCreateDish = (Button) findViewById(R.id.btn_CreateDish);
		btnMenuInventory = (Button) findViewById(R.id.btn_MenuInvantory);
		spnFilter = (Spinner) findViewById(R.id.spn_MenusFilter);

		btnCreateMenus.setText(getResources().getString(R.string.menus));

		btnCreateDish.setOnClickListener(this);
		btnCreateMenus.setOnClickListener(this);
		btnMenuInventory.setOnClickListener(this);
		spnFilter.setOnItemSelectedListener(this);

		loadUI();
		

	}
	// Result of this method,load filter data
	private void loadUI() {
		
		
		dishesList = DishesDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		menusFilterList = MenuDAO.getInstance().getMenusFilterRecords(
				DBHandler.getDBObj(Constants.READABLE));

		dishesAdapter = new DishesAdapter(MenusActivity.this, dishesList,
				ConstantsEnum.VIEW_DISH.code(), appContext, intent);
		lvMenusDish.setAdapter(dishesAdapter);

		filterAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getFilters());
		filterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnFilter.setAdapter(filterAdapter);
		
		spnFilter.setVisibility(View.INVISIBLE);

	}

	private void updateList() {
		dishesAdapter.setListData(dishesList);
		dishesAdapter.notifyDataSetChanged();
	}

	private List<String> getFilters() {
		List<String> list = new ArrayList<String>();

		filterTable = new Hashtable<String, String>();
		list.add(getResources().getString(R.string.menu_filter));
		for (DTO dto : menusFilterList) {
			MenusFilterDTO filterDTO = (MenusFilterDTO) dto;
			if ("1".equals(filterDTO.getMenuTypeId())) {
				if (filterDTO.getDays().contains(filterDTO.getWeekDays())) {
					filterTable.put(filterDTO.getMenuName(),
							filterDTO.getMenuId());
					list.add(filterDTO.getMenuName());
				}
			} else {
				filterTable.put(filterDTO.getMenuName(), filterDTO.getMenuId());
				list.add(filterDTO.getMenuName());
			}
		}
		return list;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_CreateMenus:
			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(MenusActivity.this,
					MenusViewsActivity.class);
			this.finish();
			break;

		case R.id.btn_CreateDish:
			appContext.setSeletedProducts(new ArrayList<DTO>());
			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(MenusActivity.this,
					CreateDishActivity.class);
			this.finish();
			break;

		case R.id.btn_MenuInvantory:
			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(MenusActivity.this,
					MenuInventoryActivity.class);
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long id) {
		if (adapter.getAdapter() == filterAdapter) {
			if (!spnFilter.getSelectedItem().toString().trim()
					.equals(getResources().getString(R.string.menu_filter))) {
				String selectedFilter = spnFilter.getSelectedItem().toString()
						.trim();
				String menuId = filterTable.get(selectedFilter);

				dishesList = DishesDAO.getInstance().getFilterDishes(
						DBHandler.getDBObj(Constants.READABLE), menuId);
				updateList();
			} else {
				dishesList = DishesDAO.getInstance().getRecords(
						DBHandler.getDBObj(Constants.READABLE));
				updateList();
			}

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
}