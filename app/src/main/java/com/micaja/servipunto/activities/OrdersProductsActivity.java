package com.micaja.servipunto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.OrdersProductsAdapter;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class OrdersProductsActivity extends BaseActivity implements
		OnClickListener {
	private Button btnSuggested, btnHistory, btnAddCart, btnPromotions;
	private ListView lvProductList;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		intent = new Intent(this, OrdersProductsActivity.class);
		appContext = (ServiApplication) getApplicationContext();

		inItUI();
	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.orders_supplier_product);

		btnSuggested = (Button) findViewById(R.id.btn_OrdersSuggested);
		btnHistory = (Button) findViewById(R.id.btn_OrdersHistory);
		btnAddCart = (Button) findViewById(R.id.btn_OrdersCart);
		lvProductList = (ListView) findViewById(R.id.lv_SuppProducts);

		btnSuggested.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		btnAddCart.setOnClickListener(this);

		loadUI();

	}
	// Result of this method,load data to the order products list
	private void loadUI() {
		int position = Integer.parseInt(appContext.getSeletedSupplier());
		SupplierDTO dto = (SupplierDTO) appContext.getOrderSupplierList().get(
				position);
		
		lvProductList.setAdapter(new OrdersProductsAdapter(
				OrdersProductsActivity.this, dto.getProductsList()));
		
		if (appContext.getOrder_product()) {
			btnSuggested.setVisibility(View.GONE);
			btnHistory.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_OrdersSuggested:
			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(OrdersProductsActivity.this,
					SuggestedOrdersActivity.class);
			this.finish();
			break;

		case R.id.btn_OrdersHistory:
			appContext.pushActivity(intent);
			appContext.setSeletedSupplier(String.valueOf(Integer
					.parseInt(appContext.getSeletedSupplier())));
			CommonMethods.openNewActivity(OrdersProductsActivity.this,
					OrdersHistoryActivity.class);
			this.finish();
			break;

		case R.id.btn_OrdersCart:

			if (appContext.getSelectedProducts().size() == 0)
				CommonMethods.showCustomToast(this,
						getResources().getString(R.string.noproducts_msg));
			else {
				appContext.pushActivity(intent);
				CommonMethods.openNewActivity(OrdersProductsActivity.this,
						OrderFinalActivity.class);
				this.finish();
			}
			break;


		default:
			break;
		}
	}
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		CommonMethods.openNewActivity(OrdersProductsActivity.this,
//				OrdersActivity.class);
//	}
}
