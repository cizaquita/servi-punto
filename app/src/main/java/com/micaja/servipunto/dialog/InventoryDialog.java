/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.AddSupplierActivity;
import com.micaja.servipunto.activities.InventoryActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class InventoryDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnProvider, btnProduct, btnCancel;
	private Spinner spnProviders;
	private ArrayAdapter<String> providerListAdapter;
	ServiApplication appContext;

	public InventoryDialog(Context context) {

		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}
	// Result of this method,registration for all UI views.
	private void initUI() {

		setContentView(R.layout.inventory_dialog);

		btnProvider = (Button) findViewById(R.id.btn_dialog_supplier);
		btnProduct = (Button) findViewById(R.id.btn_dialog_product);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		spnProviders = (Spinner) findViewById(R.id.spn_provider);

		btnProvider.setOnClickListener(this);
		btnProduct.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		loadUI();

	}

	private void loadUI() {

		providerListAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, getProviders());
		providerListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnProviders.setAdapter(providerListAdapter);

	}

	private List<String> getProviders() {
		List<String> list = new ArrayList<String>();

		List<DTO> supplierList = new ArrayList<DTO>();

		supplierList = SupplierDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		list.add(context.getResources().getString(R.string.select));

		for (DTO dto : supplierList) {

			SupplierDTO supplier = (SupplierDTO) dto;
			list.add(supplier.getName());

		}

		return list;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_dialog_supplier:
			Intent intent = new Intent(context, AddSupplierActivity.class);
			Intent intent1 = new Intent(context, InventoryActivity.class);
			appContext.pushActivity(intent1);
			intent.putExtra(ConstantsEnum.SUPPLIER_MODE.code(),
					ConstantsEnum.INVENTORY.code());
			context.startActivity(intent);
			break;

		case R.id.btn_dialog_product:

			if (spnProviders.getSelectedItem().toString().trim()
					.equals(context.getResources().getString(R.string.select))) {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.select_supplier));
			} else {
				CommonMethods.showCustomToast(context,
						"Navigate to product screen");
			}
			break;

		case R.id.btn_dialog_cancel:
			// appContext.popActivity();
			this.dismiss();
			break;

		default:
			break;
		}
	}

}
