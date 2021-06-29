/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.SalesInventoryAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.SalesCodes;

public class SalesInventoryActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private ListView lvInventory;
	private SalesInventoryAdapter invAdapter;
	private Button btnrecieve, btnHistoric, btnProducts;
	private EditText etxtSearch;
	private ImageView imgSearchicon, imgCloseicon, imgFiltericon;
	private Spinner spnFilter;
	private ArrayAdapter<String> filterAdapter;
	private ServiApplication appContext;
	private TextView txtLableAdjustRow;
	private Intent intent;
	private boolean isSearch = false;
	List<DTO> updatedList = new ArrayList<DTO>();

	List<DTO> prodDetails = new ArrayList<DTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, SalesInventoryActivity.class);

		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {

		setContentView(R.layout.inventory_activity);

		lvInventory = (ListView) findViewById(R.id.lv_Inventory);
		btnHistoric = (Button) findViewById(R.id.btn_history);
		btnrecieve = (Button) findViewById(R.id.btn_recieve);
		btnProducts = (Button) findViewById(R.id.btn_products);

		etxtSearch = (EditText) findViewById(R.id.etxt_search);
		imgSearchicon = (ImageView) findViewById(R.id.img_searchicon);
		imgCloseicon = (ImageView) findViewById(R.id.img_closeicon);
		txtLableAdjustRow = (TextView) findViewById(R.id.txt_label_adjust_row);

		spnFilter = (Spinner) findViewById(R.id.spn_filter);
		imgFiltericon = (ImageView) findViewById(R.id.img_filtericon);

		btnrecieve.setOnClickListener(this);
		btnHistoric.setOnClickListener(this);
		btnProducts.setOnClickListener(this);

		imgSearchicon.setOnClickListener(this);
		imgCloseicon.setOnClickListener(this);

		imgSearchicon.setVisibility(View.VISIBLE);

		btnrecieve.setVisibility(View.GONE);
		btnHistoric.setVisibility(View.GONE);
		btnProducts.setVisibility(View.GONE);
		spnFilter.setVisibility(View.GONE);
		imgFiltericon.setVisibility(View.GONE);
		txtLableAdjustRow.setVisibility(View.GONE);
		// img_filtericon

		lvInventory.setOnItemClickListener(this);
		lvInventory.setEnabled(true);

		etxtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				imgCloseicon.setVisibility(View.INVISIBLE);
				imgSearchicon.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		loadUI();
		getDataFromDB();

	}

	private void loadUI() {

		filterAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getFilters());
		filterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnFilter.setAdapter(filterAdapter);
	}

	private List<String> getFilters() {

		List<String> list = new ArrayList<String>();

		list.add(getResources().getString(R.string.spn_all));
		list.add(getResources().getString(R.string.spn_exhausted));

		return list;
	}
// get data from inventory table in db
	private void getDataFromDB() {
		prodDetails = InventoryDAO.getInstance().getSalesProductsDetails(
				DBHandler.getDBObj(Constants.READABLE));
		invAdapter = new SalesInventoryAdapter(this, prodDetails);
		lvInventory.setAdapter(invAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.img_searchicon:
			isSearch = true;
			refreshList();
			break;

		case R.id.img_closeicon:
			etxtSearch.setText("");
			getDataFromDB();
			break;

		default:
			break;
		}
	}

	// Result of this method,Search the product based on Validations
	private void refreshList() {
		// prodDetails =
		// InventoryDAO.getInstance().getSalesProductsDetails(DBHandler.getDBObj(Constants.READABLE));
		updatedList.clear();
		for (DTO dto : prodDetails) {
			SelectedProddutsDTO detailDTO = (SelectedProddutsDTO) dto;
			String str = etxtSearch.getText().toString().trim();
			if (detailDTO.getBarcode().contains(str)
					|| detailDTO.getName().toLowerCase()
							.contains(str.toLowerCase()))
				updatedList.add(detailDTO);

		}
		if (etxtSearch.getText().toString().trim().length() > 2) {
			lvInventory
					.setAdapter(new SalesInventoryAdapter(this, updatedList));
			if (updatedList.size() != 0)

				lvInventory.setAdapter(new SalesInventoryAdapter(this,
						updatedList));
			else {
				lvInventory.setAdapter(new SalesInventoryAdapter(this,
						updatedList));
				CommonMethods.showCustomToast(SalesInventoryActivity.this,
						getResources().getString(R.string.nodata));
			}

		} else {
			CommonMethods.showCustomToast(SalesInventoryActivity.this,
					getResources().getString(R.string.search_warning));
		}
		if (etxtSearch.getText().toString().trim().length() > 0) {

			imgCloseicon.setVisibility(View.VISIBLE);
			imgSearchicon.setVisibility(View.INVISIBLE);
		}
		keyBoardDis();
	}

	// keybord disable
	private void keyBoardDis() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etxtSearch.getWindowToken(), 0);
	}

	@Override
	protected void onResume() {
		appContext.setScreen(null);
		super.onResume();

	}

	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {

		boolean isExists = false;
		SelectedProddutsDTO dto;
		if (isSearch)
			dto = (SelectedProddutsDTO) updatedList.get(position);
		else
			dto = (SelectedProddutsDTO) prodDetails.get(position);

		List<DTO> selectedproducts = appContext.getSelectedProducts();
		System.out.println("Product Id in List :" + dto.getIdProduct());
		if (selectedproducts == null) {
			selectedproducts = new ArrayList<DTO>();
			appContext.setSeletedProducts(selectedproducts);

		}
		for (int i = 0; i < selectedproducts.size(); i++) {
			SelectedProddutsDTO prodDTO = (SelectedProddutsDTO) selectedproducts
					.get(i);
			String prodID = prodDTO.getIdProduct();

			if (prodID.equals(dto.getIdProduct())) {
				int qty = 0;
				if (!prodDTO.getQuantity().equals(""))
					qty = Integer.parseInt(prodDTO.getQuantity()) + 1;
				dto.setQuantity(String.valueOf(qty));
				selectedproducts.set(i, dto);
				isExists = true;
				break;
			}
		}
		if (!isExists) {
			dto.setQuantity("1");
			selectedproducts.add(dto);
		}

		Intent intent = new Intent();

		setResult(SalesCodes.PRODUCT_CODE.code(), intent);
		finish();

	}

}