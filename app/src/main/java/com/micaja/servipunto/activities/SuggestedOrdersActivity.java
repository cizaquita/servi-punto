package com.micaja.servipunto.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.OrdersSuggestedAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class SuggestedOrdersActivity extends BaseActivity {
	private Button btnSuggested, btnHistory, btnAddCart;
	private ListView lvProductList;
	private TextView txtTitle;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		intent = new Intent(this, OrdersProductsActivity.class);

		appContext = (ServiApplication) getApplicationContext();

		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.orders_history);

		btnSuggested = (Button) findViewById(R.id.btn_OrdersSuggested);
		btnHistory = (Button) findViewById(R.id.btn_OrdersHistory);
		btnAddCart = (Button) findViewById(R.id.btn_OrdersCart);
		txtTitle = (TextView) findViewById(R.id.txt_HeaderTitle);

		txtTitle.setText(getResources().getString(R.string.suggested_orders));

		lvProductList = (ListView) findViewById(R.id.lv_OrdersHistory);

		btnSuggested.setVisibility(View.GONE);
		btnHistory.setVisibility(View.GONE);
		btnAddCart.setVisibility(View.GONE);
		loadUI();
	}

	private void loadUI() {
		int position = Integer.parseInt(appContext.getSeletedSupplier());
		SupplierDTO dto = (SupplierDTO) appContext.getOrderSupplierList().get(
				position);

		List<DTO> suggestedList = InventoryDAO.getInstance()
				.getSuggestedOrders(DBHandler.getDBObj(Constants.READABLE),
						dto.getSupplierId());
		lvProductList.setAdapter(new OrdersSuggestedAdapter(
				SuggestedOrdersActivity.this, suggestedList));

		if (suggestedList.size() == 0)
			CommonMethods.showCustomToast(SuggestedOrdersActivity.this,
					getResources().getString(R.string.nodata));
	}
}
