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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.InventoryAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.ProductDetailsDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.dialog.ReceiveDialog;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class InventoryActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private ListView lvInventory;
	private InventoryAdapter invAdapter;
	private Button btnrecieve, btnHistoric, btnProducts;
	private EditText etxtSearch;
	private ImageView imgSearchicon, imgCloseicon;
	private Button btnFilter;
	private ServiApplication appContext;
	private Intent intent;
	private Hashtable<String, String> groupTable;



	List<DTO> prodDetails = new ArrayList<DTO>();
	public SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, InventoryActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		inItUI();
		appContext.setOrder_reciveinvoice("");
		ServiApplication.orederReceivedata.clear();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {

		setContentView(R.layout.inventory_screen);
		// inventory_hist_search_date=(LinearLayout)findViewById(R.id.inventory_hist_search_date);
		lvInventory = (ListView) findViewById(R.id.lv_Inventory);
		btnHistoric = (Button) findViewById(R.id.btn_history);
		btnrecieve = (Button) findViewById(R.id.btn_recieve);
		btnProducts = (Button) findViewById(R.id.btn_products);

		etxtSearch = (EditText) findViewById(R.id.etxt_search);
		imgSearchicon = (ImageView) findViewById(R.id.img_searchicon);
		imgCloseicon = (ImageView) findViewById(R.id.img_closeicon);

		btnFilter = (Button) findViewById(R.id.btn_filter);
		btnFilter.setText(getResources().getString(R.string.spn_all));

		btnrecieve.setOnClickListener(this);
		btnHistoric.setOnClickListener(this);
		btnProducts.setOnClickListener(this);
		imgSearchicon.setOnClickListener(this);
		imgCloseicon.setOnClickListener(this);

		imgSearchicon.setVisibility(View.VISIBLE);

		lvInventory.setOnItemClickListener(this);
		btnFilter.setOnClickListener(this);

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

		// loadUI();
		getDataFromDB();

	}

	// Result of this method,loading data to Inventory Adapter
	private void getDataFromDB() {

		prodDetails = InventoryDAO.getInstance().getProductDetailsWithBarcode(
				DBHandler.getDBObj(Constants.READABLE));
		invAdapter = new InventoryAdapter(this, prodDetails);
		lvInventory.setAdapter(invAdapter);
		ServiApplication.setPrevius(1);
		ServiApplication.setNext(1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.img_searchicon:
			refreshList();
			break;

		case R.id.img_closeicon:
			etxtSearch.setText("");
			deafultList();
			break;

		case R.id.btn_recieve:
			appContext.pushActivity(intent);
			ReceiveDialog dialog = new ReceiveDialog(this);
			dialog.show();

			break;

		case R.id.btn_products:

			appContext.pushActivity(intent);
			Intent prodIntent = new Intent(this, ProductsActivity.class);
			prodIntent.putExtra(ConstantsEnum.PRODUCT_MODE.code(),
					ConstantsEnum.ADD_PRODUCT.code());
			startActivity(prodIntent);

			break;
		case R.id.btn_history:

			appContext.pushActivity(intent);
			appContext.setScreen("History");
			CommonMethods.startIntent(this, InventoryHistoryActivity.class);
			ServiApplication.inventory_history_flag = false;
			break;

		case R.id.btn_filter:
			showFilters();
			break;

		default:
			break;
		}
	}

	private void deafultList() {
		List<DTO> updatedList = new ArrayList<DTO>();
		prodDetails = InventoryDAO.getInstance().getProductDetailsWithBarcode(
				DBHandler.getDBObj(Constants.READABLE));

		for (DTO dto : prodDetails) {
			ProductDetailsDTO detailDTO = (ProductDetailsDTO) dto;
			String str = etxtSearch.getText().toString().trim();
			if (detailDTO.getProductCode().contains(str)
					|| detailDTO.getName().toLowerCase()
							.contains(str.toLowerCase())) {
				updatedList.add(detailDTO);
				lvInventory.setAdapter(new InventoryAdapter(this, updatedList));
			}

		}
	}

	// Result of this method,Search the product based on Validations
	private void refreshList() {

		List<DTO> updatedList = new ArrayList<DTO>();
		prodDetails = InventoryDAO.getInstance().getProductDetailsWithBarcode(
				DBHandler.getDBObj(Constants.READABLE));

		SupplierDTO supplierlist = SupplierDAO.getInstance().getRecordsByName(
				DBHandler.getDBObj(Constants.READABLE),
				etxtSearch.getText().toString().trim());
		String supplier_id = supplierlist.getCedula();
		try {
			supplier_id = supplierlist.getCedula();
		} catch (Exception e) {
			supplier_id = "bnm";
		}

		for (DTO dto : prodDetails) {
			ProductDetailsDTO detailDTO = (ProductDetailsDTO) dto;
			String str = etxtSearch.getText().toString().trim();
			if (detailDTO.getProductCode().contains(str)
					|| detailDTO.getName().toLowerCase().contains(str.toLowerCase())
					|| detailDTO.getSupplierName().equals(supplier_id)) {
				updatedList.add(detailDTO);
			}

		}
		if (etxtSearch.getText().toString().trim().length() > 2) {
			if (updatedList.size() != 0)
				lvInventory.setAdapter(new InventoryAdapter(this, updatedList));
			else {
				lvInventory.setAdapter(new InventoryAdapter(this, updatedList));
				CommonMethods.showCustomToast(InventoryActivity.this,
						getResources().getString(R.string.nodata));
			}
		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.search_warning));
		}
		if (etxtSearch.getText().toString().trim().length() > 0) {
			imgCloseicon.setVisibility(View.VISIBLE);
			imgSearchicon.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	protected void onResume() {
		appContext.setScreen(null);
		super.onResume();
		if (CommonMethods.progressDialog != null)
			CommonMethods.dismissProgressDialog();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	// Result of this method,filter the product
	private void filterResultList(String group) {
		List<DTO> list = null;
		if ("".equals(group))
			list = InventoryDAO.getInstance().getProductDetailsExhausted(
					DBHandler.getDBObj(Constants.READABLE));
		else
			list = InventoryDAO.getInstance().getProductDetailsGroup(
					DBHandler.getDBObj(Constants.READABLE), group);

		lvInventory.setAdapter(new InventoryAdapter(this, list));

		if (list.size() == 0)
			CommonMethods.showCustomToast(InventoryActivity.this,
					getResources().getString(R.string.nodata));
	}

	private void getTotalInventory() {
		getDataFromDB();
	}

	private void getTotalInventoryWithOrderByLessInventory() {
		prodDetails = InventoryDAO.getInstance()
				.getProductDetailsWithLessInventory(
						DBHandler.getDBObj(Constants.READABLE));
		invAdapter = new InventoryAdapter(this, prodDetails);
		lvInventory.setAdapter(invAdapter);

		lvInventory.setAdapter(invAdapter);

	}

	// Result of this method,getting products to the next to expire
	private void getTotalInventoryWithOrderNextToExpireInventory() {

		prodDetails = InventoryDAO.getInstance().getProductDetailsWithnextexp(
				DBHandler.getDBObj(Constants.READABLE));
		invAdapter = new InventoryAdapter(this, prodDetails);
		lvInventory.setAdapter(invAdapter);

	}

	// Result of this method,Filter with options
	private void showFilters() {
		final Dialog dialog = new Dialog(InventoryActivity.this);
		dialog.setContentView(R.layout.custom_filters);
		dialog.setTitle(getResources().getString(R.string.inven_filters));

		RelativeLayout layout1 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterAll);
		RelativeLayout layout2 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterExhausted);
		RelativeLayout layout3 = (RelativeLayout) dialog
				.findViewById(R.id.rel_GroupLine);
		final RelativeLayout layout4 = (RelativeLayout) dialog
				.findViewById(R.id.rel_AutoComplete);
		RelativeLayout layout5 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterSubgroup);
		final RelativeLayout layout6 = (RelativeLayout) dialog
				.findViewById(R.id.rel_AutoComplete2);

		RelativeLayout layout7 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterProductExpire);
		RelativeLayout layout8 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterLessInventory);

		final RadioButton radio1 = (RadioButton) dialog
				.findViewById(R.id.radio_All);
		final RadioButton radio2 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterExhausted);
		final RadioButton radio3 = (RadioButton) dialog
				.findViewById(R.id.radio_GroupLine);
		final RadioButton radio4 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterSubgroup);

		final RadioButton radio5 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterProductExpire);
		final RadioButton radio6 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterLessInventory);

		final AutoCompleteTextView actv = (AutoCompleteTextView) dialog
				.findViewById(R.id.ac_Group);

		final AutoCompleteTextView actv2 = (AutoCompleteTextView) dialog
				.findViewById(R.id.ac_Group);

		ArrayAdapter adapter = new ArrayAdapter(InventoryActivity.this,
				android.R.layout.simple_list_item_1, getGroups());
		actv.setAdapter(adapter);

		String selected = btnFilter.getText().toString().trim();

		if (selected.equals(getResources().getString(R.string.spn_all))) {
			radio1.setChecked(true);
		} else if (selected.equals(getResources().getString(
				R.string.spn_exhausted))) {
			radio2.setChecked(true);
		} else if (selected.equals(getResources().getString(R.string.subgroup))) {
			radio4.setChecked(true);
		} else if (selected.equals(getResources().getString(
				R.string.product_expire))) {
			radio5.setChecked(true);

		} else if (selected.equals(getResources().getString(
				R.string.product_linventory))) {
			radio6.setChecked(true);
		} else {
			radio3.setChecked(true);
			layout4.setVisibility(View.VISIBLE);
			actv.setText(selected);
			actv.dismissDropDown();
		}
		radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources()
							.getString(R.string.spn_all));
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					dialog.dismiss();
					getTotalInventory();
				}
			}
		});
		radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.spn_exhausted));
					radio1.setChecked(false);
					radio3.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					dialog.dismiss();
					filterResultList("");
				}
			}
		});
		radio3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.group_line));
					radio1.setChecked(false);
					radio2.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					radio6.setChecked(false);
					layout4.setVisibility(View.VISIBLE);
					layout6.setVisibility(View.GONE);

				}
			}
		});
		radio4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.subgroup));
					radio4.setChecked(true);
					radio1.setChecked(false);
					radio2.setChecked(false);
					radio3.setChecked(false);
					layout6.setVisibility(View.VISIBLE);
					layout4.setVisibility(View.GONE);

				}
			}
		});
		radio5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.product_expire));
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio1.setChecked(false);
					radio4.setChecked(false);
					radio6.setChecked(false);
					dialog.dismiss();
					getTotalInventoryWithOrderNextToExpireInventory();
				}
			}
		});
		radio6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.product_linventory));
					radio2.setChecked(false);
					radio3.setChecked(false);
					radio1.setChecked(false);
					radio4.setChecked(false);
					radio5.setChecked(false);
					dialog.dismiss();
					getTotalInventoryWithOrderByLessInventory();
				}
			}
		});
		layout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(true);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				dialog.dismiss();
				getTotalInventory();
			}
		});

		layout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(false);
				radio2.setChecked(true);
				radio3.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				dialog.dismiss();
				filterResultList("");
			}
		});

		layout3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio3.setChecked(true);
				layout4.setVisibility(View.VISIBLE);
				radio2.setChecked(false);
				radio1.setChecked(false);
				radio4.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
			}
		});
		layout5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio4.setChecked(true);
				radio2.setChecked(false);
				radio1.setChecked(false);
				radio3.setChecked(false);
				radio5.setChecked(false);
				radio6.setChecked(false);
				dialog.dismiss();
			}
		});
		layout7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio4.setChecked(false);
				radio2.setChecked(false);
				radio1.setChecked(false);
				radio3.setChecked(false);
				radio5.setChecked(true);
				radio6.setChecked(false);
				dialog.dismiss();
				getTotalInventoryWithOrderNextToExpireInventory();
			}
		});
		layout8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio4.setChecked(false);
				radio2.setChecked(false);
				radio1.setChecked(false);
				radio3.setChecked(false);
				radio6.setChecked(true);
				getTotalInventoryWithOrderByLessInventory();
			}
		});

		Button btnOK = (Button) dialog.findViewById(R.id.btn_OK);
		// if button is clicked, close the custom dialog
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String group = actv.getText().toString().trim();
				if ("".equals(group))
					CommonMethods.showCustomToast(InventoryActivity.this,
							getResources().getString(R.string.group_error_msg));
				else {
					btnFilter.setText(group);
					String groupID = groupTable.get(group);
					dialog.dismiss();
					filterResultList(groupID);
				}
			}
		});

		Button btnOK2 = (Button) dialog.findViewById(R.id.btn_OK2);
		btnOK2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String subgroup = actv2.getText().toString().trim();
				if ("".equals(subgroup))
					CommonMethods.showCustomToast(
							InventoryActivity.this,
							getResources().getString(
									R.string.subgroup_error_msg));
				else {
					btnFilter.setText(subgroup);
					// String groupID = subgroupTable.get(subgroup);
					// dialog.dismiss();
					// filterResultList(groupID);
				}
			}
		});

		dialog.show();
	}

	private List<String> getGroups() {
		groupTable = new Hashtable<String, String>();
		List<String> groupList = new ArrayList<String>();
		List<DTO> list = GroupDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		for (int i = 0; i < list.size(); i++) {
			GroupDTO groupDTO = (GroupDTO) list.get(i);
			groupList.add(groupDTO.getName());
			groupTable.put(groupDTO.getName(), groupDTO.getGroupId());
		}

		return groupList;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		CommonMethods.openNewActivity(InventoryActivity.this,
				MenuActivity.class);
	}

}