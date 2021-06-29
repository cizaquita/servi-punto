package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.InvenHistDetailAdapter;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryInvoiceDTO;

public class InventoryDetailHistoryActivity extends BaseActivity {
	private ListView lvInventory;
	private InvenHistDetailAdapter invAdapter;
	private Button btnrecieve, btnHistoric, btnProducts;
	private ServiApplication appContext;
	private Intent intent;
	private TextView txtLabelAdjust, txtInvHeaderTitle, txtUtility,
			txtSellingPrice;
	private RelativeLayout relFilters, relSearch;
	private LinearLayout inventory_hist_search_date;
	List<DTO> prodDetails = new ArrayList<DTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, InventoryDetailHistoryActivity.class);

		inItUI();

	}
	// Result of this method,registration for all UI views.
	private void inItUI() {

		setContentView(R.layout.inventory_activity);
		inventory_hist_search_date = (LinearLayout) findViewById(R.id.inventory_hist_search_date);
		lvInventory = (ListView) findViewById(R.id.lv_Inventory);
		btnHistoric = (Button) findViewById(R.id.btn_history);
		btnrecieve = (Button) findViewById(R.id.btn_recieve);
		btnProducts = (Button) findViewById(R.id.btn_products);

		txtLabelAdjust = (TextView) findViewById(R.id.txt_label_adjust_row);
		txtUtility = (TextView) findViewById(R.id.txt_label_utility_row);
		txtSellingPrice = (TextView) findViewById(R.id.txt_label_price_row);

		txtSellingPrice.setText(getResources().getString(R.string.price));
		txtInvHeaderTitle = (TextView) findViewById(R.id.txt_inv_header_title);
		relFilters = (RelativeLayout) findViewById(R.id.layout_filter);
		relSearch = (RelativeLayout) findViewById(R.id.layout_search);
		txtInvHeaderTitle.setText(getResources().getString(
				R.string.invoice_details));

		relFilters.setVisibility(View.GONE);
		relSearch.setVisibility(View.GONE);
		txtUtility.setVisibility(View.GONE);

		setViewsInvisible();
		getDatafromContext();
	}
	// Result of this method,invisible to the views
	private void setViewsInvisible() {

		btnrecieve.setVisibility(View.GONE);
		btnProducts.setVisibility(View.GONE);
		btnHistoric.setVisibility(View.GONE);
		txtLabelAdjust.setVisibility(View.GONE);
		inventory_hist_search_date.setVisibility(View.GONE);
	}

	private void getDatafromContext() {

		InventoryInvoiceDTO dto = (InventoryInvoiceDTO) appContext
				.getInventoryHistoryList().get(
						Integer.parseInt(appContext.getSeletedSupplier()));
		prodDetails = dto.getProductsList();
		invAdapter = new InvenHistDetailAdapter(this, prodDetails);
		lvInventory.setAdapter(invAdapter);
	}
}