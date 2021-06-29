/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDetailsDTO;
import com.micaja.servipunto.dialog.InventoryAdjustDialog;
import com.micaja.servipunto.dialog.OnDialogSaveClickListener1;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class InventoryAdapter extends BaseAdapter implements
		OnDialogSaveClickListener1 {

	private Context context;
	private List<DTO> inventoryResultsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private ServiApplication appContext;

	public InventoryAdapter(Context context, List<DTO> inventoryResultsList) {
		this.context = context;
		this.inventoryResultsList = inventoryResultsList;
		appContext = (ServiApplication) context.getApplicationContext();

	}

	@Override
	public int getCount() {
		return inventoryResultsList.size();
	}

	public void setData(List<DTO> inventoryResultsList) {
		this.inventoryResultsList = inventoryResultsList;
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.inventory_row, null);
		}

		final TextView productCode = (TextView) convertView
				.findViewById(R.id.txt_inv_prodcode);
		final TextView productName = (TextView) convertView
				.findViewById(R.id.txt_inv_prodname);

		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_inv_quantity);
		final TextView sellingprice = (TextView) convertView
				.findViewById(R.id.txt_inv_sellingprice);

		final TextView utilityValue = (TextView) convertView
				.findViewById(R.id.txt_inv_utility);
		final ImageView adjust = (ImageView) convertView
				.findViewById(R.id.img_inv_adjustment);

		if (appContext.getScreen() != null)
			adjust.setVisibility(View.GONE);
		final ProductDetailsDTO inventory = (ProductDetailsDTO) inventoryResultsList
				.get(position);

		productCode.setText(inventory.getProductCode());
		productName.setText(inventory.getName());

		qty.setText(inventory.getQuantity());
		sellingprice.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(inventory.getSellingPrice())));

		utilityValue.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(inventory.getUtilityValue())));
		adjust.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				InventoryAdjustDialog dialog = new InventoryAdjustDialog(
						context, inventory);
				dialog.setOnDialogSaveListener(InventoryAdapter.this);
				dialog.show();

			}
		});

		return convertView;
	}

	@Override
	public void onSaveClick() {

		List<DTO> prodDetails = InventoryDAO.getInstance()
				.getProductDetailsWithBarcode(
						DBHandler.getDBObj(Constants.READABLE));

		this.setData(prodDetails);
		InventoryAdapter.this.notifyDataSetChanged();

	}

}
