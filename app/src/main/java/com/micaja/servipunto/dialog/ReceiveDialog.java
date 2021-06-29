/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.Hashtable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.micaja.servipunto.R;
import com.micaja.servipunto.R.color;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.AddSupplierActivity;
import com.micaja.servipunto.activities.InventoryActivity;
import com.micaja.servipunto.activities.ProductFinalActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class ReceiveDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnSupplier, btnProduct, btnCancel,btnReceive;
	private Spinner spnSuppliers;
	private ArrayAdapter<String> providerListAdapter;
	ServiApplication appContext;
	private Hashtable<String, String> supplierTable = new Hashtable<String, String>();
	private ImageView imgClose;
	private RelativeLayout layoutHeader;

	public ReceiveDialog(Context context) {

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

		setContentView(R.layout.recieve_dialog);

		btnSupplier = (Button) findViewById(R.id.btn_dialog_supplier);
		btnProduct = (Button) findViewById(R.id.btn_dialog_next);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		spnSuppliers = (Spinner) findViewById(R.id.spn_supplier);
		imgClose = (ImageView) findViewById(R.id.img_close);
		layoutHeader = (RelativeLayout) findViewById(R.id.layout_header);
		btnReceive = (Button) findViewById(R.id.btn_dialog_receive);

		btnSupplier.setOnClickListener(this);
		btnProduct.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);
		btnReceive.setOnClickListener(this);

		loadUI();

	}

	// load screen
	private void loadUI() {

		btnSupplier
				.setBackgroundResource(R.drawable.recharge_menu_modules_innerbtn);
		btnProduct
				.setBackgroundResource(R.drawable.recharge_menu_modules_innerbtn);
		layoutHeader.setBackgroundResource(color.inventory_tab);

		providerListAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, getProviders());
		providerListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnSuppliers.setAdapter(providerListAdapter);

	}
// get providers
	private List<String> getProviders() {
		List<String> list = new ArrayList<String>();

		List<DTO> supplierList = new ArrayList<DTO>();

		supplierList = SupplierDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		list.add(context.getResources().getString(R.string.select));

		for (DTO dto : supplierList) {

			SupplierDTO supplier = (SupplierDTO) dto;
			// list.add(supplier.getName()+" - "+supplier.getCedula());
			// supplierTable.put(supplier.getName()+" - "+supplier.getCedula(),
			// supplier.getCedula());

			list.add(supplier.getName());
			supplierTable.put(supplier.getName(), supplier.getCedula());

		}

		return list;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

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

		case R.id.btn_dialog_next:

			if (spnSuppliers.getSelectedItem().toString().trim()
					.equals(context.getResources().getString(R.string.select))) {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.msg_supplier));
			} else {
				appContext.setSeletedSupplier(supplierTable.get(spnSuppliers
						.getSelectedItem().toString()));
				Intent prodIntent = new Intent(context,
						ProductFinalActivity.class);
				context.startActivity(prodIntent);
			}
			this.dismiss();

			break;

		case R.id.img_close:
			this.dismiss();
			break;

		case R.id.btn_dialog_cancel:
			this.dismiss();
			break;
		case R.id.btn_dialog_receive:
			this.dismiss();
			OrderReciveDialog dialog = new OrderReciveDialog(context);
			dialog.show();
			dialog.setCancelable(false);
			
			break;


		default:
			break;
		}
	}

}
