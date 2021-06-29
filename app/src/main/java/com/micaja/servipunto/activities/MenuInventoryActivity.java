package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.MenusInventoryAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.SalesCodes;

public class MenuInventoryActivity extends BaseActivity implements
		OnClickListener {

	private ListView lvMenusDish;
	private Button btnCreateMenus, btnCreateDish, btnMenuInventory;
	private Spinner spnFilter;

	private TextView txtTitle, txtCount, txtMenuEdit;// txtMenusName,

	List<DTO> menusList = new ArrayList<DTO>();

	private MenusInventoryAdapter menuInvenAdapter;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, MenuInventoryActivity.class);

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
		txtTitle = (TextView) findViewById(R.id.txt_HeaderTitle);
		txtCount = (TextView) findViewById(R.id.txt_MenusPriceLebel);
		txtMenuEdit = (TextView) findViewById(R.id.txt_MenusEditLebel);

		btnCreateDish
				.setText(getResources().getString(R.string.menus_add_dish));
		txtTitle.setText(getResources().getString(
				R.string.menus_inventory_title));
		txtCount.setText(getResources().getString(R.string.menus_count));
		txtMenuEdit.setText(getResources().getString(R.string.adjustment));

		spnFilter.setVisibility(View.INVISIBLE);
		btnCreateMenus.setVisibility(View.INVISIBLE);
		btnMenuInventory.setVisibility(View.INVISIBLE);
		// txtMenuEdit.setVisibility(View.GONE);

		btnCreateDish.setOnClickListener(this);
		btnCreateMenus.setOnClickListener(this);
		btnMenuInventory.setOnClickListener(this);

		loadUI();

	}

	private void loadUI() {

		menusList = MenuInventoryDAO.getInstance().getMenusInventory(
				DBHandler.getDBObj(Constants.READABLE));

		menuInvenAdapter = new MenusInventoryAdapter(
				MenuInventoryActivity.this, menusList, uiHandler);
		lvMenusDish.setAdapter(menuInvenAdapter);
	}

	// This method using for Handler message to update the UI thread
	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.EDIT.code())
				updateList();
		}
	};

	private void updateList() {
		menusList = MenuInventoryDAO.getInstance().getMenusInventory(
				DBHandler.getDBObj(Constants.READABLE));

		menuInvenAdapter.setListData(menusList);
		menuInvenAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_CreateDish:
			appContext.pushActivity(intent);
			appContext.setSeletedProducts(new ArrayList<DTO>());
			CommonMethods.openNewActivity(MenuInventoryActivity.this,
					MenuInventoryAddActivity.class);
			this.finish();
			break;

		default:
			break;
		}
	}
}