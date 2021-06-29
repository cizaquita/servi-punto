package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.InventoryAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDetailsDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.SalesCodes;

public class ProductSeleDishActivity extends BaseActivity implements
		OnItemClickListener {
	private ListView lvInventory;
	private InventoryAdapter invAdapter;
	private Button btnrecieve, btnHistoric, btnProducts;
	private EditText etxtSearch;
	private ImageView imgSearchicon, imgCloseicon,imgFiltericon;
	private Spinner spnFilter;
	private ArrayAdapter<String> filterAdapter;
	private ServiApplication appContext;
	private Intent intent;

	List<DTO> invenProduList = new ArrayList<DTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, ProductSeleDishActivity.class);

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
		imgFiltericon=(ImageView)findViewById(R.id.img_filtericon);

		spnFilter = (Spinner) findViewById(R.id.spn_filter);

		btnHistoric.setVisibility(View.INVISIBLE);
		btnrecieve.setVisibility(View.INVISIBLE);
		btnProducts.setVisibility(View.INVISIBLE);
		etxtSearch.setVisibility(View.INVISIBLE);
		imgSearchicon.setVisibility(View.INVISIBLE);
		imgCloseicon.setVisibility(View.INVISIBLE);
		spnFilter.setVisibility(View.INVISIBLE);
		imgFiltericon.setVisibility(View.INVISIBLE);

		lvInventory.setOnItemClickListener(this);

		getDataFromDB();

	}

	private void getDataFromDB() {
		appContext.setScreen("History");
		invenProduList = InventoryDAO.getInstance()
				.getProductDetailsWithBarcode(
						DBHandler.getDBObj(Constants.READABLE));
		invAdapter = new InventoryAdapter(this, invenProduList);
		lvInventory.setAdapter(invAdapter);
	}

	@Override
	protected void onResume() {
		appContext.setScreen(null);
		super.onResume();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		boolean isAdded = false;
		ProductDetailsDTO productsDTO = (ProductDetailsDTO) invenProduList
				.get(position);
		SelectedProddutsDTO selectedProddutsDTO = (SelectedProddutsDTO) convertDTOs(productsDTO);
		List<DTO> productsList = appContext.getSelectedProducts();
		for (int i = 0; i < productsList.size(); i++) {
			SelectedProddutsDTO dto = (SelectedProddutsDTO) productsList.get(i);
			if (dto.getBarcode().equals(selectedProddutsDTO.getIdProduct())
					|| dto.getIdProduct().equals(
							selectedProddutsDTO.getIdProduct())) {
				int qty = 0;
				if (!dto.getQuantity().equals(""))
					qty = Integer.parseInt(dto.getQuantity()) + 1;
				dto.setQuantity(String.valueOf(qty));

				productsList.set(i, dto);

				isAdded = true;
				break;
			}
		}

		if (!isAdded) {
			productsList.add(selectedProddutsDTO);
			appContext.setSeletedProducts(productsList);
		}
		Intent intent = new Intent();
		setResult(SalesCodes.SELECT_PRODUCT.code(), intent);
		this.finish();

	}

	private DTO convertDTOs(ProductDetailsDTO productsDTO) {
		SelectedProddutsDTO dto = new SelectedProddutsDTO();

		dto.setBarcode(productsDTO.getProductCode());
		dto.setIdProduct(productsDTO.getProductCode());
		dto.setName(productsDTO.getName());
		dto.setPrice(productsDTO.getPurchasePrice());
		dto.setQuantity("1");
		dto.setSellPrice(productsDTO.getSellingPrice());
		dto.setVat(productsDTO.getVat());
		dto.setUnits(productsDTO.getUom());

		return dto;
	}
}