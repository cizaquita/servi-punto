package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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
import com.micaja.servipunto.adapters.DishesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.MenuDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class MenusViewsActivity extends BaseActivity implements OnClickListener {

	private ListView lvMenusDish;
	private Button btnCreateMenus, btnCreateDish, btnInventory;
	private Spinner spnFilter;

	private TextView txtTitle, txtMenusName, txtMenuType;

	List<DTO> dishesList = new ArrayList<DTO>();

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, MenusViewsActivity.class);

		inItUI();

	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.menus);

		lvMenusDish = (ListView) findViewById(R.id.lv_MenusDish);
		btnCreateMenus = (Button) findViewById(R.id.btn_CreateMenus);
		btnCreateDish = (Button) findViewById(R.id.btn_CreateDish);
		btnInventory = (Button) findViewById(R.id.btn_MenuInvantory);
		spnFilter = (Spinner) findViewById(R.id.spn_MenusFilter);
		txtTitle = (TextView) findViewById(R.id.txt_HeaderTitle);
		txtMenusName = (TextView) findViewById(R.id.txt_MenusNameLeble);
		txtMenuType = (TextView) findViewById(R.id.txt_MenusPriceLebel);

		btnCreateDish.setText(getResources().getString(
				R.string.create_menu_titel));
		txtTitle.setText(getResources().getString(R.string.menus));
		txtMenusName.setText(getResources().getString(R.string.menus_name));
		txtMenuType.setText(getResources().getString(R.string.menus_type));

		spnFilter.setVisibility(View.INVISIBLE);
		btnCreateMenus.setVisibility(View.INVISIBLE);
		btnInventory.setVisibility(View.INVISIBLE);

		btnCreateDish.setOnClickListener(this);

		loadUI();

	}

	private void loadUI() {
		dishesList = MenuDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		lvMenusDish
				.setAdapter(new DishesAdapter(MenusViewsActivity.this,
						dishesList, ConstantsEnum.VIEW_MENUS.code(),
						appContext, intent));
	}
	// Result of this method,click listener
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_CreateDish:
			appContext.setSeletedProducts(new ArrayList<DTO>());
			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(MenusViewsActivity.this,
					CreateMenusActivity.class);
			this.finish();
			break;

		default:
			break;
		}
	}
}