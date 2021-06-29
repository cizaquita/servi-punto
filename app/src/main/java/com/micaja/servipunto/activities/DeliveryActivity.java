/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;

import com.micaja.servipunto.adapters.DeliveryAdapter;
import com.micaja.servipunto.database.DBHandler;


import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dto.AddDeliveryScreenData;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.SalesCodes;

import java.util.ArrayList;
import java.util.List;

public class DeliveryActivity extends BaseActivity implements OnClickListener,
		OnItemSelectedListener {

	private ListView lvDelivery;
	private List<DTO> deliveryList = new ArrayList<DTO>();
	private DeliveryAdapter adapter;
	private EditText etxtSearch;
	private ImageView imgSearchicon;
	private ImageView imgCloseicon;
	private Spinner spnFilter;
	private ArrayAdapter<String> filterAdapter;

	private Button btnAddClient;

	ServiApplication appContext;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, DeliveryActivity.class);
		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.delivery_activity);

		etxtSearch = (EditText) findViewById(R.id.etxt_search);
		lvDelivery = (ListView) findViewById(R.id.lv_Client);
		imgSearchicon = (ImageView) findViewById(R.id.img_searchicon);
		imgCloseicon = (ImageView) findViewById(R.id.img_closeicon);

		spnFilter = (Spinner) findViewById(R.id.spn_filter);
		spnFilter.setOnItemSelectedListener(this);

		btnAddClient = (Button) findViewById(R.id.btn_addclient);

		imgSearchicon.setOnClickListener(this);
		imgCloseicon.setOnClickListener(this);

		btnAddClient.setOnClickListener(this);

		imgSearchicon.setVisibility(View.VISIBLE);
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
		// if (((GetAllusers) new
		// GetAllusers(ClientsActivity.this)).get_users_data()) {
		getDataFromDB();
		// }

	}

	private void loadUI() {

		filterAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getFilters());
		filterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnFilter.setAdapter(filterAdapter);
	}

	// Result of this method, sort by filter based on Date and Payment
	private List<String> getFilters() {
		List<String> list = new ArrayList<String>();

		list.add(getResources().getString(R.string.spn_select));

		list.add(getResources().getString(R.string.spn_date));
		list.add(getResources().getString(R.string.spn_payment));

		return list;
	}

	private void getDataFromDB() {

		deliveryList = DeliveryDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		setDataToViews();

	}

	private OnClickListener mListItemClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (appContext.mClientActivityBackStack != null
					&& appContext.mClientActivityBackStack instanceof SalesActivity) {
				try {
					appContext.setDeliveryDTO((DeliveryDTO) adapter
							.getItem((Integer) v.getTag()));
					setResult(SalesCodes.CLIENT_CODE.code(), null);
					finish();
				} catch (Exception e) {
				}
			}

		}
	};

	private void setDataToViews() {

		deliveryList = DeliveryDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		if (deliveryList.size() > 0) {
			adapter = new DeliveryAdapter(this, deliveryList);
			adapter.setItemListener(mListItemClickListener);
			lvDelivery.setAdapter(adapter);
			lvDelivery.invalidateViews();
		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.nodata));
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.img_searchicon:
			refreshList();
			break;

		case R.id.img_closeicon:

			etxtSearch.setText("");
			getDataFromDB();
			break;

		case R.id.btn_addclient:
			appContext.mAddDeliveryScreenData = new AddDeliveryScreenData();
			appContext.mAddDeliveryScreenData.mScreenData = null;
			appContext.mAddDeliveryScreenData.mBackStackClass = DeliveryActivity.class;
			appContext.pushActivity(intent);
			Intent intent = new Intent(this, AddDeliveryActivity.class);
			intent.putExtra(ConstantsEnum.CLIENT_MODE.code(),
					ConstantsEnum.ADD_CLIENT.code());
			startActivity(intent);
			finish();

			break;

		default:
			break;
		}

	}

	// Result of this method,Search the product based on Validations
	private void refreshList() {

		if (etxtSearch.length() > 2) {
			List<DTO> updatedList = new ArrayList<DTO>();
			updatedList = DeliveryDAO.getInstance().getSearchRecords(
					DBHandler.getDBObj(Constants.READABLE),
					etxtSearch.getText().toString().trim());
			if (updatedList.size() != 0)
				lvDelivery.setAdapter(new DeliveryAdapter(this, updatedList));
			else {
				lvDelivery.setAdapter(new DeliveryAdapter(this, updatedList));
				CommonMethods.showCustomToast(DeliveryActivity.this,
						getResources().getString(R.string.nodata));
			}

		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.search_warning));
		}
		if (etxtSearch.length() > 0) {
			imgCloseicon.setVisibility(View.VISIBLE);
			imgSearchicon.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int id,
			long arg3) {

		if (!parent.getItemAtPosition(id).toString()
				.equals(getResources().getString(R.string.spn_select)))

			filterResultList(parent.getItemAtPosition(id).toString());

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void filterResultList(String selectedItem) {
		if (selectedItem.equals(getResources().getString(R.string.spn_date)))
			deliveryList = DeliveryDAO.getInstance().getFilterRecords(
					DBHandler.getDBObj(Constants.READABLE), "DATE");
		else
			deliveryList = DeliveryDAO.getInstance().getFilterRecords(
					DBHandler.getDBObj(Constants.READABLE), "PAYMENT");

		lvDelivery.setAdapter(new DeliveryAdapter(this, deliveryList));

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(DeliveryActivity.this, MenuActivity.class);
		startActivity(intent);
		this.finish();
	}

}
