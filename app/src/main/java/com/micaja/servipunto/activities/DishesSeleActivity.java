package com.micaja.servipunto.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.DishesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.SalesCodes;

public class DishesSeleActivity extends BaseActivity implements
		OnItemClickListener {

	private ListView lvMenusDish;
	private Button btnCreateMenus, btnCreateDish, btnInventory;
	private Spinner spnFilter;

	private TextView txtTitle;

	List<DTO> dishesList;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, DishesSeleActivity.class);

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

		btnCreateMenus.setVisibility(View.INVISIBLE);
		btnCreateDish.setVisibility(View.INVISIBLE);
		btnInventory.setVisibility(View.INVISIBLE);
		spnFilter.setVisibility(View.INVISIBLE);

		lvMenusDish.setOnItemClickListener(this);

		txtTitle = (TextView) findViewById(R.id.txt_HeaderTitle);
		txtTitle.setText(getResources().getString(R.string.dishes));

		loadUI();

	}

	// Result of this method,loading dish to dish adapter
	private void loadUI() {
		dishesList = DishesDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		lvMenusDish
				.setAdapter(new DishesAdapter(DishesSeleActivity.this,
						dishesList, ConstantsEnum.VIEW_DISH.code(), appContext,
						intent));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		boolean isAdded = false;
		DishesDTO dishesDTO = (DishesDTO) dishesList.get(position);

		if (getIntent().getExtras() != null) {
			appContext.setDishDTO(null);
			appContext.setDishDTO(dishesDTO);
		}
		List<DTO> dishesList = appContext.getMenusDishesList();

		for (int i = 0; i < dishesList.size(); i++) {
			DishesDTO dto = (DishesDTO) dishesList.get(i);
			String dishId = dto.getDishId();
			if (dishId.equals(dishesDTO.getDishId())) { 
				isAdded = true;
				break;
			}
		}
		if (!isAdded) {
			dishesList.add(dishesDTO);
			appContext.setMenusDishesList(dishesList);
		}

		Intent intent = new Intent();
		setResult(SalesCodes.SELECT_DISH.code(), intent);
		this.finish();
	}
}